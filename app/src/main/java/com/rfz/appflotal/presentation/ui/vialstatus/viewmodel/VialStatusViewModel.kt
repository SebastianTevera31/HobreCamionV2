package com.rfz.appflotal.presentation.ui.vialstatus.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.core.util.AppLocale
import com.rfz.appflotal.data.local.Catalog
import com.rfz.appflotal.data.local.mapCountries
import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.data.model.location.toDomain
import com.rfz.appflotal.data.repository.location.LocationRepository
import com.rfz.appflotal.data.repository.vialstatus.VialStatusRepository
import com.rfz.appflotal.presentation.ui.utils.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class VialUiStatus(
    val countries: List<Catalog> = emptyList(),
    val states: List<Catalog> = emptyList(),
    val selectedCountry: CatalogItem? = null,
    val selectedState: CatalogItem? = null,
    val mapUrl: String = "",
    val initScale: Double = 0.55,
    val currentLanguage: Locale = Locale.getDefault(),
    val gettingStatesStatus: LoadState<Unit> = LoadState.Idle,
    val gettingMapStatus: LoadState<String> = LoadState.Idle
)

enum class VialError {
    EMPTY_MAP, SERVER_ERROR
}

@HiltViewModel
class VialStatusViewModel @Inject constructor(
    private val vialStatusRepository: VialStatusRepository,
    private val locationRepository: LocationRepository,
    private val appLocal: AppLocale
) : ViewModel() {

    private var _uiState = MutableStateFlow(VialUiStatus())
    val uiState = _uiState.asStateFlow()

    private var currentJob: Job? = null

    init {
        observeLocale()
    }

    private fun observeLocale() {
        viewModelScope.launch {
            appLocal.currentLocale.collect { locale ->
                val mappedCountries = mapCountries.map {
                    if (locale.language == Locale.ENGLISH.language) {
                        it.copy(description = it.enDescription)
                    } else it
                }
                _uiState.update {
                    it.copy(
                        countries = mappedCountries,
                        currentLanguage = locale
                    )
                }
            }
        }
    }

    fun cancelOperation() {
        currentJob?.cancel()
        _uiState.update {
            it.copy(
                mapUrl = "",
                gettingStatesStatus = if (it.gettingStatesStatus is LoadState.Loading) LoadState.Cancelled else it.gettingStatesStatus,
                gettingMapStatus = if (it.gettingMapStatus is LoadState.Loading) LoadState.Cancelled else it.gettingMapStatus
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    mapUrl = "", 
                    gettingStatesStatus = LoadState.Loading,
                    gettingMapStatus = LoadState.Idle
                ) 
            }

            val result = locationRepository.getLastLocation() ?: run {
                _uiState.update { it.copy(gettingStatesStatus = LoadState.Idle) }
                return@launch
            }

            val currentCountry = _uiState.value.countries.find { result.pais == it.enDescription }

            if (currentCountry != null) {
                _uiState.update { it.copy(selectedCountry = currentCountry) }
                fetchStates(currentCountry.id, result.estado)
            }
        }
    }

    fun changeCountry(countryId: Int) {
        val selectedCountry = _uiState.value.countries.find { countryId == it.id } ?: return

        _uiState.update {
            it.copy(
                selectedCountry = selectedCountry,
                selectedState = null,
                states = emptyList(),
                mapUrl = ""
            )
        }
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            fetchStates(selectedCountry.id)
        }
    }

    private suspend fun fetchStates(countryId: Int, stateToMatch: String? = null) {
        _uiState.update { it.copy(gettingStatesStatus = LoadState.Loading) }

        vialStatusRepository.getStates(countryId).onSuccess { statesList ->
            val mappedStates = statesList.map { it.toDomain() }
            val currentState = stateToMatch?.let { name ->
                mappedStates.find { it.enDescription == name }
            }

            _uiState.update {
                it.copy(
                    states = mappedStates,
                    selectedState = currentState ?: it.selectedState,
                    gettingStatesStatus = LoadState.Success(Unit)
                )
            }
        }.onFailure {
            _uiState.update { it.copy(gettingStatesStatus = LoadState.Error("")) }
        }
    }

    fun changeState(stateId: Int) {
        val selectedState = _uiState.value.states.find { stateId == it.id }
        _uiState.update { it.copy(selectedState = selectedState) }
    }

    fun getMap() {
        val state = _uiState.value.selectedState ?: return

        _uiState.update { it.copy(gettingMapStatus = LoadState.Loading, mapUrl = "") }

        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            vialStatusRepository.getMapByState(state.id).onSuccess { result ->
                if (result.isEmpty()) {
                    _uiState.update { it.copy(gettingMapStatus = LoadState.Error(VialError.EMPTY_MAP.name)) }
                    return@onSuccess
                }

                val link = result.first().link
                _uiState.update { it.copy(mapUrl = link, gettingMapStatus = LoadState.Success(link)) }
            }.onFailure {
                _uiState.update { it.copy(gettingMapStatus = LoadState.Error(VialError.SERVER_ERROR.name)) }
            }
        }
    }

    fun reduceScale() {
        _uiState.update { it.copy(initScale = (it.initScale - 0.05).coerceAtLeast(0.1)) }
    }

    fun increaseScale() {
        _uiState.update { it.copy(initScale = (it.initScale + 0.05).coerceAtMost(5.0)) }
    }
}
