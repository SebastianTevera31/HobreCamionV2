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
import com.rfz.appflotal.data.repository.promotions.PromotionsRepository
import com.rfz.appflotal.domain.database.GetTasksUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

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
            val result =
                remote.pushAssemblyTire(token, record.toDto().copy(idMonitor = record.idMonitor))
                    .getOrNull()

            if (result?.get(0)?.id == 200) Result.success() else Result.retry()
        } catch (e: Exception) {
            Result.retry()
        }
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
    private val promotionsRepository: PromotionsRepository,
    private val getTasksUseCase: GetTasksUseCase
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        // 1. Verificar si hay una sesión activa (obteniendo el usuario de la DB local)
        val users = getTasksUseCase().first()
        if (users.isEmpty() || users[0].fld_token.isEmpty()) {
            // Si no hay sesión, terminamos con éxito para que se reintente en la siguiente ventana programada
            return Result.success()
        }

        return try {
            // 2. Consumir el endpoint y reemplazar el cache local (el repositorio ya maneja el token internamente)
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
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        "PromotionsSyncNow",
        ExistingWorkPolicy.REPLACE,
        syncRequest
    )
}
