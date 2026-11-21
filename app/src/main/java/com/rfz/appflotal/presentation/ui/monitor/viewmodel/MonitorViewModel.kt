package com.rfz.appflotal.presentation.ui.monitor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.BuildConfig
import com.rfz.appflotal.data.NetworkStatus
import com.rfz.appflotal.data.repository.UnidadOdometro
import com.rfz.appflotal.data.repository.UnidadPresion
import com.rfz.appflotal.data.repository.UnidadTemperatura
import com.rfz.appflotal.data.repository.bluetooth.BluetoothSignalQuality
import com.rfz.appflotal.data.repository.database.SensorDataTableRepository
import com.rfz.appflotal.domain.bluetooth.BluetoothUseCase
import com.rfz.appflotal.domain.database.DataframeTableUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.login.PassWizardUseCase
import com.rfz.appflotal.domain.monitor.CargarDiagramaUseCase
import com.rfz.appflotal.domain.monitor.normalizarPosicion
import com.rfz.appflotal.domain.monitor.ObservarLlantasActivasUseCase
import com.rfz.appflotal.domain.monitor.ObservarPosicionesMontadasUseCase
import com.rfz.appflotal.domain.tpms.GetSensorDataByWheelUseCase
import com.rfz.appflotal.domain.tpms.MonitorUnitConversionUseCase
import com.rfz.appflotal.domain.tpms.UpdateSensorDataUseCase
import com.rfz.appflotal.domain.tpms.isInspectionAvailable
import com.rfz.appflotal.domain.userpreferences.ObserveOdometerUnitUseCase
import com.rfz.appflotal.domain.userpreferences.ObservePressureUnitUseCase
import com.rfz.appflotal.domain.userpreferences.ObserveTemperatureUnitUseCase
import com.rfz.appflotal.domain.userpreferences.SwitchOdometerUnitUseCase
import com.rfz.appflotal.domain.userpreferences.SwitchPressureUnitUseCase
import com.rfz.appflotal.domain.userpreferences.SwitchTemperatureUnitUseCase
import com.rfz.appflotal.domain.wifi.WifiUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class MonitorViewModel @Inject constructor(
    private val bluetoothUseCase: BluetoothUseCase,
    private val dataframeTableUseCase: DataframeTableUseCase,
    private val sensorDataTableRepository: SensorDataTableRepository,
    private val getTasksUseCase: GetTasksUseCase,
    private val wifiUseCase: WifiUseCase,
    private val updateSensorDataUseCase: UpdateSensorDataUseCase,
    private val getSensorDataByWheelUseCase: GetSensorDataByWheelUseCase,
    private val cargarDiagrama: CargarDiagramaUseCase,
    observarPosicionesMontadas: ObservarPosicionesMontadasUseCase,
    private val observarLlantasActivas: ObservarLlantasActivasUseCase,
    observeTemperatureUnitUseCase: ObserveTemperatureUnitUseCase,
    observePressureUnitUseCase: ObservePressureUnitUseCase,
    private val switchTemperatureUnitUseCase: SwitchTemperatureUnitUseCase,
    private val switchPressureUnitUseCase: SwitchPressureUnitUseCase,
    observeOdometerUnitUseCase: ObserveOdometerUnitUseCase,
    private val switchOdometerUnitUseCase: SwitchOdometerUnitUseCase,
    private val monitorUnitConversionUseCase: MonitorUnitConversionUseCase,
    private val passWizardUseCase: PassWizardUseCase,
) : ViewModel() {
    private var _monitorUiState: MutableStateFlow<MonitorUiState> =
        MutableStateFlow(MonitorUiState())

    /**
     * Posiciones montadas según la tabla local de montajes: es la fuente de verdad de
     * `isAssembled`. `null` mientras Room no ha emitido, para no marcar todo como desmontado.
     */
    private val assembledPositions: StateFlow<Set<String>?> =
        observarPosicionesMontadas().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /**
     * `ultimainspeccion` por posición según sensor_data (la llena la sincronización del
     * diagrama y la actualiza cada inspección registrada). Una posición sin registro no
     * aparece en el mapa: no hay inspección conocida.
     */
    private val lastInspectionByPosition: StateFlow<Map<String, String?>> =
        flow { emitAll(sensorDataTableRepository.observeLastRecords()) }
            .map { records ->
                records.associate { it.tire.normalizarPosicion() to it.lastInspection }
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    /**
     * `isAssembled` se deriva de [assembledPositions] al exponer el estado, de modo que ninguna
     * escritura concurrente de `listOfTires` (Bluetooth, servidor) pueda dejarlo desactualizado.
     * Dentro del ViewModel se lee y escribe siempre [_monitorUiState].
     */
    val monitorUiState: StateFlow<MonitorUiState> =
        combine(_monitorUiState, assembledPositions) { state, assembled ->
            if (assembled == null) state
            else state.copy(listOfTires = state.listOfTires.withAssembly(assembled))
        }.stateIn(viewModelScope, SharingStarted.Eagerly, _monitorUiState.value)

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

    private val odometerUnit = observeOdometerUnitUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UnidadOdometro.KILOMETROS
    )

    private val _tireUiState = MutableStateFlow(TireUiState())

    private var manualSelectionJob: Job? = null

    /**
     * Estado de la llanta seleccionada para la UI. Unidades, `isAssembled` (tabla local de
     * montajes) e `isInspectionAvailable` (montajes + `ultimainspeccion` de sensor_data) se
     * derivan aquí, de modo que los botones del panel siempre reflejan las tablas aunque la
     * llanta no tenga lecturas o se registre una inspección/montaje con el panel abierto.
     */
    val tireUiState = combine(
        _tireUiState,
        pressureUnit,
        temperatureUnit,
        assembledPositions,
        lastInspectionByPosition
    ) { state, pUnit, tUnit, assembled, lastInspections ->
        val converted = monitorUnitConversionUseCase(
            state.rawTemperature,
            tUnit,
            state.rawPressure,
            pUnit
        )
        val position = state.currentTire.normalizarPosicion()
        state.copy(
            pressure = state.pressure.copy(first = converted.pressure),
            temperature = state.temperature.copy(first = converted.temperature),
            isAssembled = assembled?.let { position in it } ?: state.isAssembled,
            isInspectionAvailable = position.isNotEmpty() &&
                    isInspectionAvailable(lastInspections[position])
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

    // Evita que el Wizard reaparezca mientras Room aún no guarda passWizard.
    private var wizardFinished = false

    // TODO: solo para pruebas. Fuerza el wizard aunque el usuario ya tenga monitor. Regresar a false.
    private val FORCE_SETUP_WIZARD = BuildConfig.DEBUG && false

    /** Finalizar o cancelar el registro del monitor: el Wizard ya no vuelve a salir. */
    fun finishSetupWizard() {
        wizardFinished = true
        _monitorUiState.update { it.copy(showSetupWizard = false, showDialog = false) }
        viewModelScope.launch { passWizardUseCase() }
    }

    init {
        viewModelScope.launch {
            getTasksUseCase().collect { data ->
                if (data.isNotEmpty()) {
                    val user = data[0]
                    _monitorUiState.update { currentUiState ->
                        currentUiState.copy(
                            monitorId = user.id_monitor,
                            monitorMac = user.monitorMac,
                            showDialog = user.id_monitor == 0
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            wifiUseCase().collect { status ->
                _wifiStatus.update { status }
                // Reenvía un Wizard completado sin red (al abrir la app y al volver la conexión).
                if (status == NetworkStatus.Connected) launch { passWizardUseCase.syncPending() }
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
                            monitorMac = user.monitorMac,
                            baseConfig = baseConfig,
                            showDialog = user.id_monitor == 0,
                            showSetupWizard = !user.passWizard && !wizardFinished
                        )
                    }

                    if (user.id_monitor != 0) {
                        getConfigData()
                        aplicarImagenDiagrama()
                    }
                }
            } catch (_: Exception) {
                // Sin datos de usuario o de configuración, la vista se muestra vacía en lugar de colgarse.
            } finally {
                _monitorUiState.update { it.copy(showView = true) }
            }
        }
    }

    private fun getConfigData() {
        val monitorId = _monitorUiState.value.monitorId
        if (monitorId == 0) {
            _monitorUiState.update { it.copy(showDialog = true) }
            return
        }
        val conRed = _wifiStatus.value == NetworkStatus.Connected
        viewModelScope.launch {
            val llantas = cargarDiagrama(monitorId, conRed)
            _monitorUiState.update { it.copy(listOfTires = llantas.map { l -> l.aMonitorTire() }) }
            applyLocalAssemblyStatus()
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

        viewModelScope.launch {
            odometerUnit.collect { unit ->
                _monitorUiState.update { currentUiState ->
                    currentUiState.copy(odometerUnit = unit)
                }
            }
        }
    }

    // La lista que llega del servidor puede no incluir montajes locales aún no sincronizados,
    // y observeAssemblyChanges() solo reacciona cuando la tabla local cambia.
    private suspend fun applyLocalAssemblyStatus() {
        syncAssemblyStatus(assembledPositions.filterNotNull().first())
    }

    private fun observeAssemblyChanges() {
        viewModelScope.launch {
            assembledPositions.filterNotNull().collect { assembled ->
                syncAssemblyStatus(assembled)
            }
        }
    }

    // La UI ya deriva isAssembled de assembledPositions; esto alinea la lista interna. La caché de
    // coordenadas no se toca: al leerla sin red también se le aplican los montajes locales.
    private fun syncAssemblyStatus(assembled: Set<String>) {
        _monitorUiState.update { state ->
            state.copy(listOfTires = state.listOfTires.withAssembly(assembled))
        }

        val currentTire = _tireUiState.value.currentTire
        if (currentTire.isNotBlank()) {
            fetchSensorDataInternal(currentTire)
        }
    }

    private fun readBluetoothData() {
        viewModelScope.launch {
            bluetoothUseCase().collect { data ->
                val monitorId = _monitorUiState.value.monitorId
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
            observarLlantasActivas().collect { activeTireMap ->
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
        val result = updateSensorDataUseCase(dataFrame = dataFrame, timestamp = timestamp)

        updateTire(result.position) { it.copy(inAlert = result.inAlert, isActive = true) }
        val currentSelected = _tireUiState.value.currentTire
        if (shouldReadAuto || result.position == currentSelected) {
            _tireUiState.update { currentState ->
                result.lectura.aTireUiState().copy(
                    currentTire = result.position.ifEmpty { currentState.currentTire },
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
            delay(60000.milliseconds)
            shouldReadAuto = true
        }

        fetchSensorDataInternal(tireSelected)
    }

    private fun fetchSensorDataInternal(tireSelected: String) {
        viewModelScope.launch {
            val result = getSensorDataByWheelUseCase(
                monitorId = _monitorUiState.value.monitorId,
                tireSelected = tireSelected
            )

            if (result != null) {
                _tireUiState.update { result.lectura.aTireUiState().copy(isLoading = false) }
                updateTire(tireSelected) { it.copy(inAlert = result.inAlert) }
            } else {
                // Sin registro en sensor_data (p. ej. ruedas recién montadas en el wizard,
                // antes de sincronizar el diagrama): no hay lecturas. Se limpia el estado para
                // no heredar las de la rueda anterior; isAssembled e isInspectionAvailable los
                // deriva tireUiState.
                _tireUiState.update { TireUiState(currentTire = tireSelected) }
            }
        }
    }

    /**
     * Modifica una sola llanta sobre el estado vigente. Nunca reemplazar `listOfTires` con una
     * lista calculada antes de un punto de suspensión: pisaría cambios concurrentes.
     */
    private fun updateTire(position: String, transform: (MonitorTire) -> MonitorTire) {
        if (position.isBlank()) return
        _monitorUiState.update { state ->
            state.copy(listOfTires = state.listOfTires.map { tire ->
                if (tire.sensorPosition == position) transform(tire) else tire
            })
        }
    }

    fun updateSelectedTire(selectedTire: String) {
        _tireUiState.update { currentUiState ->
            currentUiState.copy(
                currentTire = selectedTire
            )
        }
    }

    fun cleanMonitorData() {
        //bluetoothUseCase.disconnect()
        wizardFinished = false
        _monitorUiState.value = MonitorUiState()
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

    fun switchOdometerUnit() {
        viewModelScope.launch {
            switchOdometerUnitUseCase()
        }
    }


    // La UI decodifica el recurso; el ViewModel no maneja Context ni Bitmap.
    private fun aplicarImagenDiagrama() {
        val baseConfig = _monitorUiState.value.baseConfig ?: return
        val imageConfig = getImageConfig(baseConfig)
        _monitorUiState.update {
            it.copy(imageDimen = imageConfig.dimen, imagenDiagrama = imageConfig.image)
        }
    }
}


private fun List<MonitorTire>.withAssembly(assembledPositions: Set<String>): List<MonitorTire> =
    map { tire ->
        val isAssembled = tire.sensorPosition.normalizarPosicion() in assembledPositions
        if (tire.isAssembled == isAssembled) tire else tire.copy(isAssembled = isAssembled)
    }