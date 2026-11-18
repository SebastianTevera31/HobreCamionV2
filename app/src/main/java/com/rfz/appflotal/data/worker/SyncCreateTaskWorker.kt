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
import com.rfz.appflotal.data.repository.promotions.PromotionsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

/** Sincronizaciones de promociones bajo demanda (no las diarias), para cancelarlas al cerrar sesión. */
internal const val PROMOTIONS_ON_DEMAND_TAG = "PromotionsOnDemandSync"

@HiltWorker
class SyncDailyPromotionsWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val promotionsRepository: PromotionsRepository
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            // Sin sesión activa de plan Complete el repositorio termina con éxito sin tocar la red,
            // y el trabajo espera a la siguiente ventana programada.
            val result = promotionsRepository.syncDiscounts()

            if (result.isSuccess) {
                Result.success()
            } else {
                // Si falla el servidor, WorkManager lo reintentará según la política de backoff
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

fun schedulePromotionsWorker(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val dailyWorkRequest = PeriodicWorkRequestBuilder<SyncDailyPromotionsWorker>(1, TimeUnit.DAYS)
        .setConstraints(constraints)
        .setBackoffCriteria(
            BackoffPolicy.EXPONENTIAL,
            WorkRequest.MIN_BACKOFF_MILLIS,
            TimeUnit.MILLISECONDS
        )
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "DailyPromotionsWork",
        ExistingPeriodicWorkPolicy.KEEP,
        dailyWorkRequest
    )
}

/**
 * Fuerza una sincronización inmediata (fuera del ciclo diario), para el caso en que
 * el periodic work ya haya corrido una vez sin sesión activa (p. ej. en el primer
 * arranque de la app antes de iniciar sesión) — con KEEP, ese periodic work no vuelve
 * a ejecutarse hasta su siguiente ventana de 24h, dejando el cache vacío hasta entonces.
 */
fun triggerPromotionsSync(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val syncRequest = OneTimeWorkRequestBuilder<SyncDailyPromotionsWorker>()
        .setConstraints(constraints)
        .addTag(PROMOTIONS_ON_DEMAND_TAG)
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        "PromotionsSyncNow",
        ExistingWorkPolicy.REPLACE,
        syncRequest
    )
}

/**
 * Al cerrar sesión: cancela las sincronizaciones de promociones pendientes o en curso (aborta la
 * llamada a GetDiscounts que esté en vuelo). Los trabajos diarios siguen programados, pero sin
 * sesión Complete terminan sin tocar la red, y uno en curso se detiene antes de su siguiente llamada.
 */
fun cancelOnDemandPromotionsSyncs(context: Context) {
    WorkManager.getInstance(context).cancelAllWorkByTag(PROMOTIONS_ON_DEMAND_TAG)
}
