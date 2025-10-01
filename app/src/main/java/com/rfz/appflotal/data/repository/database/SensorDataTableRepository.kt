package com.rfz.appflotal.data.repository.database

import com.rfz.appflotal.data.dao.SensorDataDao
import com.rfz.appflotal.data.model.database.SensorDataEntity
import com.rfz.appflotal.data.model.tpms.MonitorTireByDateResponse
import jakarta.inject.Inject

class SensorDataTableRepository @Inject constructor(private val sensorData: SensorDataDao) {
    suspend fun insertSensorData(
        idMonitor: Int,
        tire: String,
        tireNumber: String,
        timestamp: String,
        temperature: Int,
        pressure: Int,
        highTemperatureAlert: Boolean,
        highPressureAlert: Boolean,
        lowPressureAlert: Boolean,
        lowBatteryAlert: Boolean,
        active: Boolean
    ) {
        sensorData.insertSensorData(
            SensorDataEntity(
                idMonitor = idMonitor,
                tire = tire,
                tireNumber = tireNumber,
                timestamp = timestamp,
                temperature = temperature,
                pressure = pressure,
                active = active,
                highTemperatureAlert = highTemperatureAlert,
                highPressureAlert = highPressureAlert,
                lowPressureAlert = lowPressureAlert,
                lowBatteryAlert = lowBatteryAlert
            )
        )
    }

    suspend fun getDataByDate(
        idMonitor: Int,
        tire: String,
        timestamp: String
    ): List<SensorDataEntity> {
        return sensorData.getRecordByDate(
            monitorId = idMonitor,
            tire = tire,
            timestamp = timestamp
        )
    }

    suspend fun getLastData(monitorId: Int): List<SensorDataEntity> {
        return sensorData.getLastRecords(
            monitorId = monitorId,
        )
    }

    suspend fun getLastDataByTire(monitorId: Int, tire: String): SensorDataEntity? {
        return sensorData.getLastRecordByTire(monitorId, tire)
    }

    suspend fun markInactiveOlderThan(monitorId: Int, cutoffUtc: String) {
        sensorData.updateSensorRecord(
            monitorId = monitorId,
            cutoffUtc = cutoffUtc
        )
    }
}

fun SensorDataEntity.toMonitorTireByDateResponse(): MonitorTireByDateResponse {
    return MonitorTireByDateResponse(
        tirePosition = tire,
        tireNumber = tireNumber,
        sensorDate = timestamp,
        psi = pressure,
        temperature = temperature
    )
}