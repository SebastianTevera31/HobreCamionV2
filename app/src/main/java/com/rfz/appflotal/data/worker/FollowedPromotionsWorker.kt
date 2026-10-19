package com.rfz.appflotal.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.rfz.appflotal.core.util.PromotionNotifier
import com.rfz.appflotal.data.repository.promotions.FollowedPromotionsRepository
import com.rfz.appflotal.data.repository.promotions.FollowedSyncScope
import com.rfz.appflotal.data.repository.promotions.isAuthError
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

private const val FOLLOWED_PROMOTIONS_PERIODIC_WORK = "FollowedPromotionsWork"
private const val FOLLOWED_PROMOTIONS_PENDING_WORK = "FollowedPromotionsSyncPending"
private const val FOLLOWED_ACCESSORY_REFRESH_WORK_PREFIX = "FollowedAccessoryRefresh-"
private const val MAX_SYNC_ATTEMPTS = 3

private const val KEY_SCOPE = "scope"
private const val KEY_ACCESSORY_ID = "followedAccessoryId"
private const val SCOPE_PENDING = "pending"
private const val SCOPE_SINGLE = "single"

/**
 * Busca promociones de los accesorios que sigue el usuario y le notifica las nuevas.
 * Sin sesión o sin accesorios por consultar termina sin tocar la red.
 */
@HiltWorker
class FollowedPromotionsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val followedPromotionsRepository: FollowedPromotionsRepository,
    private val promotionNotifier: PromotionNotifier
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val scope = inputData.toSyncScope()
        return followedPromotionsRepository.syncFollowedPromotions(scope).fold(
            onSuccess = { newPromotions ->
                // En "Buscar ahora" el usuario ya ve los resultados (con "Nuevo") en pantalla.
                if (scope !is FollowedSyncScope.Single) {
                    promotionNotifier.notifyNewPromotions(newPromotions)
                }
                Result.success()
            },
            onFailure = { error ->
                when {
                    // Token vencido: se espera a la siguiente ventana en lugar de reintentar.
                    error.isAuthError() -> Result.success()
                    // "Buscar ahora": la pantalla muestra el error y el usuario decide si reintenta.
                    scope is FollowedSyncScope.Single -> Result.failure()
                    runAttemptCount + 1 >= MAX_SYNC_ATTEMPTS -> Result.failure()
                    else -> Result.retry()
                }
            }
        )
    }
}

private fun Data.toSyncScope(): FollowedSyncScope = when (getString(KEY_SCOPE)) {
    SCOPE_PENDING -> FollowedSyncScope.PendingFirstSync
    SCOPE_SINGLE -> FollowedSyncScope.Single(getLong(KEY_ACCESSORY_ID, -1))
    // El trabajo diario no lleva datos de entrada.
    else -> FollowedSyncScope.All
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
        ExistingPeriodicWorkPolicy.UPDATE,
        dailyWorkRequest
    )
}

/**
 * Primera búsqueda de los accesorios recién seguidos, en cuanto haya red. APPEND_OR_REPLACE
 * encadena tras una búsqueda en curso (que pudo leer la lista antes del nuevo seguido); cada
 * eslabón consulta solo los que sigan pendientes, así que seguir varios seguidos no repite
 * llamadas por los ya consultados.
 */
fun triggerPendingFollowedPromotionsSync(context: Context) {
    val syncRequest = OneTimeWorkRequestBuilder<FollowedPromotionsWorker>()
        .setConstraints(followedPromotionsConstraints())
        .setInputData(workDataOf(KEY_SCOPE to SCOPE_PENDING))
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        FOLLOWED_PROMOTIONS_PENDING_WORK,
        ExistingWorkPolicy.APPEND_OR_REPLACE,
        syncRequest
    )
}

/** Nombre del trabajo de "Buscar ahora" de un accesorio, para observar su estado. */
fun followedAccessoryRefreshWorkName(followedAccessoryId: Long): String =
    "$FOLLOWED_ACCESSORY_REFRESH_WORK_PREFIX$followedAccessoryId"

/**
 * "Buscar ahora" de un accesorio. KEEP ignora los toques repetidos mientras haya una búsqueda
 * pendiente o en curso; una vez terminada, el siguiente toque vuelve a buscar.
 */
fun triggerFollowedAccessoryRefresh(context: Context, followedAccessoryId: Long) {
    val refreshRequest = OneTimeWorkRequestBuilder<FollowedPromotionsWorker>()
        .setConstraints(followedPromotionsConstraints())
        .setInputData(
            workDataOf(
                KEY_SCOPE to SCOPE_SINGLE,
                KEY_ACCESSORY_ID to followedAccessoryId
            )
        )
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        followedAccessoryRefreshWorkName(followedAccessoryId),
        ExistingWorkPolicy.KEEP,
        refreshRequest
    )
}
