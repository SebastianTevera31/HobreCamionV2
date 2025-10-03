package com.rfz.appflotal.data.repository.tpms

import com.rfz.appflotal.data.NetworkStatus
import com.rfz.appflotal.data.model.database.SensorDataEntity
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
import com.rfz.appflotal.data.repository.database.SensorDataTableRepository
import com.rfz.appflotal.data.repository.database.toMonitorTireByDateResponse
import com.rfz.appflotal.data.repository.wifi.WifiRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

class ApiTpmsRepository @Inject constructor(
    private val apiTpmsService: ApiTpmsService,
    private val wifiRepository: WifiRepository,
    private val sensorDataTableRepository: SensorDataTableRepository,
) {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var _wifiState: MutableStateFlow<NetworkStatus> =
        MutableStateFlow(NetworkStatus.Disconnected)

    init {
        scope.launch {
            wifiRepository.wifiConnectionState.distinctUntilChangedBy { it }.collect { state ->
                _wifiState.update { state }
            }
        }
    }

    suspend fun doPostSensorData(sensorRequest: SensorRequest): ApiResult<List<TpmsResponse>?> {
        return apiTpmsService.postSensorData(sensorRequest)
    }

    suspend fun doPostCrudMonitor(monitorRequest: CrudMonitor): ApiResult<List<TpmsResponse>?> {
        return apiTpmsService.postCrudMonitor(monitorRequest)
    }

    suspend fun doGetDiagramMonitor(monitorId: Int): ApiResult<List<DiagramMonitorResponse>?> {
        return if (_wifiState.value == NetworkStatus.Connected) {
            apiTpmsService.getDiagramMonitor(monitorId)
        } else {
            val list = sensorDataTableRepository.getLastData(monitorId)
            ApiResult.Success(list.map { it.toDiagram() })
        }
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
        return if (_wifiState.value == NetworkStatus.Connected) {
            apiTpmsService.getMonitorTireByDate(monitorId, position, date)
        } else {
            val result = sensorDataTableRepository.getDataByDate(
                idMonitor = monitorId,
                tire = position.uppercase(Locale.getDefault()),
                timestamp = date
            ).map { it.toMonitorTireByDateResponse() }
            ApiResult.Success(result)
        }
    }
}

fun SensorDataEntity.toDiagram(): DiagramMonitorResponse {
    return DiagramMonitorResponse(
        idDiagramaMonitor = 0,
        monitorId = idMonitor,
        monitorMac = "",
        axleId = 0,
        axleDescription = "",
        sensorPosition = tire,
        positionDescription = "",
        sensorId = if (active) 1 else 0,
        sensorName = "",
        configId = 0,
        configDescription = "",
        psi = pressure.toFloat(),
        tireNumber = tireNumber,
        temperature = temperature.toFloat(),
        highTemperature = false,
        lowPressure = false,
        highPressure = false,
        lowBattery = false,
        ultimalectura = timestamp
    )
}