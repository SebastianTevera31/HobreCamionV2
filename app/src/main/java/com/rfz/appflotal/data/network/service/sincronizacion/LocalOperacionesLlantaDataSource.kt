package com.rfz.appflotal.data.network.service.sincronizacion

import androidx.room.withTransaction
import com.rfz.appflotal.data.dao.AssemblyTireDao
import com.rfz.appflotal.data.dao.OperacionPendienteDao
import com.rfz.appflotal.data.database.AppHombreCamionDatabase
import com.rfz.appflotal.data.model.assembly.AssemblyTireEntity
import com.rfz.appflotal.data.model.sincronizacion.OperacionPendienteEntity
import com.rfz.appflotal.di.dispatchers.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** Estado local de las llantas y cola de envíos; lo que cambia ambos va en una transacción. */
class LocalOperacionesLlantaDataSource @Inject constructor(
    private val baseDatos: AppHombreCamionDatabase,
    private val operacionDao: OperacionPendienteDao,
    private val montajeDao: AssemblyTireDao,
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

    suspend fun marcarEnviando(id: Long) = withContext(io) { operacionDao.marcarEnviando(id) }

    suspend fun restablecerPendiente(id: Long) = withContext(io) {
        operacionDao.restablecerPendiente(id)
    }

    suspend fun marcarRechazada(id: Long, mensaje: String?) = withContext(io) {
        operacionDao.marcarRechazada(id, mensaje)
    }

    suspend fun sumarIntento(id: Long) = withContext(io) { operacionDao.sumarIntento(id) }

    suspend fun eliminar(id: Long) = withContext(io) { operacionDao.eliminar(id) }

    suspend fun eliminarConGrupo(id: Long) = withContext(io) { operacionDao.eliminarConGrupo(id) }
}
