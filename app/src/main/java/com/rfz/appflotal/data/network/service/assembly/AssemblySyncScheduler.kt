package com.rfz.appflotal.data.network.service.assembly

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.rfz.appflotal.data.model.assembly.AssemblyTire
import com.rfz.appflotal.data.worker.SyncCreateAssemblyTireWorker
import com.rfz.appflotal.data.worker.SyncDisassemblyTireWorker
import com.rfz.appflotal.data.worker.SyncInspectionTireWorker
import javax.inject.Inject

class AssemblySyncScheduler @Inject constructor(
    private val workManager: WorkManager
) {
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

    fun enqueueDisassembly(positionTire: String, token: String) {
        val inputData = workDataOf(
            "position" to positionTire,
            "token" to token
        )

        val work = OneTimeWorkRequestBuilder<SyncDisassemblyTireWorker>()
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .setInputData(inputData)
            .build()

        workManager.enqueue(work)
    }

    fun enqueueInspection(positionTire: String) {
        val inputData = workDataOf(
            "position" to positionTire
        )

        val work = OneTimeWorkRequestBuilder<SyncInspectionTireWorker>()
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .setInputData(inputData)
            .build()

        workManager.enqueue(work)
    }
}