package com.rfz.appflotal.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.rfz.appflotal.data.repository.maintenance.MaintenanceNotificationsRepository
import com.rfz.appflotal.data.repository.promotions.isAuthError
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

private const val MAINTENANCE_PERIODIC_WORK = "MaintenanceNotificationsWork"
private const val MAINTENANCE_SYNC_WORK = "MaintenanceNotificationsSyncNow"
private const val MAX_SYNC_ATTEMPTS = 3

private const val KEY_SYNC_CATALOG = "syncCatalog"

/**
 * Emite los recordatorios de mantenimiento vencidos. El trabajo diario solo evalúa la base
 * local; el de arranque de la app además sincroniza antes el catálogo con el endpoint.
 * Sin sesión o sin plan Complete termina sin hacer nada.
 */
@HiltWorker
class MaintenanceNotificationsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val maintenanceNotificationsRepository: MaintenanceNotificationsRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        if (!maintenanceNotificationsRepository.isEnabledForUser()) return Result.success()

        val syncError = if (inputData.getBoolean(KEY_SYNC_CATALOG, false)) {
            maintenanceNotificationsRepository.syncCatalog().exceptionOrNull()
        } else {
            null
        }

        // Con o sin sincronización, lo ya guardado se evalúa; la emisión es idempotente.
        maintenanceNotificationsRepository.emitDueNotifications()

        return when {
            syncError == null -> Result.success()
            // Token vencido: se espera al siguiente arranque en lugar de reintentar.
            syncError.isAuthError() -> Result.success()
            runAttemptCount + 1 >= MAX_SYNC_ATTEMPTS -> Result.failure()
            else -> Result.retry()
        }
    }
}

/** Evaluación diaria; no requiere red porque no consulta el endpoint. */
fun scheduleMaintenanceNotificationsWorker(context: Context) {
    val dailyWorkRequest =
        PeriodicWorkRequestBuilder<MaintenanceNotificationsWorker>(1, TimeUnit.DAYS).build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        MAINTENANCE_PERIODIC_WORK,
        ExistingPeriodicWorkPolicy.UPDATE,
        dailyWorkRequest
    )
}

/**
 * Sincroniza el catálogo al arrancar la app, en cuanto haya red. KEEP ignora los arranques
 * repetidos mientras haya una sincronización pendiente o en curso.
 */
fun triggerMaintenanceNotificationsSync(context: Context) {
    val syncRequest = OneTimeWorkRequestBuilder<MaintenanceNotificationsWorker>()
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
        .setBackoffCriteria(
            BackoffPolicy.EXPONENTIAL,
            WorkRequest.MIN_BACKOFF_MILLIS,
            TimeUnit.MILLISECONDS
        )
        .setInputData(workDataOf(KEY_SYNC_CATALOG to true))
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        MAINTENANCE_SYNC_WORK,
        ExistingWorkPolicy.KEEP,
        syncRequest
    )
}
