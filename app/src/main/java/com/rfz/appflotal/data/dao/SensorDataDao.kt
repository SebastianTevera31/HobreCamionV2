package com.rfz.appflotal.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rfz.appflotal.data.model.database.SensorDataEntity

@Dao
interface SensorDataDao {

    @Insert
    suspend fun insertSensorData(sensorData: SensorDataEntity)

    @Query("SELECT * FROM sensor_data WHERE monitor_id =:monitorId AND tire =:tire AND DATE(timestamp) =:timestamp")
    suspend fun getRecordByDate(
        monitorId: Int,
        tire: String,
        timestamp: String
    ): List<SensorDataEntity>

    @Query(
        "SELECT * FROM sensor_data AS st1 WHERE monitor_id = :monitorId " +
                "AND timestamp = (SELECT MAX(st2.timestamp) FROM sensor_data AS st2 " +
                "WHERE st1.tire = st2.tire) AND active = 1 ORDER BY st1.timestamp DESC"
    )
    suspend fun getLastRecords(monitorId: Int): List<SensorDataEntity>

    @Query("SELECT * FROM sensor_data WHERE monitor_id = :monitorId AND tire = :tire ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastRecordByTire(monitorId: Int, tire: String): SensorDataEntity?

    @Query("DELETE FROM sensor_data WHERE monitor_id =:monitorId")
    suspend fun deleteData(monitorId: Int)
}