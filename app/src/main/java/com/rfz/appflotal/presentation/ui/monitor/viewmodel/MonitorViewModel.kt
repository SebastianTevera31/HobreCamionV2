package com.rfz.appflotal.presentation.ui.monitor.viewmodel

import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.Commons.getBitmapFromDrawable
import com.rfz.appflotal.data.NetworkStatus
import com.rfz.appflotal.data.model.tpms.MonitorTireByDateResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.repository.UnidadPresion
import com.rfz.appflotal.data.repository.UnidadTemperatura
import com.rfz.appflotal.data.repository.assembly.AssemblyTireRepository
import com.rfz.appflotal.data.repository.bluetooth.BluetoothSignalQuality
import com.rfz.appflotal.data.repository.database.SensorDataTableRepository
import com.rfz.appflotal.domain.bluetooth.BluetoothUseCase
import com.rfz.appflotal.domain.database.CoordinatesTableUseCase
import com.rfz.appflotal.domain.database.DataframeTableUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.tpmsUseCase.ApiTpmsUseCase
import com.rfz.appflotal.domain.tpmsUseCase.GetSensorDataByWheelUseCase
import com.rfz.appflotal.domain.tpmsUseCase.MonitorUnitConversionUseCase
import com.rfz.appflotal.domain.tpmsUseCase.UpdateSensorDataUseCase
import com.rfz.appflotal.domain.userpreferences.ObservePressureUnitUseCase
import com.rfz.appflotal.domain.userpreferences.ObserveTemperatureUnitUseCase
import com.rfz.appflotal.domain.userpreferences.SwitchPressureUnitUseCase
import com.rfz.appflotal.domain.userpreferences.SwitchTemperatureUnitUseCase
import com.rfz.appflotal.domain.wifi.WifiUseCase
import com.rfz.appflotal.presentation.ui.utils.responseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

enum class SensorAlerts(@param:StringRes val message: Int) {
    HIGH_PRESSURE(R.string.presion_alta),
    LOW_PRESSURE(R.string.presion_baja),
    HIGH_TEMPERATURE(R.string.temperatura_alta),
    LOW_BATTERY(R.string.bateria_baja),
    NO_DATA(R.string.sin_datos),
    FAST_LEAKAGE(R.string.fuga_rapida),
    SLOW_LEAKAGE(R.string.fuga_lenta),
    REMOVAL(R.string.en_extraccion)
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
    private val updateSensorDataUseCase: UpdateSensorDataUseCase,
    private val getSensorDataByWheelUseCase: GetSensorDataByWheelUseCase,
    private val assemblyTireRepository: AssemblyTireRepository,
    observeTemperatureUnitUseCase: ObserveTemperatureUnitUseCase,
    observePressureUnitUseCase: ObservePressureUnitUseCase,
    private val switchTemperatureUnitUseCase: SwitchTemperatureUnitUseCase,
    private val switchPressureUnitUseCase: SwitchPressureUnitUseCase,
    private val monitorUnitConversionUseCase: MonitorUnitConversionUseCase,
    @param:ApplicationContext private val context: Context
) : ViewModel() {
    private var _monitorUiState: MutableStateFlow<MonitorUiState> =
        MutableStateFlow(MonitorUiState())
    val monitorUiState = _monitorUiState.asStateFlow()

    private val temperatureUnit = observeTemperatureUnitUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UnidadTemperatura.CELCIUS
    )

    private val pressureUnit = observePressureUnitUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UnidadPresion.PSI
    )

    private val _positionsUiState =
        MutableStateFlow<ApiResult<List<MonitorTireByDateResponse>?>>(ApiResult.Loading)
    val positionsUiState = _positionsUiState.asStateFlow()

    private val _filteredTiresUiState =
        MutableStateFlow<ApiResult<List<MonitorTireByDateResponse>?>>(ApiResult.Success(emptyList()))
    val filteredTiresUiState = _filteredTiresUiState.asStateFlow()

    private val _tireUiState = MutableStateFlow(TireUiState())

    // Estado para la UI con valores convertidos dinámicamente
    val tireUiState = combine(
        _tireUiState,
        pressureUnit,
        temperatureUnit
    ) { state, pUnit, tUnit ->
        val converted = monitorUnitConversionUseCase(
            state.rawTemperature,
            tUnit,
            state.rawPressure,
            pUnit
        )
        state.copy(
            pressure = state.pressure.copy(first = converted.pressure),
            temperature = state.temperature.copy(first = converted.temperature)
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        TireUiState()
    )

    private var _wifiStatus: MutableStateFlow<NetworkStatus> =
        MutableStateFlow(NetworkStatus.Connected)

    val wifiStatus = _wifiStatus.asStateFlow()

    var shouldReadManually = true

    init {
        viewModelScope.launch {
            val data = getTasksUseCase().first()
            if (data.isNotEmpty()) {
                val user = data[0]
                _monitorUiState.update { currentUiState ->
                    currentUiState.copy(
                        monitorId = user.id_monitor,
                    )
                }
            }
        }

        viewModelScope.launch {
            wifiUseCase().collect { status ->
                _wifiStatus.update { status }
            }
        }

        viewModelScope.launch {
            updateAssemblyStatus()
        }

        setUnits()
        readBluetoothData()
        statusObserver()
    }

    fun initMonitorData() {
        _monitorUiState.update { currentUiState -> currentUiState.copy(showView = false) }

        viewModelScope.launch {
            val userData = getTasksUseCase().first()
            if (userData.isNotEmpty()) {
                val user = userData[0]
                val baseConfig = if (!user.baseConfiguration.isEmpty()) getBaseConfigImage(
                    user.baseConfiguration.replace("BASE", "")
                        .trim().toInt()
                ) else null

                val uiState = _monitorUiState

                uiState.update { currentUiState ->
                    currentUiState.copy(
                        monitorId = user.id_monitor,
                        baseConfig = baseConfig,
                        showDialog = user.id_monitor == 0
                    )
                }

                // Traer información del servicio
                getConfigData()

                // Controlar si mostrar la vista
                _monitorUiState.update { currentUiState -> currentUiState.copy(showView = true) }
            }
        }
    }

    private suspend fun getConfigData() {
        val uiState = _monitorUiState
        if (uiState.value.monitorId != 0) {
            if (_wifiStatus.value == NetworkStatus.Connected) {
                coordinatesTableUseCase.deleteCoordinates(uiState.value.monitorId)
                assemblyTireRepository.refreshMountedTires()
                mapTires()
            } else {
                val localCoordinates = coordinatesTableUseCase
                    .getCoordinates(uiState.value.monitorId)
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

    private fun setUnits() {
        viewModelScope.launch {
            pressureUnit.collect { unit ->
                _monitorUiState.update { currentUiState ->
                    currentUiState.copy(pressureUnit = unit)
                }
            }
        }

        viewModelScope.launch {
            temperatureUnit.collect { unit ->
                _monitorUiState.update { currentUiState ->
                    currentUiState.copy(temperatureUnit = unit)
                }
            }
        }
    }

    private fun mapTires() {
        viewModelScope.launch {
            val uiState = monitorUiState.value
            val monitorId = uiState.monitorId

            val sensorDataDeferred = async { apiTpmsUseCase.doGetDiagramMonitor(monitorId) }
            val baseCoordinatesDeferred =
                async { apiTpmsUseCase.doGetPositionCoordinates(monitorId) }
            val sensorData = sensorDataDeferred.await()
            val baseCoordinates = baseCoordinatesDeferred.await()

            responseHelper(baseCoordinates) { coords ->
                responseHelper(sensorData) { tireInfo ->
                    val tireByPos =
                        tireInfo.orEmpty().associateBy { it.sensorPosition.trim().uppercase() }

                    val tires = coords.orEmpty().map { info ->
                        val c = tireByPos[info.position]
                        MonitorTire(
                            sensorPosition = c?.sensorPosition ?: info.position,
                            isAssembled = c?.isAssembled == true,
                            inAlert = getIsTireInAlertByApi(
                                highTemperatureStatus = c?.highTemperature,
                                highPressureStatus = c?.highPressure,
                                lowPressureStatus = c?.lowPressure,
                                batteryStatus = c?.lowBattery,
                                flatTireStatus = c?.puncture
                            ),
                            isActive = c?.sensorId != 0,
                            xPosition = info.fldPositionX,
                            yPosition = info.fldPositionY,
                        )
                    }.sortedBy {
                        it.sensorPosition.removePrefix("P").trim().toIntOrNull() ?: Int.MAX_VALUE
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

    private suspend fun updateAssemblyStatus() {
        assemblyTireRepository.observeAssemblyTire()
            .collect { assembledTiresFromDb ->
                val assembledPositions = assembledTiresFromDb.map { it.positionTire }.toSet()

                _monitorUiState.update { currentUiState ->
                    val updatedTireList = currentUiState.listOfTires.map { tire ->
                        if (tire.sensorPosition in assembledPositions) {
                            if (!tire.isAssembled) tire.copy(isAssembled = true) else tire
                        } else {
                            if (tire.isAssembled) tire.copy(isAssembled = false) else tire
                        }
                    }
                    currentUiState.copy(listOfTires = updatedTireList)
                }

                val currentlySelectedTire = _tireUiState.value.currentTire
                if (currentlySelectedTire.isNotBlank()) {
                    getSensorDataByWheel(currentlySelectedTire)
                }
            }
    }

    private fun readBluetoothData() {
        viewModelScope.launch {
            bluetoothUseCase().collect { data ->
                val monitorId = monitorUiState.value.monitorId
                if (monitorId != 0) {
                    val rssi = data.rssi
                    val bluetoothSignalQuality = data.bluetoothSignalQuality

                    _monitorUiState.update { currentUiState ->
                        currentUiState.copy(
                            signalIntensity = Pair(
                                bluetoothSignalQuality, if (rssi != null) "$rssi dBm" else "N/A"
                            ),
                            isBluetoothOn = data.isBluetoothOn
                        )
                    }

                    if (shouldReadManually) {
                        Log.d("MonitorViewModel", "$data")
                        val dataFrame = data.dataFrame

                        if (bluetoothSignalQuality == BluetoothSignalQuality.Desconocida || dataFrame == null) {
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
            withContext(Dispatchers.IO) {
                while (isActive) {
                    val uiState = _monitorUiState.value
                    val tireUiState = _tireUiState.value
                    val monitorId = uiState.monitorId
                    val tires = updateTiresStatus(
                        listTires = uiState.listOfTires
                    ) { sensorDataTableRepository.getLastData(monitorId) }

                    updateTireState(
                        currentTire = tireUiState.currentTire,
                        tires = tires,
                    ) {
                        _tireUiState.update { currentUiState ->
                            currentUiState.copy(
                                currentTire = "",
                                batteryStatus = SensorAlerts.NO_DATA,
                                pressure = Pair(0f, SensorAlerts.NO_DATA),
                                rawPressure = 0f,
                                temperature = Pair(0f, SensorAlerts.NO_DATA),
                                rawTemperature = 0f,
                                timestamp = "",
                            )
                        }

                        _monitorUiState.update { currentUiState ->
                            currentUiState.copy(listOfTires = tires)
                        }
                    }

                    delay(5 * 60_000L)
                }
            }
        }
    }

    private fun updateSensorData(dataFrame: String, timestamp: String? = null) {
        val result = updateSensorDataUseCase(
            dataFrame = dataFrame,
            currentTires = _monitorUiState.value.listOfTires,
            timestamp = timestamp,
            tempUnit = _monitorUiState.value.temperatureUnit,
            pressureUnit = _monitorUiState.value.pressureUnit
        )

        _monitorUiState.update { it.copy(listOfTires = result.updatedTireList) }
        _tireUiState.update {
            result.newTireUiState.copy(
                rawPressure = result.newTireUiState.pressure.first,
                rawTemperature = result.newTireUiState.temperature.first
            )
        }
    }

    fun getSensorDataByWheel(tireSelected: String) {
        shouldReadManually = false
        viewModelScope.launch {
            val monitorUiState = _monitorUiState.value
            val result = getSensorDataByWheelUseCase(
                monitorId = monitorUiState.monitorId,
                tireSelected = tireSelected,
                currentTires = monitorUiState.listOfTires,
                tempUnit = monitorUiState.temperatureUnit,
                pressureUnit = monitorUiState.pressureUnit
            )

            if (result != null) {
                _tireUiState.update {
                    result.newTireUiState.copy(
                        rawPressure = result.newTireUiState.pressure.first,
                        rawTemperature = result.newTireUiState.temperature.first
                    )
                }
                _monitorUiState.update { currentState ->
                    currentState.copy(listOfTires = result.updatedTireList)
                }
            }
        }
    }

    fun updateSelectedTire(selectedTire: String) {
        _tireUiState.update { currentUiState ->
            currentUiState.copy(
                currentTire = selectedTire
            )
        }
    }

    fun getLastedSensorData() {
        viewModelScope.launch {
            val uiState = monitorUiState.value
            val tires = uiState.listOfTires.associateBy { it.sensorPosition }

            _positionsUiState.update { ApiResult.Loading }

            val sensorData = sensorDataTableRepository.getLastData(uiState.monitorId)

            val filterData = sensorData.filter { tires[it.tire]?.isActive ?: false }
            val sortedData = filterData.map {
                val sensorValue = monitorUnitConversionUseCase(
                    temp = it.temperature.toFloat(),
                    tempUnit = _monitorUiState.value.temperatureUnit,
                    pressure = it.pressure.toFloat(),
                    pressureUnit = _monitorUiState.value.pressureUnit
                )
                it.toTireData().copy(
                    temperature = sensorValue.temperature.toInt(),
                    psi = sensorValue.pressure.toInt()
                )
            }.sortedBy {
                it.tirePosition.replace("P", "").toInt()
            }

            if (sensorData.isNotEmpty()) {
                _positionsUiState.update { ApiResult.Success(sortedData) }
            } else {
                Log.e("MonitorViewModel", "No se encontraron datos")
                _positionsUiState.update { ApiResult.Error() }
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
                    _filteredTiresUiState.update {
                        ApiResult.Success(tireData.data?.map {
                            val sensorValue = monitorUnitConversionUseCase(
                                it.temperature.toFloat(),
                                _monitorUiState.value.temperatureUnit,
                                it.psi.toFloat(),
                                _monitorUiState.value.pressureUnit
                            )

                            it.copy(
                                temperature = sensorValue.temperature.toInt(),
                                psi = sensorValue.pressure.toInt()
                            )
                        })
                    }
                }

                is ApiResult.Error -> {
                    _filteredTiresUiState.update { ApiResult.Error() }
                }

                ApiResult.Loading -> {}
            }
        }
    }

    fun cleanMonitorData() {
        _monitorUiState.value = MonitorUiState()
        _positionsUiState.value = ApiResult.Loading
        cleanFilteredTire()
    }

    fun cleanFilteredTire() {
        _filteredTiresUiState.value = ApiResult.Success(emptyList())
    }


    fun showMonitorDialog(show: Boolean) {
        _monitorUiState.update { currentUiState ->
            currentUiState.copy(showDialog = show)
        }
    }

    fun switchPressureUnit() {
        viewModelScope.launch {
            switchPressureUnitUseCase()
        }
    }

    fun switchTemperatureUnit() {
        viewModelScope.launch {
            switchTemperatureUnitUseCase()
        }
    }


    // Corregir funcion
    fun getBitmapImage() {
        val baseConfig = _monitorUiState.value.baseConfig ?: return

        val imageConfig = getImageConfig(baseConfig)

        val bitmap = getBitmapFromDrawable(imageConfig.image, context)

        _monitorUiState.update { currentUiState ->
            currentUiState.copy(imageDimen = imageConfig.dimen, imageBitmap = bitmap)
        }
    }
}