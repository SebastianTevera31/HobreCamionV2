package com.rfz.appflotal.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rfz.appflotal.data.model.database.SensorDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SensorDataDao {

    @Query(
        "SELECT * FROM sensor_data AS st1 WHERE monitor_id = :monitorId " +
                "AND timestamp = (SELECT MAX(st2.timestamp) FROM sensor_data AS st2 " +
                "WHERE st1.tire = st2.tire) ORDER BY st1.timestamp DESC"
    )
    fun observeLastRecords(monitorId: Int): Flow<List<SensorDataEntity>>

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
                "WHERE st1.tire = st2.tire) ORDER BY st1.timestamp DESC"
    )
    suspend fun getLastRecords(monitorId: Int): List<SensorDataEntity>

    @Query("SELECT * FROM sensor_data WHERE monitor_id = :monitorId AND tire = :tire ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastRecordByTire(monitorId: Int, tire: String): SensorDataEntity?

    @Query("DELETE FROM sensor_data WHERE monitor_id =:monitorId")
    suspend fun deleteData(monitorId: Int)

    @Query("UPDATE sensor_data SET active = 0 WHERE monitor_id =:monitorId AND tire =:tire")
    suspend fun deactivateSensorRecord(monitorId: Int, tire: String)

    @Query("DELETE FROM sensor_data WHERE monitor_id =:monitorId")
    suspend fun deleteMonitorData(monitorId: Int)

    @Query("UPDATE sensor_data SET temperature =:temperature, pressure =:pressure WHERE monitor_id =:monitorId AND tire =:tire")
    suspend fun updateSensorRecord(monitorId: Int, tire: String, temperature: Int, pressure: Int)

    @Query(
        """
    UPDATE sensor_data
    SET temperature = :temperature,
        pressure = :pressure,
        high_temperature_alert = :highTemperatureAlert,
        high_pressure_alert = :highPressureAlert,
        low_pressure_alert = :lowPressureAlert,
        low_battery_alert = :lowBatteryAlert,
        puncture_alert = :punctureAlert,
        active = :active
    WHERE monitor_id = :idMonitor
      AND tire = :tire"""
    )
    suspend fun updateSensor(
        idMonitor: Int,
        tire: String,
        temperature: Int,
        pressure: Int,
        highTemperatureAlert: Boolean,
        highPressureAlert: Boolean,
        lowPressureAlert: Boolean,
        lowBatteryAlert: Boolean,
        punctureAlert: Boolean,
        active: Boolean
    )

    @Query("UPDATE sensor_data SET last_inspection =:lastInspection WHERE monitor_id =:monitorId AND tire =:tire")
    suspend fun updateLastInspection(monitorId: Int, tire: String, lastInspection: String)
}
