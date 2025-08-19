package com.rfz.appflotal.presentation.ui.monitor.viewmodel

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.util.Log
import androidx.annotation.StringRes
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.Commons.convertDate
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.core.util.Commons.validateBluetoothConnectivity
import com.rfz.appflotal.data.model.tpms.DiagramMonitorResponse
import com.rfz.appflotal.data.model.tpms.MonitorTireByDateResponse
import com.rfz.appflotal.data.model.tpms.PositionCoordinatesResponse
import com.rfz.appflotal.data.network.service.ResultApi
import com.rfz.appflotal.data.repository.bluetooth.MonitorDataFrame
import com.rfz.appflotal.data.repository.bluetooth.SensorAlertDataFrame
import com.rfz.appflotal.data.repository.bluetooth.decodeAlertDataFrame
import com.rfz.appflotal.data.repository.bluetooth.decodeDataFrame
import com.rfz.appflotal.domain.bluetooth.BluetoothUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.database.SensorTableUseCase
import com.rfz.appflotal.domain.tpmsUseCase.ApiTpmsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt


enum class SensorAlerts(@StringRes val message: Int) {
    HighPressure(R.string.presion_alta),
    LowPressure(R.string.presion_baja),
    HighTemperature(R.string.temperatura_alta),
    NoData(R.string.sin_datos)
}

@HiltViewModel
class MonitorViewModel @Inject constructor(
    private val apiTpmsUseCase: ApiTpmsUseCase,
    private val bluetoothUseCase: BluetoothUseCase,
    private val sensorTableUseCase: SensorTableUseCase,
    private val getTasksUseCase: GetTasksUseCase
) : ViewModel() {
    private var _monitorUiState: MutableStateFlow<MonitorUiState> =
        MutableStateFlow(MonitorUiState())
    val monitorUiState = _monitorUiState.asStateFlow()

    private val _positionsUiState =
        MutableStateFlow<ResultApi<List<DiagramMonitorResponse>?>>(ResultApi.Loading)

    val positionsUiState = _positionsUiState.asStateFlow()

    private val _monitorTireUiState =
        MutableStateFlow<ResultApi<List<MonitorTireByDateResponse>?>>(ResultApi.Success(emptyList()))
    val monitorTireUiState = _monitorTireUiState.asStateFlow()

    var shouldReadManually = true

    init {
        viewModelScope.launch {
            val userData = getTasksUseCase().first()[0]
            val configInfo = apiTpmsUseCase.doGetConfigurationMonitorById(userData.id_monitor)
            when (configInfo) {
                is ResultApi.Success -> {
                    val data = configInfo.data
                    if (!data.isNullOrEmpty()) {
                        val config = data[0].fldDescription.replace("BASE", "").trim()
                        if (config.isDigitsOnly()) {

                            val wheelsWithAlert =
                                (1..config.toInt()).associate { it -> "P$it" to false }
                                    .toMap()

                            _monitorUiState.update { currentUiState ->
                                currentUiState.copy(
                                    monitorId = userData.id_monitor,
                                    numWheels = config.toInt(),
                                    chassisImageUrl = data[0].fldUrlImage,
                                    wheelsWithAlert = wheelsWithAlert
                                )
                            }

                            // Recibe datos Bluetooth
                            readBluetoothData()
                        }
                    }
                }

                is ResultApi.Error -> {}
                is ResultApi.Loading -> {}
            }
        }
    }

    private fun readBluetoothData() {
        viewModelScope.launch {
            bluetoothUseCase().collect { data ->
                if (shouldReadManually) {
                    Log.d("MonitorViewModel", "$data")
                    val rssi = data.rssi
                    val bluetoothSignalQuality = data.bluetoothSignalQuality

                    val dataFrame = data.dataFrame

                    if (!validateBluetoothConnectivity(bluetoothSignalQuality) || dataFrame == null) {
                        val monitorId = monitorUiState.value.monitorId
                        Log.d("MonitorViewModel", "Monitor ID $monitorId")
                        // Se agrega la funcion Let como seguridad, sin embargo el Id debe existir en esta parte
                        monitorId.let {
                            sensorTableUseCase.doGetLastRecord(it).forEach { data ->
                                if (data != null) updateSensorData(data.dataFrame)
                            }
                        }
                    } else updateSensorData(dataFrame)

                    _monitorUiState.update { currentUiState ->
                        currentUiState.copy(
                            signalIntensity = Pair(
                                bluetoothSignalQuality, if (rssi != null) "$rssi dBm" else "N/A"
                            ),
                        )
                    }
                } else {
                    delay(30000)
                    shouldReadManually = true
                }
            }
        }
    }

    private fun updateSensorData(dataFrame: String) {
        _monitorUiState.update { currentUiState ->
            val wheel = decodeDataFrame(dataFrame, MonitorDataFrame.POSITION_WHEEL).toInt()

            val pressionValue = decodeDataFrame(dataFrame, MonitorDataFrame.PRESSION)

            val pressureStatus = SensorAlerts.valueOf(
                decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.PRESSURE)
            )

            val temperatureValue =
                decodeDataFrame(dataFrame, MonitorDataFrame.TEMPERATURE)

            val temperatureStatus = SensorAlerts.valueOf(
                decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.HIGH_TEMPERATURE)
            )

            val pression = (pressionValue.toFloat() * 100).roundToInt() / 100f
            val temperature =
                if (temperatureValue.isDigitsOnly()) temperatureValue.toFloat() else 0f


            val inAlert =
                temperatureStatus != SensorAlerts.NoData || pressureStatus != SensorAlerts.NoData

            val newMap =
                currentUiState.wheelsWithAlert.toMutableMap().apply {
                    this["P${wheel}"] = inAlert
                }

            currentUiState.copy(
                wheel = "P${wheel}",
                battery = decodeAlertDataFrame(
                    dataFrame,
                    SensorAlertDataFrame.LOW_BATTERY
                ),
                pression = Pair(pression, pressureStatus),
                temperature = Pair(
                    temperature,
                    temperatureStatus
                ),
                timestamp = getCurrentDate(pattern = "dd/MM/yyyy HH:mm:ss"),
                wheelsWithAlert = newMap
            )
        }
    }


    fun getPositionCoordinates(monitorId: Int) {
        viewModelScope.launch {
            apiTpmsUseCase.doGetPositionCoordinates(monitorUiState.value.monitorId)
        }
    }

    fun getSensorDataByWheel(wheelPosition: String) {
        shouldReadManually = false
        viewModelScope.launch {
            val sensorData = apiTpmsUseCase.doGetDiagramMonitor(monitorUiState.value.monitorId)
            when (sensorData) {
                is ResultApi.Success -> {
                    if (!sensorData.data.isNullOrEmpty()) {
                        sensorData.data.filter { data -> data.sensorPosition == wheelPosition }[0]
                            .let {
                                if (it.sensorPosition.contains("P")) {

                                    val tempAlert = when (it.highTemperature) {
                                        true -> SensorAlerts.HighTemperature
                                        false -> SensorAlerts.NoData
                                    }

                                    val pressureAlert =
                                        if (it.lowPressure) SensorAlerts.LowPressure
                                        else if (it.highPressure) SensorAlerts.HighPressure
                                        else SensorAlerts.NoData

                                    _monitorUiState.update { currentUiState ->

                                        val inAlert =
                                            tempAlert != SensorAlerts.NoData || pressureAlert != SensorAlerts.NoData

                                        val newMap =
                                            currentUiState.wheelsWithAlert.toMutableMap().apply {
                                                this[it.sensorPosition] = inAlert
                                            }

                                        currentUiState.copy(
                                            wheel = it.sensorPosition,
                                            temperature = Pair(it.temperature, tempAlert),
                                            pression = Pair(it.psi, pressureAlert),
                                            timestamp = convertDate(it.ultimalectura),
                                            wheelsWithAlert = newMap
                                        )
                                    }
                                }
                            }
                    }
                }

                is ResultApi.Error -> {}
                ResultApi.Loading -> {}
            }
        }
    }

    fun getListSensorData() {
        viewModelScope.launch {
            val sensorData = apiTpmsUseCase.doGetDiagramMonitor(monitorUiState.value.monitorId)
            _positionsUiState.update { sensorData }
        }
    }

    fun getTireDataByDate(
        position: String,
        date: String
    ) {
        viewModelScope.launch {
            _monitorTireUiState.update { ResultApi.Loading }

            val tireData = apiTpmsUseCase.doGetMonitorTireByDate(
                monitorUiState.value.monitorId,
                position,
                date
            )

            when (tireData) {
                is ResultApi.Success -> {
                    _monitorTireUiState.update { tireData }
                }

                is ResultApi.Error -> {}
                ResultApi.Loading -> {}
            }
        }
    }

    fun convertToTireData(diagramData: List<DiagramMonitorResponse>?): List<MonitorTireByDateResponse> =
        diagramData?.map { it.toTireData() } ?: emptyList()

    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Int.MAX_VALUE).any {
            it.service.className == serviceClass.name
        }
    }
}