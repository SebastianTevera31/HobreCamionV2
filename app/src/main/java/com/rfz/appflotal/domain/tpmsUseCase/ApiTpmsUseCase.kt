package com.rfz.appflotal.domain.tpmsUseCase

import com.rfz.appflotal.data.model.tpms.ConfigurationByIdMonitorResponse
import com.rfz.appflotal.data.model.tpms.CrudMonitor
import com.rfz.appflotal.data.model.tpms.DiagramMonitorResponse
import com.rfz.appflotal.data.model.tpms.GetConfigurationsResponse
import com.rfz.appflotal.data.model.tpms.MonitorTireByDateResponse
import com.rfz.appflotal.data.model.tpms.PositionCoordinatesResponse
import com.rfz.appflotal.data.model.tpms.SensorRequest
import com.rfz.appflotal.data.model.tpms.TpmsResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.repository.tpms.ApiTpmsRepository
import javax.inject.Inject

class ApiTpmsUseCase @Inject constructor(private val apiTpmsRepository: ApiTpmsRepository) {

    suspend fun doPostSensorData(
        fldFrame: String,
        monitorId: Int,
        fldDateData: String
    ): ApiResult<List<TpmsResponse>?> {
        return apiTpmsRepository.doPostSensorData(SensorRequest(fldFrame, monitorId, fldDateData))
    }

    suspend fun doPostCrudMonitor(
        idMonitor: Int,
        fldMac: String,
        fldDate: String,
        idVehicle: Int,
        idConfiguration: Int
    ): ApiResult<List<TpmsResponse>?> {
        return apiTpmsRepository.doPostCrudMonitor(
            CrudMonitor(idMonitor, fldMac, fldDate, idVehicle, idConfiguration)
        )
    }

    suspend fun doGetDiagramMonitor(monitorId: Int): ApiResult<List<DiagramMonitorResponse>?> {
        return apiTpmsRepository.doGetDiagramMonitor(monitorId)
    }

    suspend fun doGetConfigurations(): ApiResult<List<GetConfigurationsResponse>?> {
        return apiTpmsRepository.doGetConfigurations()
    }

    suspend fun doGetConfigurationMonitorById(monitorId: Int): ApiResult<List<ConfigurationByIdMonitorResponse>?> {
        return apiTpmsRepository.doGetConfigurationById(monitorId)
    }

    suspend fun doGetPositionCoordinates(monitorId: Int): ApiResult<List<PositionCoordinatesResponse>?> {
        return apiTpmsRepository.doGetPositionCoordinates(monitorId)
    }

    suspend fun doGetMonitorTireByDate(
        monitorId: Int,
        position: String,
        date: String
    ): ApiResult<List<MonitorTireByDateResponse>?> {
        return apiTpmsRepository.doGetMonitorTireByDate(monitorId, position, date)
    }
}