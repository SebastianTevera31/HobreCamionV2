package com.rfz.appflotal.presentation.ui.monitor.viewmodel

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
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.repository.bluetooth.MonitorDataFrame
import com.rfz.appflotal.data.repository.bluetooth.SensorAlertDataFrame
import com.rfz.appflotal.data.repository.bluetooth.decodeAlertDataFrame
import com.rfz.appflotal.data.repository.bluetooth.decodeDataFrame
import com.rfz.appflotal.domain.bluetooth.BluetoothUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.database.SensorTableUseCase
import com.rfz.appflotal.domain.tpmsUseCase.ApiTpmsUseCase
import com.rfz.appflotal.presentation.ui.utils.responseHelper
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
    HIGH_PRESSURE(R.string.presion_alta),
    LOW_PRESSURE(R.string.presion_baja),
    HIGH_TEMPERATURE(R.string.temperatura_alta),
    NO_DATA(R.string.sin_datos)
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
        MutableStateFlow<ApiResult<List<DiagramMonitorResponse>?>>(ApiResult.Loading)

    val positionsUiState = _positionsUiState.asStateFlow()

    private val _monitorTireUiState =
        MutableStateFlow<ApiResult<List<MonitorTireByDateResponse>?>>(ApiResult.Success(emptyList()))
    val monitorTireUiState = _monitorTireUiState.asStateFlow()

    var shouldReadManually = true

    fun initMonitorData() {
        viewModelScope.launch {
            val userData = getTasksUseCase().first()
            if (userData.isNotEmpty()) {
                val user = userData[0]
                val configInfo = apiTpmsUseCase.doGetConfigurationMonitorById(user.id_monitor)
                responseHelper(response = configInfo) { data ->
                    if (!data.isNullOrEmpty()) {
                        val config = data[0].fldDescription.replace("BASE", "").trim()

                        if (config.isDigitsOnly()) {

                            val wheelsWithAlert =
                                (1..config.toInt()).associate { it -> "P$it" to false }
                                    .toMap()

                            _monitorUiState.update { currentUiState ->
                                currentUiState.copy(
                                    monitorId = user.id_monitor,
                                    numWheels = config.toInt(),
                                    chassisImageUrl = data[0].fldUrlImage,
                                    wheelsWithAlert = wheelsWithAlert,
                                    showDialog = user.id_monitor == 0
                                )
                            }

                            if (_monitorUiState.value.chassisImageUrl.isNotEmpty()) {
                                getDiagramCoordinates()
                            }

                            // Recibe datos Bluetooth
                            readBluetoothData()
                        }
                    }
                }

                if (user.id_monitor == 0) {
                    _monitorUiState.update { currentUiState ->
                        currentUiState.copy(
                            showDialog = true
                        )
                    }
                }

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
                    delay(60000)
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
                temperatureStatus != SensorAlerts.NO_DATA || pressureStatus != SensorAlerts.NO_DATA

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

    private fun getDiagramCoordinates() {
        viewModelScope.launch {
            val coordinates =
                apiTpmsUseCase.doGetPositionCoordinates(monitorUiState.value.monitorId)

            responseHelper(response = coordinates) { response ->
                _monitorUiState.update { currentUiState ->
                    currentUiState.copy(
                        coordinateList = response
                    )
                }
            }
        }
    }

    fun getSensorDataByWheel(wheelPosition: String) {
        shouldReadManually = false
        viewModelScope.launch {
            val sensorData = apiTpmsUseCase.doGetDiagramMonitor(monitorUiState.value.monitorId)

            responseHelper(response = sensorData) { data ->
                if (!data.isNullOrEmpty()) {
                    data.filter { data -> data.sensorPosition == wheelPosition }[0]
                        .let {
                            if (it.sensorPosition.contains("P")) {

                                val tempAlert = when (it.highTemperature) {
                                    true -> SensorAlerts.HIGH_TEMPERATURE
                                    false -> SensorAlerts.NO_DATA
                                }

                                val pressureAlert =
                                    if (it.lowPressure) SensorAlerts.LOW_PRESSURE
                                    else if (it.highPressure) SensorAlerts.HIGH_PRESSURE
                                    else SensorAlerts.NO_DATA

                                _monitorUiState.update { currentUiState ->

                                    val inAlert =
                                        tempAlert != SensorAlerts.NO_DATA || pressureAlert != SensorAlerts.NO_DATA

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
            _monitorTireUiState.update { ApiResult.Loading }

            val tireData = apiTpmsUseCase.doGetMonitorTireByDate(
                monitorUiState.value.monitorId,
                position,
                date
            )

            when (tireData) {
                is ApiResult.Success -> {
                    _monitorTireUiState.update { tireData }
                }

                is ApiResult.Error -> {}
                ApiResult.Loading -> {}
            }
        }
    }

    fun clearMonitorData() {
        _monitorUiState.value = MonitorUiState()
        _positionsUiState.value = ApiResult.Loading
        _monitorTireUiState.value = ApiResult.Success(emptyList())
    }

    fun convertToTireData(diagramData: List<DiagramMonitorResponse>?): List<MonitorTireByDateResponse> =
        diagramData?.map { it.toTireData() } ?: emptyList()
}