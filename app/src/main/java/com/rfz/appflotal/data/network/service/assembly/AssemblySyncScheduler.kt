package com.rfz.appflotal.data.network.service.assembly

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.rfz.appflotal.data.model.assembly.AssemblyTire
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AssemblySyncScheduler @Inject constructor(
    private val workManager: WorkManager,
    @param:ApplicationContext private val appContext: Context
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

        val operation = workManager.enqueue(work)

        operation.result.addListener(Runnable {
            // Esto se ejecuta cuando WorkManager termina de *encolar* la tarea (no de ejecutarla)
            Log.d("AssemblySyncScheduler", "Work enqueued successfully: ${work.id}")
        }, ContextCompat.getMainExecutor(appContext))
    }
}