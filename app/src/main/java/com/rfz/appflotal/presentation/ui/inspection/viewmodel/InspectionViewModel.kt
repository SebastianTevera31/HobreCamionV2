package com.rfz.appflotal.presentation.ui.inspection.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.data.model.tire.dto.InspectionTireDto
import com.rfz.appflotal.data.repository.database.SensorDataTableRepository
import com.rfz.appflotal.domain.catalog.CatalogUseCase
import com.rfz.appflotal.domain.tire.InspectionTireCrudUseCase
import com.rfz.appflotal.presentation.ui.commonscreens.listmanager.viewmodel.ShowToast
import com.rfz.appflotal.presentation.ui.utils.responseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class UploadingInspectionMessage(@param:StringRes val message: Int) {
    SUCCESS(R.string.inspeccion_exitosa_mensaje),
    GENERAL_ERROR(R.string.error_registrar_inspeccion),
}

@HiltViewModel
class InspectionViewModel @Inject constructor(
    private val catalogUseCase: CatalogUseCase,
    private val inspectionTireCrudUseCase: InspectionTireCrudUseCase,
    private val sensorDataTableRepository: SensorDataTableRepository
) : ViewModel() {
    private var _uiState: MutableStateFlow<InspectionUiState> =
        MutableStateFlow(InspectionUiState.Empty)
    val uiState = _uiState.asStateFlow()

    private var _requestState: MutableStateFlow<InspectionRequestState> =
        MutableStateFlow(InspectionRequestState())
    val requestState = _requestState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<ShowToast>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun load() = viewModelScope.launch {
        _uiState.value = InspectionUiState.Loading
        val tireResponse = catalogUseCase.onGetTireReport()
        responseHelper(tireResponse) { opciones ->
            _uiState.value =
                InspectionUiState.Success(
                    inspectionList = opciones?.map { it.toCatalog() }
                        ?: emptyList()
                )
        }
    }

    fun clearInspectionUiState() {
        _uiState.value = InspectionUiState.Empty
    }

    fun uploadInspection(positionTire: String, values: InspectionUi) = viewModelScope.launch {
        _requestState.update { currentUiState ->
            currentUiState.copy(
                isSending = true
            )
        }

        val result = inspectionTireCrudUseCase(
            requestBody = InspectionTireDto(
                positionTire = positionTire,
                treadDepth = values.treadDepth1,
                treadDepth2 = values.treadDepth2,
                treadDepth3 = values.treadDepth3,
                treadDepth4 = values.treadDepth4,
                tireInspectionReportId = values.reportId?.toIntOrNull() ?: 0,
                pressureInspected = values.pressure,
                dateInspection = getCurrentDate(),
                odometer = values.odometer,
                temperatureInspected = values.temperature,
                pressureAdjusted = values.adjustedPressure
            )
        )

        _requestState.update { currentUiState ->
            currentUiState.copy(
                isSending = false
            )
        }

        if (result.isSuccess) {
            sensorDataTableRepository.updateTireRecord(
                tire = positionTire,
                temperature = values.temperature,
                pressure = values.adjustedPressure
            )
            _eventFlow.emit(ShowToast(UploadingInspectionMessage.SUCCESS.message))
        } else {
            _eventFlow.emit(ShowToast(UploadingInspectionMessage.GENERAL_ERROR.message))
        }
    }
}