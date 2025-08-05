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
                "AND monitor_id = :monitorId")
    suspend fun deleteOldRecords(monitorId: Int)

    @Query("SELECT * FROM sensorTpms WHERE monitor_id = :monitorId AND sendStatus = 0")
    suspend fun getUnsentRecords(monitorId: Int): List<SensorTpmsEntity?>

    @Query("SELECT * FROM sensorTpms WHERE monitor_id = :monitorId ORDER BY rowid DESC LIMIT 1")
    suspend fun getLastRecord(monitorId: Int): SensorTpmsEntity?

    @Query("SELECT EXISTS (SELECT 1 FROM sensorTpms WHERE sensor_id = :sensorId)")
    suspend fun exist(sensorId: String): Boolean

    @Query(
        "UPDATE sensorTpms SET sendStatus = :sendStatus WHERE monitor_id = :monitorId " +
                "AND timestamp = :timestamp")
    suspend fun setRecordStatus(monitorId: Int, timestamp: String, sendStatus: Boolean)
}