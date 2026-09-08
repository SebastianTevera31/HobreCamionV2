package com.rfz.appflotal.presentation.ui.home.screen.completeplan.viewmodel

import android.R.attr.factor
import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Thermostat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.model.alerts.Alert
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.repository.location.LocationRepository
import com.rfz.appflotal.data.repository.weather.WeatherRepository
import com.rfz.appflotal.domain.alerts.GetAlertsUseCase
import com.rfz.appflotal.domain.performance.CurrentPerformanceUseCase
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.AlertStatus
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.AlertUi
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.CompletePlanUiState
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.asIcon
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.BottomNavItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.truncate
import com.rfz.appflotal.data.model.alerts.AlertType as DomainAlertType

@HiltViewModel
class CompletePlanViewModel @Inject constructor(
    private val currentPerformanceUseCase: CurrentPerformanceUseCase,
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository,
    private val alertsUseCase: GetAlertsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CompletePlanUiState())
    val uiState: StateFlow<CompletePlanUiState> = _uiState.asStateFlow()

    fun getInitialData() {
        viewModelScope.launch {
            getCurrentPerformance()
            getCurrentWeather()
            getAlerts()
        }
    }

    fun onNavItemClick(item: BottomNavItems) {
        _uiState.update { currentState ->
            currentState.copy(currentScreen = item)
        }
    }

    suspend fun getCurrentPerformance() {
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
                currentState.copy(stats = newList)
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    errorMessage = e.message ?: "Error al obtener rendimiento"
                )
            }
        }
    }

    private suspend fun getAlerts() {
        alertsUseCase(
            startDate = "",
            endDate = "",
            position = "",
            alertType = "",
            startPaging = 0
        ).onSuccess { alerts ->
            _uiState.update { currentState ->
                currentState.copy(alerts = alerts.take(5).map(Alert::toAlertUi))
            }
        }.onFailure {
            // Handle error
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentWeather() {
        val location = locationRepository.getLastLocation()
        if (location != null) {
            if (location.ciudad == null) return
            val result = weatherRepository.getLatest(
                lat = truncate(location.lat * factor) / factor,
                lon = truncate(location.lng * factor) / factor,
                locationName = location.ciudad
            )

            when (result) {
                is ApiResult.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            weatherTemp = result.data.temp.toString(),
                            weatherCity = location.ciudad,
                            weatherDesc = result.data.condLabel
                        )
                    }
                }

                else -> Unit
            }
        }
    }
}

private fun Alert.toAlertUi(): AlertUi {
    val isPressureAlert = alert == DomainAlertType.PRESSURE || alert == DomainAlertType.INFLATE
    return AlertUi(
        icon = (if (isPressureAlert) Icons.Outlined.Speed else Icons.Outlined.Thermostat).asIcon(),
        title = when (alert) {
            DomainAlertType.PRESSURE -> "Alerta de presión · $position"
            DomainAlertType.TEMPERATURE -> "Alerta de temperatura · $position"
            DomainAlertType.INFLATE -> "Alerta de inflado · $position"
            DomainAlertType.NONE -> "Alerta · $position"
        },
        detailLabel = if (isPressureAlert) "Presión:" else "Temp:",
        detailValue = if (isPressureAlert) "%.2f psi".format(psi) else "$temperature °C",
        status = AlertStatus.CRITICA
    )
}
