package com.rfz.appflotal.presentation.ui.scrap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.model.tire.toTire
import com.rfz.appflotal.data.model.waster.ScrapTirePile
import com.rfz.appflotal.data.repository.waster.WasteRepository
import com.rfz.appflotal.domain.tire.TireListUsecase
import com.rfz.appflotal.domain.waster.WasteReportListUseCase
import com.rfz.appflotal.presentation.ui.utils.OperationStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class TireWasteViewModel @Inject constructor(
    private val tireUseCase: TireListUsecase,
    private val wasteReportListUseCase: WasteReportListUseCase,
    private val wasteRepository: WasteRepository
) :
    ViewModel() {
    private var _uiState = MutableStateFlow(TireWasteUiState())
    val uiState: StateFlow<TireWasteUiState> = _uiState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            val tiresDeferred = async { tireUseCase() }
            val wasteReportListDeferred = async { wasteReportListUseCase() }

            val tires = tiresDeferred.await()
            val wasteReportList = wasteReportListDeferred.await()

            if (tires.isSuccess && wasteReportList.isSuccess) {
                val dismountedTireList =
                    tires.getOrNull()?.filter { it.destination == "Desechar" }?.map { it.toTire() }
                        ?: emptyList()


                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        dismountedTireList = dismountedTireList,
                        wasteReportList = wasteReportList.getOrNull() ?: emptyList(),
                        screenLoadStatus = OperationStatus.Success
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
    }

    fun sendTireToTireWastePile(wasteReportId: Int, tireId: Int) = viewModelScope.launch {
        val nowUtc: OffsetDateTime = OffsetDateTime.now()
        val result = runCatching {
            wasteRepository.sendTireToScrap(
                response = ScrapTirePile(
                    id = 0, // 0 = Crea un nuevo registro
                    tireId = tireId,
                    date = nowUtc,
                    scrapReportId = wasteReportId,
                    treadDepth = _uiState.value.selectedTire?.thread?.toInt() ?: 0
                )
            )
        }

        if (result.isSuccess) {
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

    fun updateSelectedTire(catalogItemId: Int) {
        _uiState.value.dismountedTireList.find { it.id == catalogItemId }?.let { tire ->
            _uiState.update { currentUiState ->
                currentUiState.copy(
                    selectedTire = tire
                )
            }
        }
    }

    fun cleanOperationStatus() {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                operationStatus = null
            )
        }
    }

    fun cleanUiState() {
        _uiState.value = TireWasteUiState()
    }
}