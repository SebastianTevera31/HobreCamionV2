package com.rfz.appflotal.debug

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.rfz.appflotal.data.dao.MaintenanceNotificationsDao
import com.rfz.appflotal.data.model.catalog.MaintenanceNotificationResponse
import com.rfz.appflotal.data.repository.maintenance.MaintenanceNotificationsRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

private const val TAG = "MaintenanceDebug"
private const val ACTION_FORCE_DUE = "com.rfz.appflotal.debug.FORCE_MAINTENANCE_DUE"

// Ejemplos de api/Catalog/Notifications, para cuando aún no se ha descargado el catálogo.
private val SAMPLE_CATALOG = listOf(
    MaintenanceNotificationResponse(
        id = 1,
        message = "You must inspect your vehicle at least once a month.",
        daysConf = 30
    ),
    MaintenanceNotificationResponse(
        id = 2,
        message = "It is recommended to have your truck’s wheels aligned every 120,000 km or according to the manufacturer’s recommendations.",
        daysConf = 240
    )
)

@EntryPoint
@InstallIn(SingletonComponent::class)
interface MaintenanceDebugEntryPoint {
    fun maintenanceNotificationsDao(): MaintenanceNotificationsDao
    fun maintenanceNotificationsRepository(): MaintenanceNotificationsRepository
}

/**
 * Solo debug: adelanta el ciclo de todos los recordatorios para que venzan ahora y corre la
 * emisión real (bandeja, punto de la campana y push). Con el catálogo vacío inserta antes los
 * ejemplos del endpoint. No valida el plan del usuario.
 */
class MaintenanceDebugReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_FORCE_DUE) return

        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            MaintenanceDebugEntryPoint::class.java
        )
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = entryPoint.maintenanceNotificationsDao()
                val now = System.currentTimeMillis()

                if (dao.getAll().isEmpty()) {
                    dao.applyCatalog(SAMPLE_CATALOG, now)
                    Log.i(TAG, "Catálogo vacío: se insertaron ${SAMPLE_CATALOG.size} ejemplos")
                }
                val overdue = dao.getAll().map {
                    it.copy(
                        firstSeenAt = now - TimeUnit.DAYS.toMillis(it.daysConf.toLong()),
                        lastNotifiedAt = null,
                        acknowledgedAt = null
                    )
                }
                dao.upsert(overdue)

                entryPoint.maintenanceNotificationsRepository().emitDueNotifications()
                Log.i(TAG, "Recordatorios emitidos: ${overdue.map { it.id }}")
            } catch (e: Exception) {
                Log.e(TAG, "No se pudieron emitir los recordatorios", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
