package com.rfz.appflotal.presentation.ui.reportes.ui.fuel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.model.report.FuelConsumptionReportResponse
import com.rfz.appflotal.domain.report.GetFuelConsumptionUseCase
import com.rfz.appflotal.presentation.ui.utils.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FuelReportViewModel @Inject constructor(
    private val getFuelConsumptionUseCase: GetFuelConsumptionUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(FuelUiState())
    val uiState = _uiState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(loadState = LoadState.Loading) }
            getFuelConsumptionUseCase().fold(
                onSuccess = { reports ->
                    _uiState.update { 
                        it.copy(
                            reports = reports,
                            loadState = LoadState.Success(Unit)
                        )
                    }
                },
                onFailure = {
                    _uiState.update { it.copy(loadState = LoadState.Error(it.toString())) }
                }
            )
        }
    }
}

data class FuelUiState(
    val reports: List<FuelConsumptionReportResponse> = emptyList(),
    val loadState: LoadState<Unit> = LoadState.Idle
)
