package com.rfz.appflotal.domain.sincronizacion

import com.rfz.appflotal.data.model.sincronizacion.EstadoSincronizacion
import com.rfz.appflotal.data.model.sincronizacion.ResultadoProcesamiento
import com.rfz.appflotal.data.model.sincronizacion.operacionesBloqueadas
import com.rfz.appflotal.data.repository.sincronizacion.ColaSincronizacionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ObservarEstadoSincronizacionUseCase @Inject constructor(
    private val cola: ColaSincronizacionRepository,
) {
    operator fun invoke(): Flow<EstadoSincronizacion> =
        combine(cola.observarOperaciones(), cola.observarEstadoEnvio()) { operaciones, envio ->
            EstadoSincronizacion(operaciones, operacionesBloqueadas(operaciones), envio)
        }
}

/** Envía lo que haya en la cola ahora; si algo queda, el worker lo intenta al tener red. */
class SincronizarAhoraUseCase @Inject constructor(
    private val procesarCola: ProcesarColaUseCase,
    private val cola: ColaSincronizacionRepository,
) {
    suspend operator fun invoke(): ResultadoProcesamiento {
        val resultado = procesarCola()
        if (resultado != ResultadoProcesamiento.COMPLETA) cola.programarEnvio()
        return resultado
    }
}

class ReintentarOperacionUseCase @Inject constructor(
    private val cola: ColaSincronizacionRepository,
    private val sincronizarAhora: SincronizarAhoraUseCase,
) {
    suspend operator fun invoke(id: Long): ResultadoProcesamiento {
        cola.reintentar(id)
        return sincronizarAhora()
    }
}
