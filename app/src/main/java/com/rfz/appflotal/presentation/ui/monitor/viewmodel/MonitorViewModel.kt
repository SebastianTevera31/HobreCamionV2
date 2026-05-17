package com.rfz.appflotal.presentation.ui.monitor.viewmodel

import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.Commons.getBitmapFromDrawable
import com.rfz.appflotal.data.NetworkStatus
import com.rfz.appflotal.data.model.assembly.AssemblyTire
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
import com.rfz.appflotal.domain.tpms.ApiTpmsUseCase
import com.rfz.appflotal.domain.tpms.GetSensorDataByWheelUseCase
import com.rfz.appflotal.domain.tpms.MonitorUnitConversionUseCase
import com.rfz.appflotal.domain.tpms.UpdateSensorDataUseCase
import com.rfz.appflotal.domain.userpreferences.ObservePressureUnitUseCase
import com.rfz.appflotal.domain.userpreferences.ObserveTemperatureUnitUseCase
import com.rfz.appflotal.domain.userpreferences.SwitchPressureUnitUseCase
import com.rfz.appflotal.domain.userpreferences.SwitchTemperatureUnitUseCase
import com.rfz.appflotal.domain.wifi.WifiUseCase
import com.rfz.appflotal.presentation.ui.utils.responseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
        MutableStateFlow<ApiResult<List<ListOfTireData>?>>(ApiResult.Loading)
    val positionsUiState = _positionsUiState.asStateFlow()

    private val _filteredTiresUiState =
        MutableStateFlow<ApiResult<List<ListOfTireData>?>>(ApiResult.Success(emptyList()))
    val filteredTiresUiState = _filteredTiresUiState.asStateFlow()

    private val _tireUiState = MutableStateFlow(TireUiState())

    private var manualSelectionJob: Job? = null

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

    var shouldReadAuto = true // true = Automático, false = Manual (Bloqueado)

    init {
        viewModelScope.launch {
            getTasksUseCase().collect { data ->
                if (data.isNotEmpty()) {
                    val user = data[0]
                    _monitorUiState.update { currentUiState ->
                        currentUiState.copy(
                            monitorId = user.id_monitor,
                            showDialog = user.id_monitor == 0
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            wifiUseCase().collect { status ->
                _wifiStatus.update { status }
            }
        }

        observeAssemblyChanges()
        setUnits()
        readBluetoothData()
        statusObserver()
    }

    fun initMonitorData() {
        // Only set showView to false if we actually have to wait for something
        shouldReadAuto = true

        viewModelScope.launch {
            try {
                val userData = getTasksUseCase().first()
                if (userData.isNotEmpty()) {
                    val user = userData[0]

                    // If monitor is already 0, we can skip showView = false to avoid flickering
                    if (user.id_monitor != 0) {
                        _monitorUiState.update { it.copy(showView = false) }
                    }

                    val baseConfigString = user.baseConfiguration.replace("BASE", "").trim()
                    val baseConfigId = baseConfigString.toIntOrNull()
                    val baseConfig =
                        if (baseConfigId != null) getBaseConfigImage(baseConfigId) else null

                    _monitorUiState.update { currentUiState ->
                        currentUiState.copy(
                            monitorId = user.id_monitor,
                            baseConfig = baseConfig,
                            showDialog = user.id_monitor == 0
                        )
                    }

                    if (user.id_monitor != 0) {
                        getConfigData()
                        getBitmapImage()
                    }
                }
            } catch (e: Exception) {
                Log.e("MonitorViewModel", "Error initializing monitor data", e)
            } finally {
                _monitorUiState.update { it.copy(showView = true) }
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

    private fun observeAssemblyChanges() {
        viewModelScope.launch {
            assemblyTireRepository.observeAssemblyTire().collect { assembledTires ->
                syncAssemblyStatus(assembledTires)
            }
        }
    }

    private fun syncAssemblyStatus(assembledTires: List<AssemblyTire>) {
        val assembledPositions = assembledTires.map { it.positionTire.trim().uppercase() }.toSet()
        val monitorId = _monitorUiState.value.monitorId

        _monitorUiState.update { state ->
            val updatedList = state.listOfTires.map { tire ->
                val isNowAssembled = tire.sensorPosition.trim().uppercase() in assembledPositions

                if (tire.isAssembled != isNowAssembled && monitorId != 0) {
                    viewModelScope.launch {
                        coordinatesTableUseCase.updateAssemblyStatus(
                            monitorId = monitorId,
                            tire = tire.sensorPosition,
                            isAssembled = isNowAssembled
                        )
                    }
                }
                tire.copy(isAssembled = isNowAssembled)
            }
            state.copy(listOfTires = updatedList)
        }

        val currentTire = _tireUiState.value.currentTire
        if (currentTire.isNotBlank()) {
            fetchSensorDataInternal(currentTire)
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

                    val dataFrame = data.dataFrame

                    if (dataFrame != null) {
                        updateSensorData(dataFrame)
                    } else if (bluetoothSignalQuality == BluetoothSignalQuality.Desconocida) {
                        monitorId.let {
                            dataframeTableUseCase.doGetLastRecord(it).forEach { data ->
                                if (data != null) updateSensorData(
                                    data.dataFrame,
                                    data.timestamp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun statusObserver() {
        viewModelScope.launch {
            sensorDataTableRepository.observeLastRecords().collect { sensorData ->
                val activeTireMap = sensorData.associate { it.tire to it.active }

                _monitorUiState.update { currentState ->
                    val updatedTires = currentState.listOfTires.map { tire ->
                        val isActiveInDb = activeTireMap[tire.sensorPosition] ?: false
                        tire.copy(isActive = isActiveInDb)
                    }
                    currentState.copy(listOfTires = updatedTires)
                }

                val currentSelected = _tireUiState.value.currentTire
                val isCurrentActive = activeTireMap[currentSelected] ?: true
                if (!isCurrentActive) {
                    _tireUiState.update { TireUiState() }
                }
            }
        }
    }

    private suspend fun updateSensorData(dataFrame: String, timestamp: String? = null) {
        val result = updateSensorDataUseCase(
            monitorId = _monitorUiState.value.monitorId,
            dataFrame = dataFrame,
            currentTires = _monitorUiState.value.listOfTires,
            timestamp = timestamp,
            tempUnit = _monitorUiState.value.temperatureUnit,
            pressureUnit = _monitorUiState.value.pressureUnit
        )

        _monitorUiState.update { it.copy(listOfTires = result.updatedTireList) }
        val currentSelected = _tireUiState.value.currentTire
        if (shouldReadAuto || result.newTireUiState.currentTire == currentSelected) {
            _tireUiState.update { currentState ->
                result.newTireUiState.copy(
                    rawPressure = result.newTireUiState.pressure.first,
                    rawTemperature = result.newTireUiState.temperature.first,
                    isInspectionAvailable = if (result.newTireUiState.currentTire == currentState.currentTire) {
                        currentState.isInspectionAvailable
                    } else result.newTireUiState.isInspectionAvailable,
                    currentTire = if (result.newTireUiState.currentTire == "") currentState.currentTire else result.newTireUiState.currentTire,
                    isLoading = false
                )
            }
        }
    }

    fun getSensorDataByWheel(tireSelected: String) {
        shouldReadAuto = false
        _tireUiState.update { it.copy(currentTire = tireSelected, isLoading = true) }

        manualSelectionJob?.cancel()

        manualSelectionJob = viewModelScope.launch {
            delay(60000)
            shouldReadAuto = true
        }

        fetchSensorDataInternal(tireSelected)
    }

    private fun fetchSensorDataInternal(tireSelected: String) {
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
                        rawTemperature = result.newTireUiState.temperature.first,
                        isLoading = false
                    )
                }
                _monitorUiState.update { currentState ->
                    currentState.copy(listOfTires = result.updatedTireList)
                }
            } else {
                _tireUiState.update { it.copy(isLoading = false) }
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
                    temperature = sensorValue.temperature,
                    psi = sensorValue.pressure
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

                            it.toTireData().copy(
                                temperature = sensorValue.temperature,
                                psi = sensorValue.pressure
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