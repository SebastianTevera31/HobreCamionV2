package com.rfz.appflotal.data.network.service.fgservice

import android.util.Log
import com.rfz.appflotal.core.util.Commons.convertDate
import com.rfz.appflotal.data.NetworkStatus
import com.rfz.appflotal.data.model.tpms.DiagramMonitorResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.repository.database.SensorDataTableRepository
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.tpmsUseCase.ApiTpmsUseCase
import com.rfz.appflotal.domain.wifi.WifiUseCase
import com.rfz.appflotal.presentation.ui.utils.asyncResponseHelper
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.text.toInt

class HombreCamionServiceController @Inject constructor(
    private val apiTmpsUseCase: ApiTpmsUseCase,
    private val wifiUseCase: WifiUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val sensorDataTableRepository: SensorDataTableRepository
) {
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun stopService() = serviceScope.cancel()

    fun getDataApi() {
        serviceScope.launch {
            val userData = getTasksUseCase().first()
            if (userData.isNotEmpty()) {
                val monitorId = userData[0].id_monitor
                wifiUseCase().collect { state ->
                    if (state == NetworkStatus.Connected) {
                        processMonitorData(monitorId)
                    }
                }
            }
        }
    }

    private suspend fun processMonitorData(monitorId: Int) {
        val response = apiTmpsUseCase.doGetDiagramMonitor(monitorId)
        sensorDataTableRepository.deleteMonitorData(monitorId)
        handleMonitorResponse(response)
    }

    private suspend fun handleMonitorResponse(
        response: ApiResult<List<DiagramMonitorResponse>?>,
    ) {
        asyncResponseHelper(
            response,
            onError = { Log.e("HBServiceController", "Error al traer datos del monitor") },
        ) { data ->
            withContext(Dispatchers.IO) {
                data?.forEach {
                    sensorDataTableRepository.insertSensorData(
                        idMonitor = it.monitorId,
                        tire = it.sensorPosition,
                        tireNumber = "",
                        timestamp = convertDate(
                            it.ultimalectura,
                            "yyyy-MM-dd'T'HH:mm:ss",
                            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
                        ),
                        temperature = it.temperature.toInt(),
                        pressure = it.psi.toInt(),
                        highTemperatureAlert = it.highTemperature,
                        highPressureAlert = it.highPressure,
                        lowPressureAlert = it.lowPressure,
                        lowBatteryAlert = it.lowBattery,
                        active = it.sensorId != 0
                    )
                }
            }
        }
    }
}