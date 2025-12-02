package com.rfz.appflotal.presentation.ui.inspection.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.core.util.Commons.convertDate
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.data.model.tire.dto.InspectionTireDto
import com.rfz.appflotal.data.repository.database.HombreCamionRepository
import com.rfz.appflotal.data.repository.database.SensorDataTableRepository
import com.rfz.appflotal.domain.catalog.CatalogUseCase
import com.rfz.appflotal.domain.tire.InspectionTireCrudUseCase
import com.rfz.appflotal.presentation.ui.commonscreens.listmanager.viewmodel.ShowToast
import com.rfz.appflotal.presentation.ui.inspection.components.UploadingInspectionMessage
import com.rfz.appflotal.presentation.ui.utils.responseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class InspectionViewModel @Inject constructor(
    private val catalogUseCase: CatalogUseCase,
    private val inspectionTireCrudUseCase: InspectionTireCrudUseCase,
    private val sensorDataTableRepository: SensorDataTableRepository,
    private val hombreCamionRepository: HombreCamionRepository,
) : ViewModel() {
    private var _uiState: MutableStateFlow<InspectionUiState> =
        MutableStateFlow(InspectionUiState.Empty)
    val uiState = _uiState.asStateFlow()

    private var _requestState: MutableStateFlow<InspectionRequestState> =
        MutableStateFlow(InspectionRequestState())
    val requestState = _requestState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<ShowToast>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun loadData() = viewModelScope.launch {
        _uiState.value = InspectionUiState.Loading
        val tireReportDeferred = async { catalogUseCase.onGetTireReport() }
        val lastOdometerDeferred = async { hombreCamionRepository.getOdometer() }

        val tireReport = tireReportDeferred.await()
        val lastOdometer = lastOdometerDeferred.await()

        val isOdometerEditable = if (!lastOdometer.dateLastOdometer.isEmpty()) {
            val dateLastOdometer = Instant.parse(lastOdometer.dateLastOdometer)
            val currentDate = Instant.now()
            currentDate.isAfter(dateLastOdometer)
        } else false

        responseHelper(tireReport) { opciones ->
            _uiState.value =
                InspectionUiState.Success(
                    inspectionList = opciones?.map { it.toCatalog() }
                        ?: emptyList(),
                    lastOdometer = lastOdometer.odometer,
                    isOdometerEditable = isOdometerEditable
                )
        }
    }

    fun clearInspectionUiState() {
        _uiState.value = InspectionUiState.Empty
    }

    fun uploadInspection(positionTire: String, values: InspectionUi) = viewModelScope.launch {
        _requestState.update { currentUiState ->
            currentUiState.copy(
                isSending = true,
                operationState = OperationState.Loading
            )
        }

        val lastOdometerMeasurement = getCurrentDate()

        val result = inspectionTireCrudUseCase(
            requestBody = InspectionTireDto(
                positionTire = positionTire,
                treadDepth = values.treadDepth1,
                treadDepth2 = values.treadDepth2,
                treadDepth3 = values.treadDepth3,
                treadDepth4 = values.treadDepth4,
                tireInspectionReportId = values.reportId?.toIntOrNull() ?: 0,
                pressureInspected = values.pressure,
                dateInspection = lastOdometerMeasurement,
                odometer = values.odometer,
                temperatureInspected = values.temperature,
                pressureAdjusted = values.adjustedPressure
            )
        )

        if (result.isSuccess) {
            // Registrar registro de odometro
            hombreCamionRepository.updateOdometer(values.odometer, lastOdometerMeasurement)
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
                temperature = values.temperature,
                pressure = values.adjustedPressure
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