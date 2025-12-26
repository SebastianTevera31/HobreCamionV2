package com.rfz.appflotal.presentation.ui.inspection.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.core.util.Commons.convertDate
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.data.model.tire.dto.InspectionTireDto
import com.rfz.appflotal.data.repository.UnidadOdometro
import com.rfz.appflotal.data.repository.UnidadPresion
import com.rfz.appflotal.data.repository.UnidadTemperatura
import com.rfz.appflotal.data.repository.database.HombreCamionRepository
import com.rfz.appflotal.data.repository.database.SensorDataTableRepository
import com.rfz.appflotal.domain.catalog.CatalogUseCase
import com.rfz.appflotal.domain.tire.InspectionTireCrudUseCase
import com.rfz.appflotal.domain.userpreferences.ObserveOdometerUnitUseCase
import com.rfz.appflotal.domain.userpreferences.ObservePressureUnitUseCase
import com.rfz.appflotal.domain.userpreferences.ObserveTemperatureUnitUseCase
import com.rfz.appflotal.presentation.ui.commonscreens.listmanager.viewmodel.ShowToast
import com.rfz.appflotal.presentation.ui.inspection.components.UploadingInspectionMessage
import com.rfz.appflotal.presentation.ui.utils.kmToMiles
import com.rfz.appflotal.presentation.ui.utils.milesToKm
import com.rfz.appflotal.presentation.ui.utils.responseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class InspectionViewModel @Inject constructor(
    private val catalogUseCase: CatalogUseCase,
    private val inspectionTireCrudUseCase: InspectionTireCrudUseCase,
    private val sensorDataTableRepository: SensorDataTableRepository,
    private val hombreCamionRepository: HombreCamionRepository,
    private val observeTemperatureUnitUseCase: ObserveTemperatureUnitUseCase,
    private val observePressureUnitUseCase: ObservePressureUnitUseCase,
    private val observeOdometerUnitUseCase: ObserveOdometerUnitUseCase,
) : ViewModel() {
    private var _uiState: MutableStateFlow<InspectionUiState> =
        MutableStateFlow(InspectionUiState.Empty)
    val uiState = _uiState.asStateFlow()

    private var _requestState: MutableStateFlow<InspectionRequestState> =
        MutableStateFlow(InspectionRequestState())
    val requestState = _requestState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<ShowToast>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val odometerUnit = observeOdometerUnitUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UnidadOdometro.KILOMETROS
    )

    fun loadData() = viewModelScope.launch {
        _uiState.value = InspectionUiState.Loading
        val tireReportTypeCatalogDeferred = async { catalogUseCase.onGetTireReport() }
        val lastOdometerDeferred = async { hombreCamionRepository.getOdometer() }

        val tireReportType = tireReportTypeCatalogDeferred.await()
        val lastOdometer = lastOdometerDeferred.await()
        val odometerInspectionDate = lastOdometer.dateLastOdometer

        val temperatureUnit = observeTemperatureUnitUseCase().first()
        val pressureUnit = observePressureUnitUseCase().first()
        val odometerUnit = observeOdometerUnitUseCase().first()

        val isOdometerEditable = if (odometerInspectionDate.isNotEmpty()) {
            try {
                val lastOdometerInspection = Instant.parse(odometerInspectionDate)
                val oneDayAgo = Instant.now().minus(1, ChronoUnit.DAYS)
                lastOdometerInspection.isBefore(oneDayAgo)
            } catch (_: DateTimeParseException) {
                try {
                    val dateLastOdometer = LocalDateTime.parse(odometerInspectionDate)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                    val currentDate = Instant.now()
                    currentDate.isAfter(dateLastOdometer)
                } catch (_: DateTimeParseException) {
                    false
                }
            }
        } else false

        responseHelper(tireReportType) { opciones ->

            val mainOrder = listOf(1, 27, 6, 38, 9)
            val mainIds = mainOrder.toSet()

            val (mainPriority, secondaryPriority) = opciones
                .orEmpty()
                .partition { it.idTireInspectionReport in mainIds }

            val orderedMain = mainPriority.sortedBy { option ->
                mainOrder.indexOf(option.idTireInspectionReport)
            }

            val list = orderedMain + secondaryPriority

            val odometerValue = if (odometerUnit == UnidadOdometro.KILOMETROS) lastOdometer.odometer
            else kmToMiles(lastOdometer.odometer.toDouble())

            _uiState.value =
                InspectionUiState.Success(
                    inspectionList = list.map { it.toCatalog() },
                    lastOdometer = odometerValue.toInt(),
                    isOdometerEditable = isOdometerEditable,
                    pressureUnit = pressureUnit,
                    temperatureUnit = temperatureUnit,
                    odometerUnit = odometerUnit,
                )

            observeOdometerChange()
        }
    }

    fun observeOdometerChange() = viewModelScope.launch {
        odometerUnit.collect {
            if (_uiState.value is InspectionUiState.Success) {
                _uiState.update { currentUiState ->
                    (currentUiState as InspectionUiState.Success).copy(
                        odometerUnit = it
                    )
                }
            }
        }
    }

    fun clearInspectionUiState() {
        _uiState.value = InspectionUiState.Empty
        _requestState.value = InspectionRequestState()
    }

    fun clearInspectionRequestState() {
        _requestState.update { currentUiState ->
            currentUiState.copy(
                operationState = OperationState.Loading
            )
        }
    }


    fun uploadInspection(positionTire: String, values: InspectionUi) = viewModelScope.launch {
        _requestState.update { currentUiState ->
            currentUiState.copy(
                isSending = true,
                operationState = OperationState.Loading
            )
        }

        val lastOdometerMeasurement = getCurrentDate()

        val state = _uiState.value as InspectionUiState.Success

        val temperatureValue = if (state.temperatureUnit == UnidadTemperatura.FAHRENHEIT) {
            ((values.temperature - 32) / 1.8).toInt()
        } else values.temperature

        val pressureValue = if (state.pressureUnit == UnidadPresion.BAR) {
            (values.pressure * 14.5038).toInt()
        } else values.pressure

        val odometerValue = if (odometerUnit.value == UnidadOdometro.KILOMETROS) values.odometer.toDouble()
        else milesToKm(values.odometer.toDouble())

        val adjustedPressureValue = if (state.pressureUnit == UnidadPresion.BAR) {
            (values.adjustedPressure * 14.5038).toInt()
        } else values.adjustedPressure

        val result = inspectionTireCrudUseCase(
            requestBody = InspectionTireDto(
                positionTire = positionTire,
                treadDepth = values.treadDepth1,
                treadDepth2 = values.treadDepth2,
                treadDepth3 = values.treadDepth3,
                treadDepth4 = values.treadDepth4,
                tireInspectionReportId = values.reportId?.toIntOrNull() ?: 0,
                pressureInspected = pressureValue.toInt(),
                dateInspection = lastOdometerMeasurement,
                odometer = odometerValue.roundToInt(),
                temperatureInspected = temperatureValue.toInt(),
                pressureAdjusted = adjustedPressureValue.toInt()
            )
        )

        if (result.isSuccess) {
            // Registrar registro de odometro
            hombreCamionRepository.updateOdometer(odometerValue.roundToInt(), lastOdometerMeasurement)
            sensorDataTableRepository.updateLastInspection(
                tire = positionTire,
                lastInspection = convertDate(
                    date = lastOdometerMeasurement,
                    initialFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                    convertFormat = "yyyy-MM-dd'T'HH:mm:ss",
                )
            )
            sensorDataTableRepository.updateTireRecord(
                tire = positionTire,
                temperature = temperatureValue.toInt(),
                pressure = adjustedPressureValue.toInt()
            )
            _requestState.update { currentUiState ->
                currentUiState.copy(
                    isSending = false,
                    operationState = OperationState.Success
                )
            }
            _eventFlow.emit(ShowToast(UploadingInspectionMessage.SUCCESS.message))
        } else {
            _requestState.update { currentUiState ->
                currentUiState.copy(
                    isSending = false,
                    operationState = OperationState.Error
                )
            }
            _eventFlow.emit(ShowToast(UploadingInspectionMessage.GENERAL_ERROR.message))
        }
    }
}
