package com.rfz.appflotal.presentation.ui.reportes.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.OilBarrel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.R
import com.rfz.appflotal.domain.performance.CurrentPerformanceUseCase
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.VehicleStat
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.asIcon
import com.rfz.appflotal.presentation.ui.utils.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MenuReportsUi(
    val vehicleName: String = "Mercedes Actros",
    val vehiclePlate: String = "4521-KBX",
    val cardState: LoadState<Unit> = LoadState.Idle,
    val stats: List<VehicleStat> = listOf(
        VehicleStat(
            1,
            Icons.Outlined.OilBarrel.asIcon(),
            "0",
            "lts",
            R.string.consumo_de_combustible
        ),
        VehicleStat(
            2,
            Icons.Outlined.Cloud.asIcon(), "100", "kg",
            R.string.emision_co2
        )
    )
)

@HiltViewModel
class MenuReportsViewModel @Inject constructor(
    private val currentPerformanceUseCase: CurrentPerformanceUseCase
) : ViewModel() {
    private var _uiState = MutableStateFlow(MenuReportsUi())
    val uiState = _uiState.asStateFlow()

    fun getInitialData() {
        viewModelScope.launch {
            getCurrentPerformance()
        }
    }

    suspend fun getCurrentPerformance() {
        try {
            _uiState.update { currentUiState -> currentUiState.copy(cardState = LoadState.Loading) }
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
                currentState.copy(stats = newList, cardState = LoadState.Success(Unit))
            }
        } catch (e: Exception) {
            _uiState.update { currentUiState ->
                currentUiState.copy(cardState = LoadState.Error(e.message ?: "Error"))
            }
        }
    }
}