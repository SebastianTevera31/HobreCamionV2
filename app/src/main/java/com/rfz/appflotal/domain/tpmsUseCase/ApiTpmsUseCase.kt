package com.rfz.appflotal.domain.tpmsUseCase

import com.rfz.appflotal.data.model.tpms.SensorRequest
import com.rfz.appflotal.data.repository.tpms.ApiTpmsRepository
import javax.inject.Inject

class ApiTpmsUseCase @Inject constructor(private val apiTpmsRepository: ApiTpmsRepository) {
    suspend fun doPostSensorData(
        token: String,
        fldFrame: String,
        monitorId: Int,
        fldDateData: String
    ) {
        apiTpmsRepository.doPostSensorData(SensorRequest(token, fldFrame, monitorId, fldDateData))
    }

    suspend fun doGetDiagramMonitor(monitorId: Int) {
        apiTpmsRepository.doGetDiagramMonitor(monitorId)
    }

    suspend fun doGetConfigurationMonitorById(monitorId: Int) {
        apiTpmsRepository.doGetConfigurationById(monitorId)
    }

}