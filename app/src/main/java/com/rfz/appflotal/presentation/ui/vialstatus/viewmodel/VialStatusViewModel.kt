package com.rfz.appflotal.presentation.ui.vialstatus.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.local.Catalog
import com.rfz.appflotal.data.local.mapCountries
import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.data.model.location.toDomain
import com.rfz.appflotal.data.repository.location.LocationRepository
import com.rfz.appflotal.data.repository.vialstatus.VialStatusRepository
import com.rfz.appflotal.presentation.ui.utils.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VialUiStatus(
    val countries: List<Catalog> = mapCountries,
    val states: List<Catalog> = emptyList(),
    val selectedCountry: CatalogItem? = null,
    val selectedState: CatalogItem? = null,
    val mapUrl: String = "",
    val gettingStatesStatus: LoadState<Unit> = LoadState.Idle,
    val gettingMapStatus: LoadState<String> = LoadState.Idle
)

enum class VialError {
    EMPTY_MAP, SERVER_ERROR
}

@HiltViewModel
class VialStatusViewModel @Inject constructor(
    private val vialStatusRepository: VialStatusRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private var _uiState = MutableStateFlow(VialUiStatus())
    val uiState = _uiState.asStateFlow()

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() = viewModelScope.launch {
        val result = locationRepository.getLastLocation()
        if (result != null) {
            val currentCountry = _uiState.value.countries.find { result.pais == it.enDescription }

            if (currentCountry != null) {
                // Seleccionamos el país y cargamos sus estados
                _uiState.update {
                    it.copy(
                        selectedCountry = currentCountry,
                        gettingStatesStatus = LoadState.Loading
                    )
                }

                vialStatusRepository.getStates(currentCountry.id).onSuccess { statesList ->
                    val mappedStates = statesList.map {
                        Catalog(
                            id = it.idState,
                            description = it.stateName,
                            enDescription = it.stateName
                        )
                    }
                    val currentState = mappedStates.find { result.estado == it.enDescription }

                    _uiState.update {
                        it.copy(
                            states = mappedStates,
                            selectedState = currentState,
                            gettingStatesStatus = LoadState.Success(Unit)
                        )
                    }
                }.onFailure {
                    _uiState.update { it.copy(gettingStatesStatus = LoadState.Error("")) }
                }
            }
        }
    }

    fun changeState(stateId: Int) {
        val selectedState = _uiState.value.states.find { stateId == it.id }
        _uiState.update { currentUiState ->
            currentUiState.copy(
                selectedState = selectedState
            )
        }
    }

    fun changeCountry(countryId: Int) {
        val selectedCountry = _uiState.value.countries.find { countryId == it.id } ?: return

        _uiState.update { currentUiState ->
            currentUiState.copy(
                selectedCountry = selectedCountry,
                selectedState = null,
                states = emptyList(),
                gettingStatesStatus = LoadState.Loading
            )
        }

        viewModelScope.launch {
            vialStatusRepository.getStates(selectedCountry.id).onSuccess { statesList ->
                val mappedStates = statesList.map { it.toDomain() }
                updateLoadState(LoadState.Success(Unit)) {
                    copy(states = mappedStates, gettingStatesStatus = it)
                }
            }.onFailure {
                updateLoadState(LoadState.Error("")) { copy(gettingStatesStatus = it) }
            }
        }
    }

    fun getMap() {
        val state = _uiState.value.selectedState ?: return
        updateLoadState(LoadState.Loading) { copy(gettingMapStatus = it) }

        viewModelScope.launch {
            vialStatusRepository.getMapByState(state.id).onSuccess { result ->
                if (result.isEmpty()) {
                    updateLoadState(LoadState.Error(VialError.EMPTY_MAP.name)) {
                        copy(gettingMapStatus = it)
                    }
                    return@onSuccess
                }

                val link = result.first().link
                updateLoadState(LoadState.Success(link)) {
                    copy(mapUrl = link, gettingMapStatus = it)
                }
            }.onFailure {
                updateLoadState(LoadState.Error(VialError.SERVER_ERROR.name)) {
                    copy(gettingMapStatus = it)
                }
            }
        }
    }

    fun <T> updateLoadState(
        status: LoadState<T>,
        updateBlock: VialUiStatus.(LoadState<T>) -> VialUiStatus
    ) {
        _uiState.update { it.updateBlock(status) }
    }
}
