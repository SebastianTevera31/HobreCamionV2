package com.rfz.appflotal.presentation.ui.weather.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.repository.location.LocationRepository
import com.rfz.appflotal.data.repository.weather.WeatherRepository
import com.rfz.appflotal.domain.weather.City
import com.rfz.appflotal.presentation.ui.utils.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.truncate

data class WeatherLocation(
    val lat: Double,
    val lon: Double,
    val name: String
)

data class WeatherUiState(
    val isLoading: Boolean = false,
    val city: City? = null,
    val error: String? = null,
    val screenState: LoadState<Unit> = LoadState.Idle
)

private const val factor = 1000000.0

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository,
) : ViewModel() {

    private val _weatherState = MutableStateFlow(WeatherUiState())
    val weatherState = _weatherState.asStateFlow()

    @SuppressLint("MissingPermission")
    fun getCurrentWeather() {
        viewModelScope.launch {
            _weatherState.update { currentUiState ->
                currentUiState.copy(
                    isLoading = true,
                    screenState = LoadState.Loading
                )
            }

            val location = locationRepository.getLastLocation()
            if (location != null) {
                if (location.ciudad == null) return@launch
                val result = weatherRepository.getLatest(
                    lat = 16.764900, // Invertir
                    lon = -93.183400, // Invertir
                    locationName = "Tuxtla Gutierrez"
                )
                when (result) {
                    is ApiResult.Error -> {
                        _weatherState.update { currentUiState ->
                            currentUiState.copy(
                                error = result.message,
                                screenState = LoadState.Error("Error al obtener el clima.")
                            )
                        }
                    }

                    is ApiResult.Success -> {
                        _weatherState.update { currentUiState ->
                            currentUiState.copy(
                                city = result.data,
                                screenState = LoadState.Success(Unit)
                            )
                        }
                    }

                    else -> Unit
                }
            }
        }
    }

    fun getWeatherApi(lat: Double, lon: Double, nombreUbicacion: String) {
        viewModelScope.launch {
            when (val result = weatherRepository.getWeatherApi(lat, lon, nombreUbicacion)) {
                is ApiResult.Error -> {
                    _weatherState.update { currentUiState ->
                        currentUiState.copy(
                            error = result.message,
                            screenState = LoadState.Error("Error al obtener el clima.")
                        )
                    }
                }

                is ApiResult.Success -> {
                    _weatherState.update { currentUiState ->
                        currentUiState.copy(
                            city = result.data,
                            screenState = LoadState.Success(Unit)
                        )
                    }
                }

                else -> Unit
            }
        }
    }
}
