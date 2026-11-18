package com.rfz.appflotal.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rfz.appflotal.data.model.sincronizacion.ResultadoProcesamiento
import com.rfz.appflotal.domain.sincronizacion.ProcesarColaUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException

@HiltWorker
class SincronizarOperacionesWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val procesarCola: ProcesarColaUseCase,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = try {
        when (procesarCola()) {
            // EN_CURSO: la pantalla ya está enviando y programa otro trabajo si algo queda.
            ResultadoProcesamiento.COMPLETA, ResultadoProcesamiento.EN_CURSO -> Result.success()
            // Sesión vencida: al iniciar sesión de nuevo, el siguiente reintento ya tiene token.
            ResultadoProcesamiento.REINTENTAR, ResultadoProcesamiento.SESION_VENCIDA -> Result.retry()
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.retry()
    }
}
