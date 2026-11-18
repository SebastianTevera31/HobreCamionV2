package com.rfz.appflotal.data.repository.sincronizacion

import com.rfz.appflotal.data.model.database.AppHCEntity
import com.rfz.appflotal.data.model.sincronizacion.OperacionPendiente
import com.rfz.appflotal.data.model.sincronizacion.ResultadoEnvio
import com.rfz.appflotal.data.model.sincronizacion.toDomain
import com.rfz.appflotal.data.network.service.sincronizacion.EnviadoresOperacion
import com.rfz.appflotal.data.network.service.sincronizacion.LocalOperacionesLlantaDataSource
import com.rfz.appflotal.data.network.service.sincronizacion.ProgramadorSincronizacion
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/** Cola de operaciones pendientes de enviar al servidor. */
interface ColaSincronizacionRepository {
    /** La más antigua enviable de la sesión actual; null si no hay o no hay sesión. */
    suspend fun siguienteParaEnviar(): OperacionPendiente?
    suspend fun obtener(id: Long): OperacionPendiente?
    suspend fun marcarEnviando(id: Long)
    suspend fun enviar(id: Long): ResultadoEnvio

    /** null si no se puede saber (sin red o el servidor no lo expone). */
    suspend fun yaAplicada(id: Long): Boolean?
    suspend fun marcarAceptada(id: Long)
    suspend fun marcarRechazada(id: Long, mensaje: String?)
    suspend fun registrarFalloConexion(id: Long)
    suspend fun restablecerPendiente(id: Long)

    /** Quita la operación y las de su grupo (la inspección de un desmontaje). */
    suspend fun descartar(id: Long)
    fun programarEnvio()
}

class ColaSincronizacionRepositoryImpl @Inject constructor(
    private val local: LocalOperacionesLlantaDataSource,
    private val enviadores: EnviadoresOperacion,
    private val programador: ProgramadorSincronizacion,
    private val getTasksUseCase: GetTasksUseCase,
) : ColaSincronizacionRepository {

    override suspend fun siguienteParaEnviar(): OperacionPendiente? {
        val usuario = sesion() ?: return null
        return local.siguienteParaEnviar(usuario.idUser)?.toDomain()
    }

    override suspend fun obtener(id: Long): OperacionPendiente? = local.obtener(id)?.toDomain()

    override suspend fun marcarEnviando(id: Long) = local.marcarEnviando(id)

    override suspend fun enviar(id: Long): ResultadoEnvio {
        val usuario = sesion() ?: return ResultadoEnvio.SesionVencida
        // Si ya no existe (se descartó mientras tanto) no hay nada que enviar.
        val operacion = local.obtener(id) ?: return ResultadoEnvio.Aceptada
        return enviadores.para(operacion.tipo).enviar(usuario.fld_token, operacion.payload)
    }

    override suspend fun yaAplicada(id: Long): Boolean? {
        val usuario = sesion() ?: return null
        val operacion = local.obtener(id) ?: return null
        return enviadores.para(operacion.tipo)
            .yaAplicada(usuario.fld_token, operacion.idMonitor, operacion.payload)
    }

    override suspend fun marcarAceptada(id: Long) = local.eliminar(id)

    override suspend fun marcarRechazada(id: Long, mensaje: String?) =
        local.marcarRechazada(id, mensaje)

    override suspend fun registrarFalloConexion(id: Long) = local.sumarIntento(id)

    override suspend fun restablecerPendiente(id: Long) = local.restablecerPendiente(id)

    override suspend fun descartar(id: Long) = local.eliminarConGrupo(id)

    override fun programarEnvio() = programador.programar()

    private suspend fun sesion(): AppHCEntity? = getTasksUseCase().first().firstOrNull()
}
