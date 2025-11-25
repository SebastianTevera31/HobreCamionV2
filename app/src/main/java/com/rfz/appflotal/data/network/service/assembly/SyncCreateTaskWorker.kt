package com.rfz.appflotal.data.network.service.assembly

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rfz.appflotal.data.model.assembly.toDto
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
        val position = inputData.getString("assemblyTirePosition")
            ?: return Result.failure()
        val token = inputData.getString("token") ?: return Result.failure()

        val record = local.getAssemblyTire(position)

        val result = remote.pushAssemblyTire(
            token = token,
            assemblyTire = record.toDto()
        )

        return if (result.isSuccessful) Result.success() else Result.failure()
    }
}