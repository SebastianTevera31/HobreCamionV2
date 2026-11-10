package com.rfz.appflotal.data.repository.maintenance

import com.rfz.appflotal.data.model.catalog.MaintenanceNotification
import com.rfz.appflotal.data.model.catalog.MaintenanceNotificationEntity
import com.rfz.appflotal.data.model.catalog.MaintenanceNotificationResponse
import java.util.concurrent.TimeUnit

private val DAY_MILLIS = TimeUnit.DAYS.toMillis(1)

/** `id_notifications` de "alinear ruedas"; el endpoint no trae su límite de kilometraje. */
const val WHEEL_ALIGNMENT_NOTIFICATION_ID = 2
private const val WHEEL_ALIGNMENT_KM = 120_000

/** Kilometraje que adelanta el recordatorio [id], o null si solo vence por días. */
fun kmConfFor(id: Int): Int? =
    if (id == WHEEL_ALIGNMENT_NOTIFICATION_ID) WHEEL_ALIGNMENT_KM else null

/** Odómetro (km) utilizable: 0 o negativo significa que aún no hay captura. */
fun validOdometerKm(odometerKm: Int?): Int? = odometerKm?.takeIf { it > 0 }

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
                acknowledgedAt = previous?.acknowledgedAt,
                baselineOdometerKm = previous?.baselineOdometerKm
            )
        }
}

/** Ciclo fijo: [MaintenanceNotificationEntity.daysConf] días desde la última emisión. */
fun MaintenanceNotificationEntity.nextDueAt(): Long =
    (lastNotifiedAt ?: firstSeenAt) + daysConf.toLong() * DAY_MILLIS

fun MaintenanceNotificationEntity.isDueByDays(now: Long): Boolean = now >= nextDueAt()

/** La unidad recorrió el límite de kilometraje desde el odómetro de referencia del ciclo. */
fun MaintenanceNotificationEntity.isDueByKm(odometerKm: Int?): Boolean {
    val kmConf = kmConfFor(id) ?: return false
    val baseline = baselineOdometerKm ?: return false
    val current = validOdometerKm(odometerKm) ?: return false
    return current - baseline >= kmConf
}

/** Vence por lo que ocurra primero: los días o, si tiene regla, el kilometraje. */
fun MaintenanceNotificationEntity.isDue(now: Long, odometerKm: Int?): Boolean =
    isDueByDays(now) || isDueByKm(odometerKm)

/**
 * Odómetro de referencia antes de evaluar: el primer odómetro válido inicia la referencia, y si
 * el odómetro bajó (cambio de unidad o corrección) se reinicia al actual. Sin regla de
 * kilometraje o sin odómetro válido no cambia nada.
 */
fun MaintenanceNotificationEntity.withOdometerBaseline(odometerKm: Int?): MaintenanceNotificationEntity {
    if (kmConfFor(id) == null) return this
    val current = validOdometerKm(odometerKm) ?: return this
    val baseline = baselineOdometerKm
    return if (baseline == null || current < baseline) copy(baselineOdometerKm = current) else this
}

/**
 * Registro de la emisión: el ciclo de días y el de kilometraje se reinician juntos, sea cual
 * sea la condición que lo venció. Sin odómetro válido conserva la referencia anterior.
 */
fun MaintenanceNotificationEntity.emittedAt(
    now: Long,
    odometerKm: Int?
): MaintenanceNotificationEntity {
    val current = validOdometerKm(odometerKm)
    return copy(
        lastNotifiedAt = now,
        baselineOdometerKm = if (kmConfFor(id) != null && current != null) current else baselineOdometerKm
    )
}

/** Emitido y sin "Enterado" posterior a esa emisión. */
fun MaintenanceNotificationEntity.isPending(): Boolean {
    val notifiedAt = lastNotifiedAt ?: return false
    return acknowledgedAt == null || acknowledgedAt < notifiedAt
}

/** Resultado de evaluar el catálogo: filas a guardar y las que se emiten en esta evaluación. */
data class MaintenanceEvaluation(
    val updated: List<MaintenanceNotificationEntity>,
    val emitted: List<MaintenanceNotificationEntity>
)

/**
 * Ajusta las referencias de odómetro, decide qué vence y registra la emisión. [updated] solo
 * incluye las filas que cambiaron.
 */
fun evaluateMaintenanceNotifications(
    notifications: List<MaintenanceNotificationEntity>,
    now: Long,
    odometerKm: Int?
): MaintenanceEvaluation {
    val updated = mutableListOf<MaintenanceNotificationEntity>()
    val emitted = mutableListOf<MaintenanceNotificationEntity>()
    notifications.forEach { original ->
        val adjusted = original.withOdometerBaseline(odometerKm)
        val result = if (adjusted.isDue(now, odometerKm)) {
            adjusted.emittedAt(now, odometerKm).also { emitted += it }
        } else {
            adjusted
        }
        if (result != original) updated += result
    }
    return MaintenanceEvaluation(updated = updated, emitted = emitted)
}

/** Pendientes de la bandeja, la más reciente primero. */
fun pendingMaintenanceNotifications(
    notifications: List<MaintenanceNotificationEntity>
): List<MaintenanceNotification> = notifications
    .filter { it.isPending() }
    .sortedByDescending { it.lastNotifiedAt }
    .map {
        MaintenanceNotification(id = it.id, message = it.message, notifiedAt = it.lastNotifiedAt!!)
    }
