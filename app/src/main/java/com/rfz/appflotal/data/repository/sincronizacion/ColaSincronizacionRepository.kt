package com.rfz.appflotal.data.repository.sincronizacion

import com.rfz.appflotal.data.model.database.AppHCEntity
import com.rfz.appflotal.data.model.sincronizacion.EstadoEnvio
import com.rfz.appflotal.data.model.sincronizacion.OperacionPendiente
import com.rfz.appflotal.data.model.sincronizacion.ResultadoEnvio
import com.rfz.appflotal.data.model.sincronizacion.ResultadoProcesamiento
import com.rfz.appflotal.data.model.sincronizacion.toDomain
import com.rfz.appflotal.data.network.service.sincronizacion.EnviadoresOperacion
import com.rfz.appflotal.data.network.service.sincronizacion.LocalMarcaSincronizacionDataSource
import com.rfz.appflotal.data.network.service.sincronizacion.LocalOperacionesLlantaDataSource
import com.rfz.appflotal.data.network.service.sincronizacion.ProgramadorSincronizacion
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
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

    /** Una rechazada vuelve a pendiente para intentar enviarla de nuevo. */
    suspend fun reintentar(id: Long)

    /** Quita la operación y las de su grupo (la inspección de un desmontaje). */
    suspend fun descartar(id: Long)
    fun programarEnvio()

    /** Operaciones de la sesión actual, en el orden en que se enviarán. */
    fun observarOperaciones(): Flow<List<OperacionPendiente>>
    fun observarEstadoEnvio(): Flow<EstadoEnvio>
    fun iniciarProceso()
    fun terminarProceso(resultado: ResultadoProcesamiento)
    suspend fun registrarSincronizacion(momento: Long)
}

class ColaSincronizacionRepositoryImpl @Inject constructor(
    private val local: LocalOperacionesLlantaDataSource,
    private val marca: LocalMarcaSincronizacionDataSource,
    private val enviadores: EnviadoresOperacion,
    private val programador: ProgramadorSincronizacion,
    private val getTasksUseCase: GetTasksUseCase,
) : ColaSincronizacionRepository {

    // Singleton (ver RepositoriesDi): el worker y la pantalla comparten este estado.
    private val proceso = MutableStateFlow(EstadoEnvio())

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

    override suspend fun marcarAceptada(id: Long) = local.eliminarAceptada(id)

    override suspend fun marcarRechazada(id: Long, mensaje: String?) =
        local.marcarRechazada(id, mensaje)

    override suspend fun registrarFalloConexion(id: Long) = local.sumarIntento(id)

    override suspend fun restablecerPendiente(id: Long) = local.restablecerPendiente(id)

    override suspend fun reintentar(id: Long) = local.reintentar(id)

    override suspend fun descartar(id: Long) = local.eliminarConGrupo(id)

    override fun programarEnvio() = programador.programar()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observarOperaciones(): Flow<List<OperacionPendiente>> =
        flow { emitAll(getTasksUseCase()) }.flatMapLatest { usuarios ->
            val usuario = usuarios.firstOrNull() ?: return@flatMapLatest flowOf(emptyList())
            local.observar(usuario.idUser).map { lista -> lista.map { it.toDomain() } }
        }

    override fun observarEstadoEnvio(): Flow<EstadoEnvio> =
        combine(proceso, marca.observar()) { enMemoria, ultima ->
            enMemoria.copy(ultimaSincronizacion = ultima)
        }

    override fun iniciarProceso() = proceso.update { it.copy(procesando = true) }

    override fun terminarProceso(resultado: ResultadoProcesamiento) = proceso.update {
        it.copy(
            procesando = false,
            sesionVencida = when (resultado) {
                ResultadoProcesamiento.SESION_VENCIDA -> true
                ResultadoProcesamiento.COMPLETA -> false
                else -> it.sesionVencida
            }
        )
    }

    override suspend fun registrarSincronizacion(momento: Long) = marca.registrar(momento)

    private suspend fun sesion(): AppHCEntity? = getTasksUseCase().first().firstOrNull()
}
