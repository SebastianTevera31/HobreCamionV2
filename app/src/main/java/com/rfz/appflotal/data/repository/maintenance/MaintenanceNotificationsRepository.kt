package com.rfz.appflotal.data.repository.maintenance

import com.rfz.appflotal.core.util.MaintenanceNotifier
import com.rfz.appflotal.data.dao.MaintenanceNotificationsDao
import com.rfz.appflotal.data.model.catalog.MaintenanceNotification
import com.rfz.appflotal.data.network.service.catalog.RemoteCatalogDataSource
import com.rfz.appflotal.data.repository.fcmessaging.toSafePaymentPlanType
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MaintenanceNotificationsRepository @Inject constructor(
    private val remoteCatalogDataSource: RemoteCatalogDataSource,
    private val maintenanceNotificationsDao: MaintenanceNotificationsDao,
    private val maintenanceNotifier: MaintenanceNotifier,
    private val getTasksUseCase: GetTasksUseCase
) {

    /** Solo un usuario con sesión y plan Complete consulta el endpoint y recibe recordatorios. */
    suspend fun isEnabledForUser(): Boolean {
        val user = getTasksUseCase().first().firstOrNull() ?: return false
        return user.fld_token.isNotBlank() &&
                user.paymentPlan.toSafePaymentPlanType() == PaymentPlanType.Complete
    }

    /**
     * Reemplaza el catálogo local con el del endpoint; los ids que ya no expone se eliminan
     * junto con su notificación. Si la consulta falla, el catálogo local no se toca.
     */
    suspend fun syncCatalog(): Result<Unit> =
        remoteCatalogDataSource.getMaintenanceNotifications().map { remote ->
            val removedIds = maintenanceNotificationsDao.applyCatalog(remote, System.currentTimeMillis())
            maintenanceNotifier.cancel(removedIds)
        }

    /**
     * Registra en la bandeja los recordatorios vencidos y publica su notificación si hay
     * permiso; sin permiso igual quedan pendientes en la bandeja.
     */
    suspend fun emitDueNotifications() {
        val due = maintenanceNotificationsDao.takeDue(System.currentTimeMillis())
        maintenanceNotifier.notify(pendingMaintenanceNotifications(due))
    }

    fun observePending(): Flow<List<MaintenanceNotification>> =
        maintenanceNotificationsDao.observeAll().map(::pendingMaintenanceNotifications)

    /** "Enterado": lo saca de la bandeja sin mover su ciclo. */
    suspend fun acknowledge(id: Int) {
        maintenanceNotificationsDao.acknowledge(id, System.currentTimeMillis())
        maintenanceNotifier.cancel(listOf(id))
    }

    /** Al cerrar sesión: el siguiente usuario empieza su conteo desde cero. */
    suspend fun clearAll() {
        maintenanceNotificationsDao.clearAll()
        maintenanceNotifier.cancelAll()
    }
}
