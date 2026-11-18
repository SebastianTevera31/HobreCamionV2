package com.rfz.appflotal.domain.sincronizacion

import com.rfz.appflotal.data.model.assembly.AssemblyTire
import com.rfz.appflotal.data.model.assembly.AssemblyTireResponse
import com.rfz.appflotal.data.model.sincronizacion.EstadoOperacion
import com.rfz.appflotal.data.model.sincronizacion.OperacionPendiente
import com.rfz.appflotal.data.model.sincronizacion.ResultadoEnvio
import com.rfz.appflotal.data.model.sincronizacion.TipoOperacion
import com.rfz.appflotal.data.repository.assembly.AssemblyTireRepository
import com.rfz.appflotal.data.repository.sincronizacion.ColaSincronizacionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/** Cola en memoria con la misma regla de bloqueo por posición que el DAO. */
class FakeColaSincronizacion : ColaSincronizacionRepository {
    val operaciones = mutableListOf<OperacionPendiente>()
    val respuestas = mutableMapOf<Long, ResultadoEnvio>()
    val aplicadas = mutableMapOf<Long, Boolean?>()
    val enviadas = mutableListOf<Long>()
    var programaciones = 0

    fun agregar(
        id: Long,
        posicion: String,
        tipo: TipoOperacion = TipoOperacion.MONTAJE,
        estado: EstadoOperacion = EstadoOperacion.PENDIENTE,
        idGrupo: String? = null
    ) {
        operaciones += OperacionPendiente(id, tipo, posicion, idGrupo, estado, 0, null, id)
    }

    fun estadoDe(id: Long) = operaciones.find { it.id == id }?.estado

    override suspend fun siguienteParaEnviar() = operaciones.sortedBy { it.id }.firstOrNull { op ->
        op.estado != EstadoOperacion.CON_ERROR && operaciones.none {
            it.posicion == op.posicion && it.estado == EstadoOperacion.CON_ERROR && it.id < op.id
        }
    }

    override suspend fun obtener(id: Long) = operaciones.find { it.id == id }

    override suspend fun marcarEnviando(id: Long) = cambiar(id) { it.copy(estado = EstadoOperacion.ENVIANDO) }

    override suspend fun enviar(id: Long): ResultadoEnvio {
        enviadas += id
        return respuestas[id] ?: ResultadoEnvio.Aceptada
    }

    override suspend fun yaAplicada(id: Long) = aplicadas[id]

    override suspend fun marcarAceptada(id: Long) {
        operaciones.removeAll { it.id == id }
    }

    override suspend fun marcarRechazada(id: Long, mensaje: String?) =
        cambiar(id) { it.copy(estado = EstadoOperacion.CON_ERROR, ultimoError = mensaje) }

    override suspend fun registrarFalloConexion(id: Long) =
        cambiar(id) { it.copy(intentos = it.intentos + 1) }

    override suspend fun restablecerPendiente(id: Long) =
        cambiar(id) { it.copy(estado = EstadoOperacion.PENDIENTE) }

    override suspend fun descartar(id: Long) {
        val grupo = obtener(id)?.idGrupo
        operaciones.removeAll { it.id == id || (grupo != null && it.idGrupo == grupo) }
    }

    override fun programarEnvio() {
        programaciones++
    }

    private fun cambiar(id: Long, cambio: (OperacionPendiente) -> OperacionPendiente) {
        val indice = operaciones.indexOfFirst { it.id == id }
        if (indice >= 0) operaciones[indice] = cambio(operaciones[indice])
    }
}

class FakeMontajes : AssemblyTireRepository {
    var reconciliaciones = 0

    override suspend fun observeAssemblyTire(): Flow<List<AssemblyTire>> = emptyFlow()
    override suspend fun getAssemblyTire(positionTire: String): AssemblyTire? = null
    override suspend fun confirmTireMounted(positionTire: String) = false
    override suspend fun refreshMountedTires(): Result<List<AssemblyTireResponse>> {
        reconciliaciones++
        return Result.success(emptyList())
    }
}
