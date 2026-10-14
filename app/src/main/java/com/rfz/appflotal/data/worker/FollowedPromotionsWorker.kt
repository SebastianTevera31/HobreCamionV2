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
import com.rfz.appflotal.core.util.PromotionNotifier
import com.rfz.appflotal.data.repository.promotions.FollowedPromotionsRepository
import com.rfz.appflotal.data.repository.promotions.isAuthError
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

private const val FOLLOWED_PROMOTIONS_PERIODIC_WORK = "FollowedPromotionsWork"
private const val FOLLOWED_PROMOTIONS_SYNC_NOW_WORK = "FollowedPromotionsSyncNow"
private const val MAX_SYNC_ATTEMPTS = 3

/**
 * Busca promociones de los accesorios que sigue el usuario y le notifica las nuevas.
 * Sin sesión o sin accesorios seguidos termina sin tocar la red.
 */
@HiltWorker
class FollowedPromotionsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val followedPromotionsRepository: FollowedPromotionsRepository,
    private val promotionNotifier: PromotionNotifier
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return followedPromotionsRepository.syncFollowedPromotions().fold(
            onSuccess = { newPromotions ->
                promotionNotifier.notifyNewPromotions(newPromotions)
                Result.success()
            },
            onFailure = { error ->
                when {
                    // Token vencido: se espera a la siguiente ventana en lugar de reintentar.
                    error.isAuthError() -> Result.success()
                    runAttemptCount + 1 >= MAX_SYNC_ATTEMPTS -> Result.failure()
                    else -> Result.retry()
                }
            }
        )
    }
}

private fun followedPromotionsConstraints() = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .build()

fun scheduleFollowedPromotionsWorker(context: Context) {
    val dailyWorkRequest = PeriodicWorkRequestBuilder<FollowedPromotionsWorker>(1, TimeUnit.DAYS)
        .setConstraints(followedPromotionsConstraints())
        .setBackoffCriteria(
            BackoffPolicy.EXPONENTIAL,
            WorkRequest.MIN_BACKOFF_MILLIS,
            TimeUnit.MILLISECONDS
        )
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        FOLLOWED_PROMOTIONS_PERIODIC_WORK,
        ExistingPeriodicWorkPolicy.KEEP,
        dailyWorkRequest
    )
}

/**
 * Sincroniza en cuanto haya red, p. ej. al seguir un accesorio nuevo. APPEND_OR_REPLACE
 * encadena tras una sincronización en curso en lugar de cancelarla.
 */
fun triggerFollowedPromotionsSync(context: Context) {
    val syncRequest = OneTimeWorkRequestBuilder<FollowedPromotionsWorker>()
        .setConstraints(followedPromotionsConstraints())
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        FOLLOWED_PROMOTIONS_SYNC_NOW_WORK,
        ExistingWorkPolicy.APPEND_OR_REPLACE,
        syncRequest
    )
}
