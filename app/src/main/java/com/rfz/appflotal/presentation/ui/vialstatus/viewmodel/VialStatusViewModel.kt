package com.rfz.appflotal.presentation.ui.vialstatus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.local.mapCountries
import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.data.repository.vialstatus.VialStatusRepository
import com.rfz.appflotal.presentation.ui.utils.LoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VialUiStatus(
    val countries: List<CatalogItem> = mapCountries,
    val states: List<CatalogItem> = emptyList(),
    val selectedCountry: CatalogItem? = null,
    val selectedState: CatalogItem? = null,
    val mapUrl: String = "",
    val gettingStatesStatus: LoadState<Unit> = LoadState.Idle
)

@HiltViewModel
class VialStatusViewModel @Inject constructor(
    private val vialStatusRepository: VialStatusRepository
) : ViewModel() {

    private var _uiState = MutableStateFlow(VialUiStatus())
    val uiState = _uiState.asStateFlow()

    init {
        // Determinar ubicacion con geocoder
    }

    fun getMap() {
        val state = _uiState.value.selectedState ?: return

        viewModelScope.launch {
            vialStatusRepository.getMapByState(state.id).onSuccess {

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