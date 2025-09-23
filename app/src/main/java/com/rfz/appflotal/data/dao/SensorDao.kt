package com.rfz.appflotal.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rfz.appflotal.data.model.flotalSoft.SensorTpmsEntity

@Dao
interface SensorDao {
    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun insert(sensorTpms: SensorTpmsEntity)

    @Query(
        "DELETE FROM sensorTpms WHERE (sensor_id, timestamp) NOT IN " +
                "(SELECT sensor_id, MAX(timestamp) FROM sensorTpms WHERE monitor_id = :monitorId " +
                "GROUP BY sensor_id) " +
                "AND monitor_id = :monitorId"
    )
    suspend fun deleteOldRecords(monitorId: Int)

    @Query("SELECT * FROM sensorTpms WHERE monitor_id = :monitorId AND sendStatus = 0")
    suspend fun getUnsentRecords(monitorId: Int): List<SensorTpmsEntity?>

    @Query(
        "SELECT * FROM sensorTpms AS st1 WHERE monitor_id = :monitorId " +
                "AND timestamp = (SELECT MAX(st2.timestamp) FROM sensorTpms AS st2 " +
                "WHERE st1.sensor_id = st2.sensor_id) ORDER BY st1.timestamp DESC"
    )
    suspend fun getLastRecords(monitorId: Int): List<SensorTpmsEntity?>

    @Query("SELECT EXISTS (SELECT 1 FROM sensorTpms WHERE sensor_id = :sensorId)")
    suspend fun exist(sensorId: String): Boolean

    @Query("UPDATE sensorTpms SET sendStatus = :sendStatus, active =:active WHERE monitor_id = :monitorId " +
                "AND timestamp = :timestamp")
    suspend fun setRecordStatus(
        monitorId: Int,
        timestamp: String,
        sendStatus: Boolean,
        active: Boolean
    )

    @Query("UPDATE sensorTpms SET active =:active WHERE monitor_id = :monitorId " +
                "AND timestamp = :timestamp")
    suspend fun updateActiveStatus(monitorId: Int, timestamp: String, active: Boolean)
}