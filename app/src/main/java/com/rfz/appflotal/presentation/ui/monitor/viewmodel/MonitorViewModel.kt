package com.rfz.appflotal.presentation.ui.monitor.viewmodel

import android.util.Log
import androidx.annotation.StringRes
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.Commons.convertDate
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.core.util.Commons.getDateObject
import com.rfz.appflotal.core.util.Commons.validateBluetoothConnectivity
import com.rfz.appflotal.core.util.Positions.findOutPosition
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

    LOW_BATTERY(R.string.bateria_baja),
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
                        _monitorUiState.update { currentUiState ->
                            currentUiState.copy(
                                monitorId = user.id_monitor,
                                chassisImageUrl = data[0].fldUrlImage,
                                showDialog = user.id_monitor == 0
                            )
                        }

                        if (_monitorUiState.value.chassisImageUrl.isNotEmpty()) {
                            mapTires()
                        }

                        // Recibe datos Bluetooth
                        readBluetoothData()
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

    private fun mapTires() {
        viewModelScope.launch {
            val uiState = monitorUiState.value
            val monitorId = uiState.monitorId

            val sensorData = apiTpmsUseCase.doGetDiagramMonitor(monitorId)
            val baseCoordinates = apiTpmsUseCase.doGetPositionCoordinates(monitorId)

            responseHelper(baseCoordinates) { coords ->
                responseHelper(sensorData) { tireInfo ->
                    val coordByPos = coords.orEmpty()
                        .associateBy { it.position.trim().uppercase() }

                    val tires = tireInfo.orEmpty().map { info ->
                        val key = info.sensorPosition.trim().uppercase()
                        val c = coordByPos[key]
                        Tire(
                            sensorPosition = info.sensorPosition,
                            inAlert = info.highTemperature || info.lowPressure || info.highPressure || info.lowBattery,
                            isActive = info.sensorId != 0,
                            xPosition = c?.fldPositionX ?: 0,
                            yPosition = c?.fldPositionY ?: 0
                        )
                    }

                    _monitorUiState.update { currentUiState ->
                        currentUiState.copy(
                            imageDimen = getImageDimens(currentUiState.chassisImageUrl),
                            listOfTires = tires.sortedBy {
                                it.sensorPosition.removePrefix("P").trim().toIntOrNull()
                                    ?: Int.MAX_VALUE
                            },
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
                                if (data != null) updateSensorData(data.dataFrame, data.timestamp)
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

    private fun updateSensorData(dataFrame: String, timestamp: String? = null) {
        _monitorUiState.update { currentUiState ->
            val tire = decodeDataFrame(dataFrame, MonitorDataFrame.POSITION_WHEEL).toInt()
            val realTire = findOutPosition("P${tire}")

            val pressionValue = decodeDataFrame(dataFrame, MonitorDataFrame.PRESSION)

            val pressureStatus = decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.PRESSURE)

            val temperatureValue =
                decodeDataFrame(dataFrame, MonitorDataFrame.TEMPERATURE)

            val temperatureStatus =
                decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.HIGH_TEMPERATURE)

            val pression = (pressionValue.toFloat() * 100).roundToInt() / 100f

            val temperature =
                if (temperatureValue.isDigitsOnly()) temperatureValue.toFloat() else 0f

            val batteryStatus = decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.LOW_BATTERY)

            val inAlert = temperatureStatus != SensorAlerts.NO_DATA
                    || pressureStatus != SensorAlerts.NO_DATA
                    || batteryStatus != SensorAlerts.NO_DATA

            val newList = currentUiState.listOfTires.toMutableList().map { tire ->
                if (tire.sensorPosition == realTire) tire.copy(inAlert = inAlert) else tire
            }

            val time = if (timestamp != null) {
                val getDate = getDateObject(timestamp)
                getCurrentDate(date = getDate, pattern = "dd/MM/yyyy HH:mm:ss")
            } else getCurrentDate(pattern = "dd/MM/yyyy HH:mm:ss")

            currentUiState.copy(
                currentTire = realTire,

                pression = Pair(pression, pressureStatus),

                temperature = Pair(
                    temperature,
                    temperatureStatus
                ),

                timestamp = time,
                batteryStatus = batteryStatus,
                listOfTires = newList
            )
        }
    }

    fun getSensorDataByWheel(tireSelected: String) {
        shouldReadManually = false
        viewModelScope.launch {
            val sensorData = apiTpmsUseCase.doGetDiagramMonitor(monitorUiState.value.monitorId)

            responseHelper(response = sensorData) { data ->
                if (!data.isNullOrEmpty()) {
                    val result = data.filter { data -> data.sensorPosition == tireSelected }
                    if (!result.isEmpty()) {
                        result[0].let {
                            if (it.sensorPosition.contains("P")) {

                                val tempAlert = getAlertType(it.highTemperature)

                                val pressureAlert = getPressureAlert(
                                    lowPressure = it.lowPressure,
                                    highPressure = it.highPressure
                                )

                                val batteryAlert = getBatteryAlert(it.lowBattery)

                                _monitorUiState.update { currentUiState ->

                                    val inAlert = getIsTireInAlert(
                                        tempAlert = tempAlert,
                                        pressureAlert = pressureAlert,
                                        batteryAlert = batteryAlert
                                    )

                                    val newList =
                                        currentUiState.listOfTires.toMutableList().map { tire ->
                                            if (tire.sensorPosition == it.sensorPosition) tire.copy(
                                                inAlert = inAlert
                                            ) else tire
                                        }

                                    currentUiState.copy(
                                        temperature = Pair(it.temperature, tempAlert),
                                        pression = Pair(it.psi, pressureAlert),
                                        timestamp = convertDate(it.ultimalectura),
                                        listOfTires = newList,
                                        batteryStatus = batteryAlert
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun updateSelectedTire(selectedTire: String) {
        _monitorUiState.update { currentUiState ->
            currentUiState.copy(
                currentTire = selectedTire
            )
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
        diagramData?.map { it.toTireData() }?.sortedBy { it.tirePosition.replace("P", "").toInt() }
            ?: emptyList()
}