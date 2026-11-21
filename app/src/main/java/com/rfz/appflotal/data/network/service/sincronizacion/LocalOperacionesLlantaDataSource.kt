package com.rfz.appflotal.data.network.service.sincronizacion

import androidx.room.withTransaction
import com.rfz.appflotal.data.dao.AssemblyTireDao
import com.rfz.appflotal.data.dao.OperacionPendienteDao
import com.rfz.appflotal.data.dao.SensorDataDao
import com.rfz.appflotal.data.database.AppHombreCamionDatabase
import com.rfz.appflotal.data.model.assembly.AssemblyTireEntity
import com.rfz.appflotal.data.model.sincronizacion.OperacionPendienteEntity
import com.rfz.appflotal.data.model.sincronizacion.TipoOperacion
import com.rfz.appflotal.data.model.sincronizacion.gsonPayload
import com.rfz.appflotal.data.model.tire.dto.InspectionTireDto
import com.rfz.appflotal.di.dispatchers.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** Estado local de las llantas y cola de envíos; lo que cambia ambos va en una transacción. */
class LocalOperacionesLlantaDataSource @Inject constructor(
    private val baseDatos: AppHombreCamionDatabase,
    private val operacionDao: OperacionPendienteDao,
    private val montajeDao: AssemblyTireDao,
    private val sensorDao: SensorDataDao,
    @IoDispatcher private val io: CoroutineDispatcher,
) {
    suspend fun registrarMontaje(
        montaje: AssemblyTireEntity,
        operacion: OperacionPendienteEntity
    ): Long = withContext(io) {
        baseDatos.withTransaction {
            montajeDao.upsertAssemblyTire(montaje)
            operacionDao.insertar(operacion)
        }
    }

    /** La posición se libera en el momento para que el monitor no la muestre montada. */
    suspend fun registrarDesmontaje(
        posicion: String,
        operaciones: List<OperacionPendienteEntity>
    ): List<Long> = withContext(io) {
        baseDatos.withTransaction {
            montajeDao.deleteAssemblyTire(posicion)
            operaciones.map { operacionDao.insertar(it) }
        }
    }

    suspend fun registrarOperacion(operacion: OperacionPendienteEntity): Long = withContext(io) {
        operacionDao.insertar(operacion)
    }

    suspend fun obtenerMontaje(posicion: String): AssemblyTireEntity? = withContext(io) {
        montajeDao.getAssemblyTire(posicion)
    }

    /** Lo que dice el servidor, salvo las posiciones con envíos en curso que aún no refleja. */
    suspend fun reconciliarMontajes(
        delServidor: List<AssemblyTireEntity>,
        idUsuario: Int
    ) = withContext(io) {
        baseDatos.withTransaction {
            val conservar = operacionDao.posicionesEnviables(idUsuario)
            montajeDao.eliminarExcepto(conservar)
            delServidor.filter { it.positionTire !in conservar }
                .forEach { montajeDao.upsertAssemblyTire(it) }
        }
    }

    suspend fun siguienteParaEnviar(idUsuario: Int) = withContext(io) {
        operacionDao.siguienteParaEnviar(idUsuario)
    }

    suspend fun obtener(id: Long) = withContext(io) { operacionDao.obtener(id) }

    fun observar(idUsuario: Int) = operacionDao.observar(idUsuario)

    suspend fun reintentar(id: Long) = withContext(io) { operacionDao.reintentar(id) }

    suspend fun marcarEnviando(id: Long) = withContext(io) { operacionDao.marcarEnviando(id) }

    suspend fun restablecerPendiente(id: Long) = withContext(io) {
        operacionDao.restablecerPendiente(id)
    }

    suspend fun marcarRechazada(id: Long, mensaje: String?) = withContext(io) {
        operacionDao.marcarRechazada(id, mensaje)
    }

    suspend fun sumarIntento(id: Long) = withContext(io) { operacionDao.sumarIntento(id) }

    /**
     * Una inspección cuenta para la regla de una al día cuando el servidor la acepta; mientras
     * espera la cubre la cola, y si la rechaza no deja rastro.
     */
    suspend fun eliminarAceptada(id: Long) = withContext(io) {
        baseDatos.withTransaction {
            val operacion = operacionDao.obtener(id)
            if (operacion?.tipo == TipoOperacion.INSPECCION) {
                val inspeccion = gsonPayload.fromJson(operacion.payload, InspectionTireDto::class.java)
                sensorDao.updateLastInspection(
                    operacion.idMonitor, operacion.posicion, inspeccion.dateInspection
                )
            }
            operacionDao.eliminar(id)
        }
    }

    suspend fun eliminarConGrupo(id: Long) = withContext(io) { operacionDao.eliminarConGrupo(id) }
}
