package com.rfz.appflotal.data.repository.maintenance

import com.rfz.appflotal.data.model.catalog.MaintenanceNotification
import com.rfz.appflotal.data.model.catalog.MaintenanceNotificationEntity
import com.rfz.appflotal.data.model.catalog.MaintenanceNotificationResponse
import java.util.concurrent.TimeUnit

private val DAY_MILLIS = TimeUnit.DAYS.toMillis(1)

/**
 * Catálogo local tras una respuesta exitosa del endpoint: solo quedan los ids que este expone
 * (los demás se eliminan). Conserva las fechas de los ya conocidos para no reiniciar su ciclo.
 * Se descartan filas sin mensaje o con días no positivos.
 */
fun mergeMaintenanceCatalog(
    remote: List<MaintenanceNotificationResponse>,
    local: List<MaintenanceNotificationEntity>,
    now: Long
): List<MaintenanceNotificationEntity> {
    val localById = local.associateBy { it.id }
    return remote
        .distinctBy { it.id }
        .mapNotNull { response ->
            val message = response.message?.trim().orEmpty()
            if (message.isEmpty() || response.daysConf <= 0) return@mapNotNull null
            val previous = localById[response.id]
            MaintenanceNotificationEntity(
                id = response.id,
                message = message,
                daysConf = response.daysConf,
                firstSeenAt = previous?.firstSeenAt ?: now,
                lastNotifiedAt = previous?.lastNotifiedAt,
                acknowledgedAt = previous?.acknowledgedAt
            )
        }
}

/** Ciclo fijo: [MaintenanceNotificationEntity.daysConf] días desde la última emisión. */
fun MaintenanceNotificationEntity.nextDueAt(): Long =
    (lastNotifiedAt ?: firstSeenAt) + daysConf.toLong() * DAY_MILLIS

fun MaintenanceNotificationEntity.isDue(now: Long): Boolean = now >= nextDueAt()

/** Emitido y sin "Enterado" posterior a esa emisión. */
fun MaintenanceNotificationEntity.isPending(): Boolean {
    val notifiedAt = lastNotifiedAt ?: return false
    return acknowledgedAt == null || acknowledgedAt < notifiedAt
}

fun dueMaintenanceNotifications(
    notifications: List<MaintenanceNotificationEntity>,
    now: Long
): List<MaintenanceNotificationEntity> = notifications.filter { it.isDue(now) }

/** Pendientes de la bandeja, la más reciente primero. */
fun pendingMaintenanceNotifications(
    notifications: List<MaintenanceNotificationEntity>
): List<MaintenanceNotification> = notifications
    .filter { it.isPending() }
    .sortedByDescending { it.lastNotifiedAt }
    .map { MaintenanceNotification(id = it.id, message = it.message, notifiedAt = it.lastNotifiedAt!!) }
