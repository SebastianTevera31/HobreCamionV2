package com.rfz.appflotal.presentation.ui.reportes.ui.co2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.model.report.CO2EmissionsReportResponse
import com.rfz.appflotal.domain.report.GetCO2EmissionsUseCase
import com.rfz.appflotal.presentation.ui.utils.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CO2ReportViewModel @Inject constructor(
    private val getCO2EmissionsUseCase: GetCO2EmissionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CO2UiState())
    val uiState = _uiState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(loadState = LoadState.Loading) }
            getCO2EmissionsUseCase().fold(
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

data class CO2UiState(
    val reports: List<CO2EmissionsReportResponse> = emptyList(),
    val loadState: LoadState<Unit> = LoadState.Idle
)
