package com.rfz.appflotal.data.network.service.assembly

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.rfz.appflotal.data.model.assembly.AssemblyTire
import javax.inject.Inject

class AssemblySyncScheduler @Inject constructor(private val workManager: WorkManager) {
    fun enqueueCreate(assemblyTire: AssemblyTire, token: String) {
        val inputData = workDataOf(
            "assemblyTirePosition" to assemblyTire.positionTire,
            "token" to token
        )

        val work = OneTimeWorkRequestBuilder<SyncCreateAssemblyTireWorker>()
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .setInputData(inputData)
            .build()

        workManager.enqueue(work)
    }
}