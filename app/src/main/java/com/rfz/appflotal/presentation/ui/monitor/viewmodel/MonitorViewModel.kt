package com.rfz.appflotal.presentation.ui.monitor.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.Commons.convertDate
import com.rfz.appflotal.core.util.Commons.getBitmapFromDrawable
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.core.util.Commons.getDateObject
import com.rfz.appflotal.core.util.Commons.validateBluetoothConnectivity
import com.rfz.appflotal.core.util.Positions.findOutPosition
import com.rfz.appflotal.core.util.tpms.getPressure
import com.rfz.appflotal.core.util.tpms.getTemperature
import com.rfz.appflotal.data.NetworkStatus
import com.rfz.appflotal.data.model.tpms.MonitorTireByDateResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.repository.bluetooth.MonitorDataFrame
import com.rfz.appflotal.data.repository.bluetooth.SensorAlertDataFrame
import com.rfz.appflotal.data.repository.bluetooth.decodeAlertDataFrame
import com.rfz.appflotal.data.repository.bluetooth.decodeDataFrame
import com.rfz.appflotal.data.repository.database.SensorDataTableRepository
import com.rfz.appflotal.domain.bluetooth.BluetoothUseCase
import com.rfz.appflotal.domain.database.CoordinatesTableUseCase
import com.rfz.appflotal.domain.database.DataframeTableUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.tpmsUseCase.ApiTpmsUseCase
import com.rfz.appflotal.domain.wifi.WifiUseCase
import com.rfz.appflotal.presentation.ui.utils.responseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

enum class SensorAlerts(@StringRes val message: Int) {
    HIGH_PRESSURE(R.string.presion_alta),
    LOW_PRESSURE(R.string.presion_baja),
    HIGH_TEMPERATURE(R.string.temperatura_alta),
    LOW_BATTERY(R.string.bateria_baja),
    NO_DATA(R.string.sin_datos),
    FUGA_RAPIDA(R.string.fuga_rapida),
    FUGA_LENTA(R.string.fuga_lenta)
}

@HiltViewModel
class MonitorViewModel @Inject constructor(
    private val apiTpmsUseCase: ApiTpmsUseCase,
    private val bluetoothUseCase: BluetoothUseCase,
    private val dataframeTableUseCase: DataframeTableUseCase,
    private val sensorDataTableRepository: SensorDataTableRepository,
    private val getTasksUseCase: GetTasksUseCase,
    private val coordinatesTableUseCase: CoordinatesTableUseCase,
    private val wifiUseCase: WifiUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var _monitorUiState: MutableStateFlow<MonitorUiState> =
        MutableStateFlow(MonitorUiState())
    val monitorUiState = _monitorUiState.asStateFlow()

    private val _positionsUiState =
        MutableStateFlow<ApiResult<List<MonitorTireByDateResponse>?>>(ApiResult.Loading)

    val positionsUiState = _positionsUiState.asStateFlow()

    private val _filteredTiresUiState =
        MutableStateFlow<ApiResult<List<MonitorTireByDateResponse>?>>(ApiResult.Success(emptyList()))
    val filteredTiresUiState = _filteredTiresUiState.asStateFlow()

    private var _wifiStatus: MutableStateFlow<NetworkStatus> =
        MutableStateFlow(NetworkStatus.Connected)
    val wifiStatus = _wifiStatus.asStateFlow()

    var shouldReadManually = true

    init {
        viewModelScope.launch {
            wifiUseCase().collect { status ->
                _wifiStatus.update { status }
            }
        }

        readBluetoothData()
        //statusObserver()
    }

    fun initMonitorData() {
        _monitorUiState.update { currentUiState -> currentUiState.copy(showView = false) }

        viewModelScope.launch {
            val userData = getTasksUseCase().first()
            if (userData.isNotEmpty()) {
                val user = userData[0]
                val baseConfig = getBaseConfigImage(
                    user.baseConfiguration.replace("BASE", "")
                        .trim().toInt()
                )

                val uiState = _monitorUiState

                uiState.update { currentUiState ->
                    currentUiState.copy(
                        monitorId = user.id_monitor,
                        baseConfig = baseConfig,
                        showDialog = user.id_monitor == 0
                    )
                }
            }

            getConfigData()
            _monitorUiState.update { currentUiState -> currentUiState.copy(showView = true) }
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
                    val tireByPos =
                        tireInfo.orEmpty().associateBy { it.sensorPosition.trim().uppercase() }

                    val tires = coords.orEmpty().map { info ->
                        val c = tireByPos[info.position]
                        Tire(
                            sensorPosition = c?.sensorPosition ?: info.position,
                            inAlert = c?.highPressure == true || c?.lowPressure == true || c?.highPressure == true || c?.lowBattery == true,
                            isActive = c?.sensorId != 0,
                            xPosition = info.fldPositionX,
                            yPosition = info.fldPositionY
                        )
                    }.sortedBy {
                        it.sensorPosition.removePrefix("P").trim().toIntOrNull()
                            ?: Int.MAX_VALUE
                    }

                    _monitorUiState.update { currentUiState ->
                        currentUiState.copy(
                            imageDimen = getImageDimens(currentUiState.baseConfig),
                            listOfTires = tires
                        )
                    }
                }
            }

            // Insertar registro de ruedas en la base de datos.
            coordinatesTableUseCase.insertCoordinates(monitorId, _monitorUiState.value.listOfTires)
        }
    }

    private suspend fun getConfigData() {
        val uiState = _monitorUiState
        if (uiState.value.monitorId != 0) {
            if (_wifiStatus.value == NetworkStatus.Connected) {
                coordinatesTableUseCase.deleteCoordinates(uiState.value.monitorId)
                mapTires()
            } else {
                val localCoordinates =
                    coordinatesTableUseCase.getCoordinates(uiState.value.monitorId)
                uiState.update { currentUiState ->
                    currentUiState.copy(
                        listOfTires = localCoordinates.map { it.toTire() }
                    )
                }
            }
        } else {
            _monitorUiState.update { currentUiState ->
                currentUiState.copy(
                    showDialog = true
                )
            }
        }
    }

    private fun readBluetoothData() {
        viewModelScope.launch {
            bluetoothUseCase().collect { data ->
                val monitorId = monitorUiState.value.monitorId
                if (monitorId != 0) {
                    if (shouldReadManually) {
                        Log.d("MonitorViewModel", "$data")
                        val rssi = data.rssi
                        val bluetoothSignalQuality = data.bluetoothSignalQuality

                        val dataFrame = data.dataFrame

                        if (!validateBluetoothConnectivity(bluetoothSignalQuality) || dataFrame == null) {
                            Log.d("MonitorViewModel", "Monitor ID $monitorId")
                            // Se agrega la funcion Let como seguridad, sin embargo el Id debe existir en esta parte
                            monitorId.let {
                                dataframeTableUseCase.doGetLastRecord(it).forEach { data ->
                                    if (data != null) updateSensorData(
                                        data.dataFrame,
                                        data.timestamp
                                    )
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
    }

    private fun statusObserver() {
        viewModelScope.launch {
            val uiState = _monitorUiState.value
            val monitorId = uiState.monitorId
            withContext(Dispatchers.IO) {
                while (isActive) {
                    val tires = updateTiresStatus(
                        listTires = uiState.listOfTires
                    ) { sensorDataTableRepository.getLastData(monitorId) }

                    updateTireState(
                        currentTire = uiState.currentTire,
                        tires = tires,
                    ) { tire ->
                        _monitorUiState.update { currentUiState ->
                            currentUiState.copy(
                                currentTire = "",
                                batteryStatus = SensorAlerts.NO_DATA,
                                pression = Pair(0f, SensorAlerts.NO_DATA),
                                temperature = Pair(0f, SensorAlerts.NO_DATA),
                                timestamp = ""
                            )
                        }
                    }

                    _monitorUiState.update { currentUiState ->
                        currentUiState.copy(
                            listOfTires = tires
                        )
                    }
                    delay(60_000L)
                }
            }
        }
    }

    private fun updateSensorData(dataFrame: String, timestamp: String? = null) {
        _monitorUiState.update { currentUiState ->
            val tire = decodeDataFrame(dataFrame, MonitorDataFrame.POSITION_WHEEL).toInt()
            val realTire = findOutPosition("P${tire}")

            val pressure = getPressure(dataFrame)
            val pressureStatus = decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.PRESSURE)

            val temperature = getTemperature(dataFrame)
            val temperatureStatus =
                decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.HIGH_TEMPERATURE)

            val batteryStatus =
                decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.LOW_BATTERY)

            val inAlert = temperatureStatus != SensorAlerts.NO_DATA
                    || pressureStatus != SensorAlerts.NO_DATA
                    || batteryStatus != SensorAlerts.NO_DATA

            val newList = currentUiState.listOfTires.toMutableList().map { tireData ->
                if (tireData.sensorPosition == realTire) tireData.copy(
                    inAlert = inAlert,
                    isActive = true
                ) else tireData
            }

            val time = if (timestamp != null) {
                val getDate = getDateObject(timestamp)
                getCurrentDate(date = getDate, pattern = "dd/MM/yyyy HH:mm:ss")
            } else getCurrentDate(pattern = "dd/MM/yyyy HH:mm:ss")

            currentUiState.copy(
                currentTire = realTire,
                pression = Pair(pressure, pressureStatus),
                temperature = Pair(temperature, temperatureStatus),
                timestamp = time,
                batteryStatus = batteryStatus,
                listOfTires = newList
            )
        }
    }

    fun getSensorDataByWheel(tireSelected: String) {
        shouldReadManually = false
        viewModelScope.launch {
            val uiState = _monitorUiState.value
            val data =
                sensorDataTableRepository.getLastDataByTire(uiState.monitorId, tireSelected)

            if (data != null) {
                val pressureStatus = if (data.lowPressureAlert) SensorAlerts.LOW_PRESSURE
                else if (data.highPressureAlert) SensorAlerts.HIGH_PRESSURE
                else SensorAlerts.NO_DATA

                val temperatureStatus =
                    if (data.highTemperatureAlert) SensorAlerts.HIGH_TEMPERATURE
                    else SensorAlerts.NO_DATA

                val batteryStatus = if (data.lowBatteryAlert) SensorAlerts.LOW_BATTERY
                else SensorAlerts.NO_DATA

                val inAlert = getIsTireInAlert(
                    tempAlert = temperatureStatus,
                    pressureAlert = pressureStatus,
                    batteryAlert = batteryStatus
                )

                _monitorUiState.update { currentUiState ->
                    val newList =
                        currentUiState.listOfTires.toMutableList().map { tire ->
                            if (tire.sensorPosition == tireSelected) tire.copy(
                                inAlert = inAlert
                            ) else tire
                        }

                    currentUiState.copy(
                        currentTire = data.tire,

                        pression = Pair(data.pressure.toFloat(), pressureStatus),

                        temperature = Pair(
                            data.temperature.toFloat(),
                            temperatureStatus
                        ),

                        timestamp = convertDate(data.timestamp, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
                        batteryStatus = batteryStatus,
                        listOfTires = newList
                    )
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

    fun getLastedSensorData() {
        viewModelScope.launch {
            _positionsUiState.update { ApiResult.Loading }
            if (_wifiStatus.value == NetworkStatus.Connected) {
                val sensorData =
                    apiTpmsUseCase.doGetDiagramMonitor(monitorUiState.value.monitorId)
                responseHelper(
                    sensorData,
                    onError = { _positionsUiState.update { ApiResult.Error(message = "Error al consultar lsos datos") } }) { data ->
                    val filterData =
                        data?.filter { it.sensorId != 0 } // Representa si esta activo
                    val sortedData = filterData?.map { it.toTireData() }
                        ?.sortedBy { it.tirePosition.replace("P", "").toInt() }
                        ?: emptyList()
                    _positionsUiState.update {
                        ApiResult.Success(sortedData)
                    }
                }
            } else {
                val sensorData =
                    sensorDataTableRepository.getLastData(monitorUiState.value.monitorId)

                val filterData = sensorData.filter { it.active }
                val sortedData = filterData.map { it.toTireData() }
                    .sortedBy { it.tirePosition.replace("P", "").toInt() }

                if (sensorData.isNotEmpty()) {
                    _positionsUiState.update { ApiResult.Success(sortedData) }
                } else {
                    Log.e("MonitorViewModel", "No se encontraron datos")
                    _positionsUiState.update { ApiResult.Error() }
                }
            }
        }
    }

    fun getTireDataByDate(
        position: String,
        date: String
    ) {
        viewModelScope.launch {
            _filteredTiresUiState.update { ApiResult.Loading }
            val tireData = apiTpmsUseCase.doGetMonitorTireByDate(
                monitorUiState.value.monitorId,
                position,
                date
            )
            when (tireData) {
                is ApiResult.Success -> {
                    _filteredTiresUiState.update { tireData }
                }

                is ApiResult.Error -> {}
                ApiResult.Loading -> {}
            }
        }
    }

    fun clearMonitorData() {
        _monitorUiState.value = MonitorUiState()
        _positionsUiState.value = ApiResult.Loading
        clearFilteredTire()
    }

    fun clearFilteredTire() {
        _filteredTiresUiState.value = ApiResult.Success(emptyList())
    }

    fun getBitmapImage(): Bitmap? {
        val baseConfig = _monitorUiState.value.baseConfig ?: return null

        val imageConfig = when (baseConfig) {
            BaseConfig.BASE6 -> ImageConfig(Pair(620, 327), R.drawable.base6)
            BaseConfig.BASE10 -> ImageConfig(Pair(628, 327), R.drawable.base22)
            BaseConfig.BASE22 -> ImageConfig(Pair(1280, 425), R.drawable.base22)
            BaseConfig.BASE38 -> ImageConfig(Pair(1780, 327), R.drawable.base32)
        }

        _monitorUiState.update { currentUiState ->
            currentUiState.copy(imageDimen = imageConfig.dimen)
        }

        return getBitmapFromDrawable(imageConfig.image, context)
    }
}