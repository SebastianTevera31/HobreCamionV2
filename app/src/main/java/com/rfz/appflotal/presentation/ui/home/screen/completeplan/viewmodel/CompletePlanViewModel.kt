package com.rfz.appflotal.presentation.ui.home.screen.completeplan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.domain.performance.CurrentPerformanceUseCase
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.CompletePlanUiState
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.BottomNavItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CompletePlanViewModel @Inject constructor(
    private val currentPerformanceUseCase: CurrentPerformanceUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CompletePlanUiState())
    val uiState: StateFlow<CompletePlanUiState> = _uiState.asStateFlow()

    fun getInitialData() {
        getCurrentPerformance()
    }

    fun onNavItemClick(item: BottomNavItems) {
        _uiState.update { currentState ->
            currentState.copy(currentScreen = item)
        }
    }

    fun getCurrentPerformance() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val result = currentPerformanceUseCase()
                val newList = _uiState.value.stats.map { stat ->
                    val newValue = when (stat.id) {
                        1 -> result.fuelConsumption
                        2 -> result.co2Emissions
                        else -> stat.value
                    }
                    stat.copy(value = newValue)
                }

                _uiState.update { currentState ->
                    currentState.copy(stats = newList, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error al obtener rendimiento") }
            }
        }
    }
}
