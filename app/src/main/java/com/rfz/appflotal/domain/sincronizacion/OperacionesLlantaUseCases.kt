package com.rfz.appflotal.domain.sincronizacion

import com.rfz.appflotal.data.model.assembly.AssemblyTire
import com.rfz.appflotal.data.model.disassembly.tire.DisassemblyTire
import com.rfz.appflotal.data.model.sincronizacion.EstadoOperacion
import com.rfz.appflotal.data.model.sincronizacion.ResultadoOperacion
import com.rfz.appflotal.data.model.tire.dto.InspectionTireDto
import com.rfz.appflotal.data.repository.assembly.AssemblyTireRepository
import com.rfz.appflotal.data.repository.sincronizacion.ColaSincronizacionRepository
import com.rfz.appflotal.data.repository.sincronizacion.OperacionesLlantaRepository
import javax.inject.Inject

class MontarLlantaUseCase @Inject constructor(
    private val operaciones: OperacionesLlantaRepository,
    private val enviar: EnviarOperacionesUseCase,
) {
    suspend operator fun invoke(montaje: AssemblyTire): Result<ResultadoOperacion> =
        runCatching { enviar(operaciones.registrarMontaje(montaje)) }
}

class DesmontarLlantaUseCase @Inject constructor(
    private val operaciones: OperacionesLlantaRepository,
    private val enviar: EnviarOperacionesUseCase,
) {
    /** [inspeccion] es null cuando hoy ya hubo una o la llanta se montó hoy. */
    suspend operator fun invoke(
        inspeccion: InspectionTireDto?,
        desmontaje: DisassemblyTire
    ): Result<ResultadoOperacion> =
        runCatching { enviar(operaciones.registrarDesmontaje(inspeccion, desmontaje)) }
}

class InspeccionarLlantaUseCase @Inject constructor(
    private val operaciones: OperacionesLlantaRepository,
    private val enviar: EnviarOperacionesUseCase,
) {
    suspend operator fun invoke(inspeccion: InspectionTireDto): Result<ResultadoOperacion> =
        runCatching { enviar(operaciones.registrarInspeccion(inspeccion)) }
}

/**
 * Intenta enviar en el momento lo recién registrado. Con red, la pantalla conoce el rechazo
 * del servidor igual que antes; sin red, queda en cola para el worker.
 */
class EnviarOperacionesUseCase @Inject constructor(
    private val procesarCola: ProcesarColaUseCase,
    private val cola: ColaSincronizacionRepository,
    private val descartar: DescartarOperacionUseCase,
) {
    suspend operator fun invoke(ids: List<Long>): ResultadoOperacion {
        procesarCola()
        val restantes = ids.mapNotNull { cola.obtener(it) }
        val rechazada = restantes.firstOrNull { it.estado == EstadoOperacion.CON_ERROR }
        return when {
            restantes.isEmpty() -> ResultadoOperacion.Enviada
            // El usuario ve el motivo en la pantalla; no se deja en la cola, como antes.
            rechazada != null -> {
                descartar(rechazada.id)
                ResultadoOperacion.Rechazada(rechazada.ultimoError)
            }
            else -> {
                cola.programarEnvio()
                ResultadoOperacion.EnCola
            }
        }
    }
}

/** Quita la operación de la cola y deja las llantas como las tiene el servidor. */
class DescartarOperacionUseCase @Inject constructor(
    private val cola: ColaSincronizacionRepository,
    private val montajes: AssemblyTireRepository,
) {
    suspend operator fun invoke(id: Long) {
        cola.descartar(id)
        runCatching { montajes.refreshMountedTires() }
        // Las operaciones que esperaban detrás de esta en su posición ya pueden enviarse.
        cola.programarEnvio()
    }
}
