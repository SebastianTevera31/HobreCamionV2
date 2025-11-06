package com.rfz.appflotal.data.repository.database

import com.rfz.appflotal.data.dao.SensorDataDao
import com.rfz.appflotal.data.model.database.SensorDataEntity
import com.rfz.appflotal.data.model.tpms.MonitorTireByDateResponse
import com.rfz.appflotal.domain.database.GetTasksUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first

class SensorDataTableRepository @Inject constructor(
    private val sensorData: SensorDataDao,
    private val getTasksUseCase: GetTasksUseCase
) {
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
        punctureAlert: Boolean,
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
                lowBatteryAlert = lowBatteryAlert,
                punctureAlert = punctureAlert
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

    suspend fun deactivateTireRecord(monitorId: Int, tire: String) {
        sensorData.deactivateSensorRecord(
            monitorId = monitorId,
            tire = tire
        )
    }

    suspend fun deleteMonitorData(monitorId: Int) = sensorData.deleteMonitorData(monitorId)

    suspend fun updateTireRecord(tire: String, temperature: Int, pressure: Int) {
        val idMonitor = getTasksUseCase.invoke().first()[0].id_monitor
        sensorData.updateSensorRecord(
            monitorId = idMonitor,
            tire = tire,
            temperature = temperature,
            pressure = pressure
        )
    }

    suspend fun isTireActive(monitorId: Int, tire: String): Boolean {
        val data = sensorData.getLastRecordByTire(monitorId, tire)
        return data?.active ?: false
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