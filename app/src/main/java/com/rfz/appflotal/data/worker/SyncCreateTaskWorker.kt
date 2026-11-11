package com.rfz.appflotal.data.worker

import android.content.Context
import android.util.Log
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
import com.rfz.appflotal.data.model.assembly.toDto
import com.rfz.appflotal.data.model.disassembly.tire.DisassemblyTire
import com.rfz.appflotal.data.model.disassembly.tire.toDto
import com.rfz.appflotal.data.model.tire.dto.toDto
import com.rfz.appflotal.data.network.service.assembly.LocalAssemblyDataSource
import com.rfz.appflotal.data.network.service.assembly.RemoteAssemblyDataSource
import com.rfz.appflotal.data.network.service.disassembly.LocalDisassemblyDataSource
import com.rfz.appflotal.data.network.service.disassembly.RemoteDisassemblyTireDataSource
import com.rfz.appflotal.data.network.service.tire.InspectionTireCrudService
import com.rfz.appflotal.data.network.service.tire.LocalInspectionDataSource
import com.rfz.appflotal.data.repository.assembly.ASSEMBLY_SUCCESS_ID
import com.rfz.appflotal.data.repository.promotions.PromotionsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

/** Sincronizaciones de promociones bajo demanda (no las diarias), para cancelarlas al cerrar sesión. */
internal const val PROMOTIONS_ON_DEMAND_TAG = "PromotionsOnDemandSync"

@HiltWorker
class SyncCreateAssemblyTireWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val remote: RemoteAssemblyDataSource,
    private val local: LocalAssemblyDataSource,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val position = inputData.getString("assemblyTirePosition") ?: return Result.failure()
        val token = inputData.getString("token") ?: return Result.failure()

        return try {
            val record = local.getAssemblyTire(position).getOrNull() ?: return Result.failure()
            val pushResult = remote.pushAssemblyTire(token, record.toDto())

            // Sin red / error de transporte: se reintenta más tarde.
            if (pushResult.isFailure) return Result.retry()

            val response = pushResult.getOrNull()?.firstOrNull()
            if (response?.id == ASSEMBLY_SUCCESS_ID) return Result.success()

            // HTTP 200 con id != 200: el servidor rechazó el montaje y reintentar no lo arregla.
            // Si un envío anterior sí llegó (p. ej. se perdió la respuesta), el servidor ya lo
            // tiene y el montaje local se conserva; si no, se quita para no mostrarlo montado.
            val mountedOnServer = remote.fetchMountedTire(token, record.idMonitor)
                .getOrNull()
                ?.any { it.positionTire == record.positionTire && it.tireId == record.idTire }
                ?: return Result.retry()

            if (mountedOnServer) {
                Result.success()
            } else {
                Log.w(TAG, "Montaje en $position rechazado: ${response?.message}")
                local.deleteAssemblyTire(position)
                Result.failure()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private companion object {
        const val TAG = "SyncCreateAssembly"
    }
}

@HiltWorker
class SyncDisassemblyTireWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val remote: RemoteDisassemblyTireDataSource,
    private val local: LocalDisassemblyDataSource,
    private val localAssembly: LocalAssemblyDataSource
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val position = inputData.getString("position") ?: return Result.failure()
        val token = inputData.getString("token") ?: return Result.failure()

        return try {
            val record = local.getDisassemblyTire(position) ?: return Result.failure()
            val domainModel = DisassemblyTire(
                disassemblyCause = record.disassemblyCause,
                destination = record.destination,
                dateOperation = record.dateOperation,
                positionTire = record.positionTire,
                odometer = record.odometer
            )
            val result = remote.createDisassemblyTire(token, domainModel.toDto())

            if (result.isSuccess) {
                local.deleteDisassemblyTire(position)
                localAssembly.deleteAssemblyTire(position)
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

@HiltWorker
class SyncInspectionTireWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val remote: InspectionTireCrudService,
    private val local: LocalInspectionDataSource,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val position = inputData.getString("position") ?: return Result.failure()

        return try {
            val record = local.getInspectionTire(position) ?: return Result.failure()
            val response = remote.doInspectionTire(record.toDto())

            if (response.isSuccessful) {
                local.deleteInspectionTire(position)
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

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
