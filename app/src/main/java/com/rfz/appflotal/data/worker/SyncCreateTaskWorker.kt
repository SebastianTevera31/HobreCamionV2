package com.rfz.appflotal.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
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
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

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