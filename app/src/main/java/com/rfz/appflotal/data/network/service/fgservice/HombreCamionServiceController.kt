package com.rfz.appflotal.data.network.service.fgservice

import android.util.Log
import com.rfz.appflotal.core.util.Commons.convertDate
import com.rfz.appflotal.core.util.Commons.parseUtcDate
import com.rfz.appflotal.core.util.Commons.toUtcIsoDate
import com.rfz.appflotal.data.NetworkStatus
import com.rfz.appflotal.data.model.database.SensorDataEntity
import com.rfz.appflotal.data.model.tpms.DiagramMonitorResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.repository.database.SensorDataTableRepository
import com.rfz.appflotal.domain.database.CoordinatesTableUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.tpms.ApiTpmsUseCase
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
import java.time.Instant
import java.time.temporal.ChronoUnit

class HombreCamionServiceController @Inject constructor(
    private val apiTmpsUseCase: ApiTpmsUseCase,
    private val wifiUseCase: WifiUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val sensorDataTableRepository: SensorDataTableRepository,
    private val coordinatesTableUseCase: CoordinatesTableUseCase
) {
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun stopService() = serviceScope.cancel()

    fun getDataApi() {
        serviceScope.launch {
            wifiUseCase().collect { state ->
                if (state == NetworkStatus.Connected) {
                    // Se lee en cada sincronización: el monitor puede registrarse (wizard)
                    // después de que arrancó el servicio, y antes quedaba con id_monitor 0.
                    val monitorId = getTasksUseCase().first().firstOrNull()?.id_monitor
                    if (monitorId != null && monitorId != 0) processMonitorData(monitorId)
                }
            }
        }
    }

    private suspend fun processMonitorData(monitorId: Int) {
        val response = apiTmpsUseCase.doGetDiagramMonitor(monitorId)
        handleMonitorResponse(response, monitorId)
    }

    private suspend fun handleMonitorResponse(
        response: ApiResult<List<DiagramMonitorResponse>?>,
        monitorId: Int
    ) {
        asyncResponseHelper(
            response,
            onError = { Log.e("HBServiceController", "Error al traer datos del monitor") },
        ) { data ->
            withContext(Dispatchers.IO) {
                val records = mutableListOf<SensorDataEntity>()
                data?.forEach {
                    val tire = it.sensorPosition

                    val inAlert = it.highPressure || it.highTemperature || it.lowPressure || it.lowBattery

                    // ultimalectura llega en UTC (con o sin "Z"); se guarda normalizada en UTC.
                    val date = toUtcIsoDate(it.ultimalectura)

                    val lastInspection = convertDate(
                        it.ultimaInspeccion, "yyyy-MM-dd'T'HH:mm:ss",
                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
                    )

                    val cutoff = Instant.now().minus(15, ChronoUnit.MINUTES)

                    val storeData = parseUtcDate(it.ultimalectura)
                    val isActive = it.sensorId != 0 && !storeData.isBefore(cutoff)

                    records += SensorDataEntity(
                        idMonitor = it.monitorId,
                        tire = tire,
                        tireNumber = "",
                        timestamp = date,
                        temperature = it.temperature.toInt(),
                        pressure = it.psi.toInt(),
                        active = isActive,
                        highTemperatureAlert = it.highTemperature,
                        highPressureAlert = it.highPressure,
                        lowPressureAlert = it.lowPressure,
                        lowBatteryAlert = it.lowBattery,
                        punctureAlert = it.puncture,
                        lastInspection = lastInspection
                    )

                    // Actualiza el estado de alerta y disponibilidad de las ruedas
                    coordinatesTableUseCase.updateCoordinates(
                        monitorId = monitorId,
                        tire = tire,
                        isActive = isActive,
                        isAlert = inAlert
                    )
                }
                sensorDataTableRepository.replaceMonitorData(monitorId, records)
            }
        }
    }
}