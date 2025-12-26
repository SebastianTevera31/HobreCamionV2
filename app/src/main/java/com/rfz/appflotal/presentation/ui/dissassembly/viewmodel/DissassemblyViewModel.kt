package com.rfz.appflotal.presentation.ui.dissassembly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.data.model.disassembly.tire.DisassemblyTire
import com.rfz.appflotal.data.model.tire.dto.InspectionTireDto
import com.rfz.appflotal.data.model.tire.toTire
import com.rfz.appflotal.data.repository.UnidadOdometro
import com.rfz.appflotal.data.repository.UnidadPresion
import com.rfz.appflotal.data.repository.UnidadTemperatura
import com.rfz.appflotal.data.repository.assembly.AssemblyTireRepository
import com.rfz.appflotal.data.repository.database.HombreCamionRepository
import com.rfz.appflotal.data.repository.database.SensorDataTableRepository
import com.rfz.appflotal.data.repository.disassembly.SetDisassemblyTireUseCase
import com.rfz.appflotal.domain.catalog.CatalogUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.destination.DestinationUseCase
import com.rfz.appflotal.domain.disassembly.DisassemblyCauseUseCase
import com.rfz.appflotal.domain.tire.InspectionTireCrudUseCase
import com.rfz.appflotal.domain.tire.TireListUsecase
import com.rfz.appflotal.domain.userpreferences.ObserveOdometerUnitUseCase
import com.rfz.appflotal.domain.userpreferences.ObservePressureUnitUseCase
import com.rfz.appflotal.domain.userpreferences.ObserveTemperatureUnitUseCase
import com.rfz.appflotal.presentation.ui.dissassembly.screen.NavigationScreen
import com.rfz.appflotal.presentation.ui.inspection.viewmodel.InspectionUi
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.convertPressureValue
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.convertTemperatureValue
import com.rfz.appflotal.presentation.ui.utils.OperationStatus
import com.rfz.appflotal.presentation.ui.utils.kmToMiles
import com.rfz.appflotal.presentation.ui.utils.milesToKm
import com.rfz.appflotal.presentation.ui.utils.responseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class DisassemblyViewModel @Inject constructor(
    private val tireUseCase: TireListUsecase,
    private val getDestinationUseCase: DestinationUseCase,
    private val disassemblyCauseUseCase: DisassemblyCauseUseCase,
    private val setDisassemblyTireUseCase: SetDisassemblyTireUseCase,
    private val assemblyTireRepository: AssemblyTireRepository,
    private val inspectionTireCrudUseCase: InspectionTireCrudUseCase,
    private val catalogUseCase: CatalogUseCase,
    private val hombreCamionRepository: HombreCamionRepository,
    private val sensorDataTableRepository: SensorDataTableRepository,
    private val getTasksUseCase: GetTasksUseCase,
    private val observeTemperatureUnitUseCase: ObserveTemperatureUnitUseCase,
    private val observePressureUnitUseCase: ObservePressureUnitUseCase,
    private val observeOdometerUnitUseCase: ObserveOdometerUnitUseCase,
) : ViewModel() {
    private var _uiState = MutableStateFlow(DisassemblyUiState())
    val uiState: StateFlow<DisassemblyUiState> = _uiState.asStateFlow()

    private val odometerUnit = observeOdometerUnitUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UnidadOdometro.KILOMETROS
    )

    fun loadData(tirePosition: String, initialPressure: Int, initialTemperature: Int) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                positionTire = tirePosition,
                initialPressure = initialPressure,
                initialTemperature = initialTemperature
            )
        }

        viewModelScope.launch {
            val tiresDeferred = async { tireUseCase() }
            val getDestinationDeferred = async { getDestinationUseCase() }
            val getMountedTireDeferred =
                async { assemblyTireRepository.getAssemblyTire(tirePosition) }
            val getDisassemblyCauseDeferred = async { disassemblyCauseUseCase() }
            val tireReportDeferred = async { catalogUseCase.onGetTireReport() }
            val lastOdometerDeferred = async { hombreCamionRepository.getOdometer() }

            val destinations = getDestinationDeferred.await()
            val tire = getMountedTireDeferred.await()
            val availableTireList = tiresDeferred.await()
            val disassemblyCauses = getDisassemblyCauseDeferred.await()
            val tireReportList = tireReportDeferred.await()
            val lastOdometer = lastOdometerDeferred.await()

            val temperatureUnit = observeTemperatureUnitUseCase().first()
            val pressureUnit = observePressureUnitUseCase().first()
            val odometerUnit = observeOdometerUnitUseCase().first()

            val odometerValue = if (odometerUnit == UnidadOdometro.KILOMETROS) lastOdometer.odometer
            else kmToMiles(lastOdometer.odometer.toDouble())

            responseHelper(tireReportList, onError = {
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        screenLoadStatus = OperationStatus.Error
                    )
                }
            }) { options ->
                if (destinations.isSuccess
                    && tire != null
                    && disassemblyCauses.isSuccess
                    && availableTireList.isSuccess
                ) {
                    val destinationsList =
                        destinations.getOrNull()?.filter { it.id == 1 || it.id == 3 || it.id == 6 }
                            ?: emptyList()

                    val disassemblyList = disassemblyCauses.getOrNull() ?: emptyList()

                    val tire = availableTireList.getOrNull()
                        ?.find { it.idTire == tire.idTire && it.destination == "Montada" }
                        ?.toTire()

                    _uiState.update { currentUiState ->
                        currentUiState.copy(
                            destinationList = destinationsList,
                            tire = tire,
                            disassemblyCauseList = disassemblyList,
                            screenLoadStatus = OperationStatus.Success,
                            lastOdometer = odometerValue.toInt(),
                            tireReportList = options?.map { it.toCatalog() } ?: emptyList(),
                            temperatureUnit = temperatureUnit,
                            pressureUnit = pressureUnit,
                            odometerUnit = odometerUnit
                        )
                    }
                } else {
                    _uiState.update { currentUiState ->
                        currentUiState.copy(
                            screenLoadStatus = OperationStatus.Error
                        )
                    }
                }
            }

            observeOdometerChange()
        }
    }

    fun observeOdometerChange() = viewModelScope.launch {
        odometerUnit.collect {
            _uiState.update { currentUiState ->
                currentUiState.copy(
                    odometerUnit = it
                )
            }
        }
    }

    fun dismountTire(causeId: Int, destinationId: Int) = viewModelScope.launch {
        val lastOdometerMeasurement = getCurrentDate()

        // Publicamos camibios
        val inspectionResult = uploadInspection(lastOdometerMeasurement)

        val odometer = uiState.value.inspectionForm.odometer
        val odometerValue =
            if (_uiState.value.odometerUnit == UnidadOdometro.KILOMETROS) odometer.toDouble()
            else milesToKm(odometer.toDouble())

        if (inspectionResult) {
            val odometer = getTasksUseCase().first()[0].odometer
            val result = setDisassemblyTireUseCase(
                DisassemblyTire(
                    disassemblyCause = causeId,
                    destination = destinationId,
                    dateOperation = getCurrentDate(),
                    positionTire = uiState.value.positionTire,
                    odometer = odometerValue.roundToInt()
                )
            )

            // Registrar registro de odometro
            async {
                hombreCamionRepository.updateOdometer(
                    odometerValue.roundToInt(),
                    lastOdometerMeasurement
                )
            }

            _uiState.update { currentUiState ->
                currentUiState.copy(
                    operationStatus = result.fold(
                        onSuccess = {
                            OperationStatus.Success
                        },
                        onFailure = { OperationStatus.Error }
                    )
                )
            }
        } else {
            _uiState.update { currentUiState ->
                currentUiState.copy(
                    operationStatus = OperationStatus.Error
                )
            }
        }
    }


    fun updateInspection(values: InspectionUi) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                inspectionForm = values
            )
        }
    }

    fun updateNavigation(navigationScreen: NavigationScreen) {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                navigationScreen = navigationScreen
            )
        }
    }

    private suspend fun uploadInspection(lastOdometerMeasurement: String): Boolean {
        val uiState = _uiState.value

        val pressure = if (_uiState.value.pressureUnit == UnidadPresion.BAR) {
            convertPressureValue(uiState.inspectionForm.pressure, UnidadPresion.PSI)
        } else {
            uiState.inspectionForm.pressure
        }

        val temperature = if (_uiState.value.temperatureUnit == UnidadTemperatura.FAHRENHEIT) {
            convertTemperatureValue(uiState.inspectionForm.temperature, UnidadTemperatura.CELCIUS)
        } else {
            uiState.inspectionForm.temperature
        }

        val pressureAdjusted = if (_uiState.value.temperatureUnit == UnidadTemperatura.FAHRENHEIT) {
            convertPressureValue(uiState.inspectionForm.pressure, UnidadPresion.PSI)
        } else {
            uiState.inspectionForm.pressure
        }

        val odometerValue =
            if (_uiState.value.odometerUnit == UnidadOdometro.KILOMETROS) uiState.lastOdometer.toDouble()
            else milesToKm(uiState.inspectionForm.odometer.toDouble())

        val result = inspectionTireCrudUseCase(
            requestBody = InspectionTireDto(
                positionTire = uiState.positionTire,
                treadDepth = uiState.inspectionForm.treadDepth1,
                treadDepth2 = uiState.inspectionForm.treadDepth2,
                treadDepth3 = uiState.inspectionForm.treadDepth3,
                treadDepth4 = uiState.inspectionForm.treadDepth4,
                tireInspectionReportId = 3, // 3 = Enviar a "desechar"
                pressureInspected = pressure.toInt(),
                dateInspection = lastOdometerMeasurement,
                odometer = odometerValue.roundToInt(),
                temperatureInspected = temperature.toInt(),
                pressureAdjusted = pressureAdjusted.toInt()
            )
        )

        return if (result.isSuccess) {
            hombreCamionRepository.updateOdometer(odometerValue.toInt(), lastOdometerMeasurement)
            sensorDataTableRepository.updateTireRecord(
                tire = uiState.positionTire,
                temperature = temperature.toInt(),
                pressure = pressure.toInt()
            )
            result.isSuccess
        } else false
    }

    fun restartOperationStatus() {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                operationStatus = null
            )
        }
    }

    fun cleanUiState() {
        _uiState.value = DisassemblyUiState()
    }
}