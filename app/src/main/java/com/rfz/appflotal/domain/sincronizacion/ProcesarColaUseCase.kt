package com.rfz.appflotal.domain.sincronizacion

import com.rfz.appflotal.data.model.sincronizacion.EstadoOperacion
import com.rfz.appflotal.data.model.sincronizacion.ResultadoEnvio
import com.rfz.appflotal.data.model.sincronizacion.ResultadoProcesamiento
import com.rfz.appflotal.data.repository.assembly.AssemblyTireRepository
import com.rfz.appflotal.data.repository.sincronizacion.ColaSincronizacionRepository
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Envía la cola en el orden en que se registró. Un error de conexión detiene todo para no
 * alterar el orden; un rechazo solo detiene las operaciones siguientes de esa posición.
 */
@Singleton
class ProcesarColaUseCase @Inject constructor(
    private val cola: ColaSincronizacionRepository,
    private val montajes: AssemblyTireRepository,
) {
    // Worker y pantalla comparten la cola; el que llega segundo no espera, el que corre toma lo nuevo.
    private val candado = Mutex()

    suspend operator fun invoke(): ResultadoProcesamiento {
        if (!candado.tryLock()) return ResultadoProcesamiento.EN_CURSO
        cola.iniciarProceso()
        // Si algo falla a medias, se trata como error de conexión: el worker reintenta.
        var resultado = ResultadoProcesamiento.REINTENTAR
        try {
            resultado = recorrerCola()
            return resultado
        } finally {
            cola.terminarProceso(resultado)
            candado.unlock()
        }
    }

    private suspend fun recorrerCola(): ResultadoProcesamiento {
        var huboCambios = false
        while (true) {
            val operacion = cola.siguienteParaEnviar() ?: break
            val id = operacion.id

            // Quedó a medias (se cerró la app o se perdió la respuesta): pudo haber llegado.
            if (operacion.estado == EstadoOperacion.ENVIANDO && cola.yaAplicada(id) == true) {
                cola.marcarAceptada(id)
                huboCambios = true
                continue
            }

            cola.marcarEnviando(id)
            when (val resultado = cola.enviar(id)) {
                ResultadoEnvio.Aceptada -> cola.marcarAceptada(id)

                is ResultadoEnvio.Rechazada ->
                    if (cola.yaAplicada(id) == true) cola.marcarAceptada(id)
                    else cola.marcarRechazada(id, resultado.mensaje)

                // Se queda en ENVIANDO: el servidor pudo recibirla antes de cortarse la conexión.
                ResultadoEnvio.ErrorConexion -> {
                    cola.registrarFalloConexion(id)
                    return ResultadoProcesamiento.REINTENTAR
                }

                ResultadoEnvio.SesionVencida -> {
                    cola.restablecerPendiente(id)
                    return ResultadoProcesamiento.SESION_VENCIDA
                }
            }
            huboCambios = true
        }

        if (huboCambios) {
            cola.registrarSincronizacion(System.currentTimeMillis())
            // Lo que se mostró antes de enviar puede no coincidir con lo que aceptó el servidor.
            runCatching { montajes.refreshMountedTires() }
        }
        return ResultadoProcesamiento.COMPLETA
    }
}
