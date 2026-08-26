package com.rfz.appflotal.presentation.ui.home.screen.completeplan.viewmodel

import android.R.attr.factor
import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.repository.location.LocationRepository
import com.rfz.appflotal.data.repository.weather.WeatherRepository
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
import kotlin.math.truncate

@HiltViewModel
class CompletePlanViewModel @Inject constructor(
    private val currentPerformanceUseCase: CurrentPerformanceUseCase,
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CompletePlanUiState())
    val uiState: StateFlow<CompletePlanUiState> = _uiState.asStateFlow()

    fun getInitialData() {
        viewModelScope.launch {
            getCurrentPerformance()
            getCurrentWeather()
        }
    }

    fun onNavItemClick(item: BottomNavItems) {
        _uiState.update { currentState ->
            currentState.copy(currentScreen = item)
        }
    }

    suspend fun getCurrentPerformance() {
        //_uiState.update { it.copy(isLoading = true, errorMessage = null) }
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

    @SuppressLint("MissingPermission")
    suspend fun getCurrentWeather() {
        //_uiState.update { it.copy(isLoading = true, errorMessage = null) }

        val location = locationRepository.getLastLocation()
        if (location != null) {
            if (location.ciudad == null) return
            val result = weatherRepository.getLatest(
                lat = truncate(location.lat * factor) / factor, // Invertir
                lon = truncate(location.lng * factor) / factor, // Invertir
                locationName = location.ciudad
            )

            when (result) {
                is ApiResult.Error -> {
//                    _weatherState.update { currentUiState ->
//                        currentUiState.copy(
//                            error = result.message,
//                            screenState = LoadState.Error("Error al obtener el clima.")
//                        )
//                    }
                }

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
