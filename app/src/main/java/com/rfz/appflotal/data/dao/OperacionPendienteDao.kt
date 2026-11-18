package com.rfz.appflotal.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rfz.appflotal.data.model.sincronizacion.OperacionPendienteEntity

/** Una operación es enviable si no tiene error y ninguna anterior de su posición lo tiene. */
private const val ENVIABLE = """
    o.idUsuario = :idUsuario AND o.estado != 'CON_ERROR'
    AND NOT EXISTS (
        SELECT 1 FROM operacion_pendiente AS e
        WHERE e.idUsuario = o.idUsuario AND e.posicion = o.posicion
            AND e.estado = 'CON_ERROR' AND e.id < o.id
    )
"""

@Dao
interface OperacionPendienteDao {
    @Insert
    suspend fun insertar(operacion: OperacionPendienteEntity): Long

    @Query("SELECT * FROM operacion_pendiente AS o WHERE $ENVIABLE ORDER BY o.id LIMIT 1")
    suspend fun siguienteParaEnviar(idUsuario: Int): OperacionPendienteEntity?

    @Query("SELECT DISTINCT o.posicion FROM operacion_pendiente AS o WHERE $ENVIABLE")
    suspend fun posicionesEnviables(idUsuario: Int): List<String>

    @Query("SELECT * FROM operacion_pendiente WHERE id = :id")
    suspend fun obtener(id: Long): OperacionPendienteEntity?

    @Query("UPDATE operacion_pendiente SET estado = 'ENVIANDO' WHERE id = :id")
    suspend fun marcarEnviando(id: Long)

    @Query("UPDATE operacion_pendiente SET estado = 'PENDIENTE' WHERE id = :id")
    suspend fun restablecerPendiente(id: Long)

    @Query(
        "UPDATE operacion_pendiente SET estado = 'CON_ERROR', ultimoError = :mensaje, " +
            "intentos = intentos + 1 WHERE id = :id"
    )
    suspend fun marcarRechazada(id: Long, mensaje: String?)

    @Query("UPDATE operacion_pendiente SET intentos = intentos + 1 WHERE id = :id")
    suspend fun sumarIntento(id: Long)

    @Query("DELETE FROM operacion_pendiente WHERE id = :id")
    suspend fun eliminar(id: Long)

    /** Descartar una parte del desmontaje descarta también su inspección (mismo grupo). */
    @Query(
        "DELETE FROM operacion_pendiente WHERE id = :id OR (idGrupo IS NOT NULL AND idGrupo = " +
            "(SELECT idGrupo FROM operacion_pendiente WHERE id = :id))"
    )
    suspend fun eliminarConGrupo(id: Long)
}
