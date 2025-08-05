package com.rfz.appflotal.data.repository.tpms

import com.rfz.appflotal.data.model.tpms.SensorRequest
import com.rfz.appflotal.data.network.service.tpms.ApiTpmsService
import javax.inject.Inject

class ApiTpmsRepository @Inject constructor(private val apiTpmsService: ApiTpmsService) {
    suspend fun doPostSensorData(sensorRequest: SensorRequest) {
        apiTpmsService.postSensorData(sensorRequest)
    }

    suspend fun doGetDiagramMonitor(monitorId: Int) {
        apiTpmsService.getDiagramMonitor(monitorId)
    }

    suspend fun doGetConfigurationById(monitorId: Int) {
        apiTpmsService.getConfigurationByIdMonitor(monitorId)
    }
}