package com.rfz.appflotal.data.repository.tpms

import com.rfz.appflotal.data.model.tpms.ConfigurationByIdMonitorResponse
import com.rfz.appflotal.data.model.tpms.DiagramMonitorResponse
import com.rfz.appflotal.data.model.tpms.MonitorTireByDateResponse
import com.rfz.appflotal.data.model.tpms.PositionCoordinatesResponse
import com.rfz.appflotal.data.model.tpms.SensorRequest
import com.rfz.appflotal.data.model.tpms.TpmsResponse
import com.rfz.appflotal.data.network.service.ResultApi
import com.rfz.appflotal.data.network.service.tpms.ApiTpmsService
import javax.inject.Inject

class ApiTpmsRepository @Inject constructor(private val apiTpmsService: ApiTpmsService) {
    suspend fun doPostSensorData(sensorRequest: SensorRequest): ResultApi<List<TpmsResponse>?> {
        return apiTpmsService.postSensorData(sensorRequest)
    }

    suspend fun doGetDiagramMonitor(monitorId: Int): ResultApi<List<DiagramMonitorResponse>?> {
        return apiTpmsService.getDiagramMonitor(monitorId)
    }

    suspend fun doGetConfigurationById(monitorId: Int): ResultApi<List<ConfigurationByIdMonitorResponse>?> {
        return apiTpmsService.getConfigurationByIdMonitor(monitorId)
    }

    suspend fun doGetPositionCoordinates(monitorId: Int): ResultApi<List<PositionCoordinatesResponse>?> {
        return apiTpmsService.getPositionCoordinates(monitorId)
    }

    suspend fun doGetMonitorTireByDate(
        monitorId: Int,
        position: String,
        date: String
    ): ResultApi<List<MonitorTireByDateResponse>?> {
        return apiTpmsService.getMonitorTireByDate(monitorId, position, date)
    }
}