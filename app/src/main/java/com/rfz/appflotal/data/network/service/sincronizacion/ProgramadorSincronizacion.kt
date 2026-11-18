package com.rfz.appflotal.data.network.service.sincronizacion

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.rfz.appflotal.data.worker.SincronizarOperacionesWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val TRABAJO_SINCRONIZAR_OPERACIONES = "sincronizar-operaciones"

class ProgramadorSincronizacion @Inject constructor(
    private val workManager: WorkManager,
) {
    /** APPEND: si ya corre un envío, se encadena otro para no perder lo que se agregó mientras. */
    fun programar() {
        val trabajo = OneTimeWorkRequestBuilder<SincronizarOperacionesWorker>()
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniqueWork(
            TRABAJO_SINCRONIZAR_OPERACIONES,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            trabajo
        )
    }
}
