package com.rfz.appflotal.data.repository.sincronizacion

import com.rfz.appflotal.data.model.assembly.AssemblyTire
import com.rfz.appflotal.data.model.assembly.toDto
import com.rfz.appflotal.data.model.assembly.toEntity
import com.rfz.appflotal.data.model.database.AppHCEntity
import com.rfz.appflotal.data.model.disassembly.tire.DisassemblyTire
import com.rfz.appflotal.data.model.disassembly.tire.toDto
import com.rfz.appflotal.data.model.sincronizacion.OperacionPendienteEntity
import com.rfz.appflotal.data.model.sincronizacion.TipoOperacion
import com.rfz.appflotal.data.model.sincronizacion.gsonPayload
import com.rfz.appflotal.data.model.tire.dto.InspectionTireDto
import com.rfz.appflotal.data.network.service.sincronizacion.LocalOperacionesLlantaDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject

/** Registra montajes, desmontajes e inspecciones en el teléfono; el envío lo hace la cola. */
interface OperacionesLlantaRepository {
    /** Devuelve los ids de las operaciones encoladas. */
    suspend fun registrarMontaje(montaje: AssemblyTire): List<Long>
    suspend fun registrarDesmontaje(inspeccion: InspectionTireDto?, desmontaje: DisassemblyTire): List<Long>
    suspend fun registrarInspeccion(inspeccion: InspectionTireDto): List<Long>

    /** Puente con SyncCreateAssemblyTireWorker: encola un montaje guardado por la versión anterior. */
    suspend fun encolarMontajeHeredado(posicion: String)
}

class OperacionesLlantaRepositoryImpl @Inject constructor(
    private val local: LocalOperacionesLlantaDataSource,
    private val getTasksUseCase: GetTasksUseCase,
) : OperacionesLlantaRepository {

    override suspend fun registrarMontaje(montaje: AssemblyTire): List<Long> {
        val usuario = sesion()
        val entidad = montaje.toEntity().copy(idMonitor = usuario.id_monitor)
        val operacion = nuevaOperacion(TipoOperacion.MONTAJE, entidad.positionTire, entidad.toDto(), usuario)
        return listOf(local.registrarMontaje(entidad, operacion))
    }

    /**
     * La inspección va primero: el servidor la registra sobre la llanta todavía montada. Sin
     * inspección (ya hubo una hoy o la llanta se montó hoy) se encola solo el desmontaje.
     */
    override suspend fun registrarDesmontaje(
        inspeccion: InspectionTireDto?,
        desmontaje: DisassemblyTire
    ): List<Long> {
        val usuario = sesion()
        val grupo = UUID.randomUUID().toString()
        val posicion = desmontaje.positionTire
        val operaciones = listOfNotNull(
            inspeccion?.let { nuevaOperacion(TipoOperacion.INSPECCION, posicion, it, usuario, grupo) },
            nuevaOperacion(TipoOperacion.DESMONTAJE, posicion, desmontaje.toDto(), usuario, grupo)
        )
        return local.registrarDesmontaje(posicion, operaciones)
    }

    override suspend fun registrarInspeccion(inspeccion: InspectionTireDto): List<Long> {
        val operacion = nuevaOperacion(
            TipoOperacion.INSPECCION, inspeccion.positionTire, inspeccion, sesion()
        )
        return listOf(local.registrarOperacion(operacion))
    }

    override suspend fun encolarMontajeHeredado(posicion: String) {
        val montaje = local.obtenerMontaje(posicion) ?: return
        local.registrarOperacion(
            nuevaOperacion(TipoOperacion.MONTAJE, posicion, montaje.toDto(), sesion())
        )
    }

    private suspend fun sesion(): AppHCEntity = getTasksUseCase().first().first()

    private fun nuevaOperacion(
        tipo: TipoOperacion,
        posicion: String,
        cuerpo: Any,
        usuario: AppHCEntity,
        idGrupo: String? = null
    ) = OperacionPendienteEntity(
        idCliente = UUID.randomUUID().toString(),
        tipo = tipo,
        posicion = posicion,
        idMonitor = usuario.id_monitor,
        idUsuario = usuario.idUser,
        idGrupo = idGrupo,
        payload = gsonPayload.toJson(cuerpo),
        creadoEn = System.currentTimeMillis()
    )
}
