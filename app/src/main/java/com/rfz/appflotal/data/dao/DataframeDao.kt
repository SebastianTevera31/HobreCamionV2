package com.rfz.appflotal.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rfz.appflotal.data.model.database.DataframeEntity

@Dao
interface DataframeDao {
    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun insert(dataframe: DataframeEntity): Long

    /**
     * Tramas que ya no se van a enviar (aceptadas o rechazadas por el servidor) anteriores a
     * [corte]. Las fechas son ISO-8601 en UTC, así que se comparan como texto.
     */
    @Query("DELETE FROM dataframe WHERE (sendStatus = 1 OR active = 0) AND timestamp < :corte")
    suspend fun borrarResueltasAntesDe(corte: String): Int

    /** Tramas que siguen pendientes de enviar, anteriores a [corte]. */
    @Query("DELETE FROM dataframe WHERE sendStatus = 0 AND active = 1 AND timestamp < :corte")
    suspend fun borrarPendientesAntesDe(corte: String): Int

    // active = 0 marca tramas que el servidor rechazó (p. ej. "Invalid Frame"); no se reintentan
    @Query("SELECT * FROM dataframe WHERE monitor_id = :monitorId AND sendStatus = 0 AND active = 1")
    suspend fun getUnsentRecords(monitorId: Int): List<DataframeEntity?>

    @Query(
        "SELECT * FROM dataframe AS st1 WHERE monitor_id = :monitorId " +
                "AND timestamp = (SELECT MAX(st2.timestamp) FROM dataframe AS st2 " +
                "WHERE st1.sensor_id = st2.sensor_id) ORDER BY st1.timestamp DESC"
    )
    suspend fun getLastRecords(monitorId: Int): List<DataframeEntity?>

    @Query("SELECT EXISTS (SELECT 1 FROM dataframe WHERE sensor_id = :sensorId)")
    suspend fun exist(sensorId: String): Boolean

    @Query("UPDATE dataframe SET sendStatus = :sendStatus, active =:active WHERE id_record = :idRecord")
    suspend fun setRecordStatus(
        idRecord: Long,
        sendStatus: Boolean,
        active: Boolean
    )

    @Query("UPDATE dataframe SET active =:active WHERE monitor_id = :monitorId " +
                "AND timestamp = :timestamp")
    suspend fun updateActiveStatus(monitorId: Int, timestamp: String, active: Boolean)
}