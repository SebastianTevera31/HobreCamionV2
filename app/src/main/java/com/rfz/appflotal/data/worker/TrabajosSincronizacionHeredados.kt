package com.rfz.appflotal.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rfz.appflotal.data.repository.sincronizacion.ColaSincronizacionRepository
import com.rfz.appflotal.data.repository.sincronizacion.OperacionesLlantaRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/*
 * Puentes temporales: WorkManager guarda el nombre de la clase de los trabajos que encoló la
 * versión anterior. Se conservan una versión para pasar su trabajo a la cola nueva; después se borran.
 */

/** El montaje está en assembly_tire_table, que no se migró; aquí se encola. */
@HiltWorker
class SyncCreateAssemblyTireWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val operaciones: OperacionesLlantaRepository,
    private val cola: ColaSincronizacionRepository,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val posicion = inputData.getString("assemblyTirePosition") ?: return Result.success()
        return runCatching {
            operaciones.encolarMontajeHeredado(posicion)
            cola.programarEnvio()
        }.fold(onSuccess = { Result.success() }, onFailure = { Result.retry() })
    }
}

/** La migración 34→35 ya pasó el desmontaje a la cola; solo falta programar el envío. */
@HiltWorker
class SyncDisassemblyTireWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val cola: ColaSincronizacionRepository,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        cola.programarEnvio()
        return Result.success()
    }
}

/** La migración 34→35 ya pasó la inspección a la cola; solo falta programar el envío. */
@HiltWorker
class SyncInspectionTireWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val cola: ColaSincronizacionRepository,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        cola.programarEnvio()
        return Result.success()
    }
}
