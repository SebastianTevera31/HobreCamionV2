package com.rfz.appflotal.data.repository.tpms

import com.rfz.appflotal.data.model.tpms.ConfigurationByIdMonitorResponse
import com.rfz.appflotal.data.model.tpms.CrudMonitor
import com.rfz.appflotal.data.model.tpms.DiagramMonitorResponse
import com.rfz.appflotal.data.model.tpms.GetConfigurationsResponse
import com.rfz.appflotal.data.model.tpms.MonitorTireByDateResponse
import com.rfz.appflotal.data.model.tpms.PositionCoordinatesResponse
import com.rfz.appflotal.data.model.tpms.SensorRequest
import com.rfz.appflotal.data.model.tpms.TpmsResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.network.service.tpms.ApiTpmsService
import javax.inject.Inject

class ApiTpmsRepository @Inject constructor(private val apiTpmsService: ApiTpmsService) {

    suspend fun doPostSensorData(sensorRequest: SensorRequest): ApiResult<List<TpmsResponse>?> {
        return apiTpmsService.postSensorData(sensorRequest)
    }

    suspend fun doPostCrudMonitor(monitorRequest: CrudMonitor): ApiResult<List<TpmsResponse>?> {
        return apiTpmsService.postCrudMonitor(monitorRequest)
    }

    suspend fun doGetDiagramMonitor(monitorId: Int): ApiResult<List<DiagramMonitorResponse>?> {
        return apiTpmsService.getDiagramMonitor(monitorId)
    }

    suspend fun doGetConfigurations(): ApiResult<List<GetConfigurationsResponse>?> {
        return apiTpmsService.getConfigurations()
    }

    suspend fun doGetConfigurationById(monitorId: Int): ApiResult<List<ConfigurationByIdMonitorResponse>?> {
        return apiTpmsService.getConfigurationByIdMonitor(monitorId)
    }

    suspend fun doGetPositionCoordinates(monitorId: Int): ApiResult<List<PositionCoordinatesResponse>?> {
        return apiTpmsService.getPositionCoordinates(monitorId)
    }

    suspend fun doGetMonitorTireByDate(
        monitorId: Int,
        position: String,
        date: String
    ): ApiResult<List<MonitorTireByDateResponse>?> {
        return apiTpmsService.getMonitorTireByDate(monitorId, position, date)
    }
}