package com.rfz.appflotal.data.repository.tpms

import com.rfz.appflotal.data.model.tpms.ConfigurationByIdMonitorResponse
import com.rfz.appflotal.data.model.tpms.DiagramMonitorResponse
import com.rfz.appflotal.data.model.tpms.SensorRequest
import com.rfz.appflotal.data.network.service.ResultApi
import com.rfz.appflotal.data.network.service.tpms.ApiTpmsService
import javax.inject.Inject

class ApiTpmsRepository @Inject constructor(private val apiTpmsService: ApiTpmsService) {
    suspend fun doPostSensorData(sensorRequest: SensorRequest) {
        apiTpmsService.postSensorData(sensorRequest)
    }

    suspend fun doGetDiagramMonitor(monitorId: Int): ResultApi<List<DiagramMonitorResponse>?> {
        return apiTpmsService.getDiagramMonitor(monitorId)
    }

    suspend fun doGetConfigurationById(monitorId: Int): ResultApi<List<ConfigurationByIdMonitorResponse>?> {
        return apiTpmsService.getConfigurationByIdMonitor(monitorId)
    }
}