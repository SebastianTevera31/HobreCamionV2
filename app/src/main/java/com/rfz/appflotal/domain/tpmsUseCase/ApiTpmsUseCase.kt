package com.rfz.appflotal.domain.tpmsUseCase

import com.rfz.appflotal.data.model.tpms.ConfigurationByIdMonitorResponse
import com.rfz.appflotal.data.model.tpms.DiagramMonitorResponse
import com.rfz.appflotal.data.model.tpms.SensorRequest
import com.rfz.appflotal.data.model.tpms.TpmsResponse
import com.rfz.appflotal.data.network.service.ResultApi
import com.rfz.appflotal.data.repository.tpms.ApiTpmsRepository
import javax.inject.Inject

class ApiTpmsUseCase @Inject constructor(private val apiTpmsRepository: ApiTpmsRepository) {
    suspend fun doPostSensorData(
        fldFrame: String,
        monitorId: Int,
        fldDateData: String
    ): ResultApi<List<TpmsResponse>?> {
        return apiTpmsRepository.doPostSensorData(SensorRequest(fldFrame, monitorId, fldDateData))
    }

    suspend fun doGetDiagramMonitor(monitorId: Int): ResultApi<List<DiagramMonitorResponse>?> {
        return apiTpmsRepository.doGetDiagramMonitor(monitorId)
    }

    suspend fun doGetConfigurationMonitorById(monitorId: Int): ResultApi<List<ConfigurationByIdMonitorResponse>?> {
        return apiTpmsRepository.doGetConfigurationById(monitorId)
    }

}