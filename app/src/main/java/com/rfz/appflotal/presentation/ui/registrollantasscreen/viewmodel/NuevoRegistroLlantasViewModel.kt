package com.rfz.appflotal.presentation.ui.registrollantasscreen.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.model.tire.response.TireListResponse
import com.rfz.appflotal.domain.tire.TireListUsecase
import com.rfz.appflotal.presentation.navigation.LLANTA_GUARDADA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NuevoRegistroLlantasUiState(
    val tires: List<TireListResponse> = emptyList(),
    val displayedTires: List<TireListResponse> = emptyList(),
    val isLoading: Boolean = true,
    val errorCarga: Boolean = false,
    val searchQuery: String = "",
)

sealed interface EventoListaLlantas {
    data object LlantaGuardada : EventoListaLlantas
}

@HiltViewModel
class NuevoRegistroLlantasViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val tireListUsecase: TireListUsecase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(NuevoRegistroLlantasUiState())
    val uiState: StateFlow<NuevoRegistroLlantasUiState> = _uiState.asStateFlow()

    private val _eventos = Channel<EventoListaLlantas>(Channel.BUFFERED)
    val eventos: Flow<EventoListaLlantas> = _eventos.receiveAsFlow()

    init {
        loadTires()
        // El formulario deja la bandera en esta entrada de navegación al guardar.
        viewModelScope.launch {
            savedStateHandle.getStateFlow(LLANTA_GUARDADA, false).filter { it }.collect {
                savedStateHandle[LLANTA_GUARDADA] = false
                _eventos.send(EventoListaLlantas.LlantaGuardada)
                loadTires()
            }
        }
    }

    fun onReintentar() = loadTires()

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilter()
    }

    fun onClearQuery() = onSearchQueryChanged("")

    private fun loadTires() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorCarga = false) }
            val result = runCatching { tireListUsecase().getOrThrow() }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    tires = result.getOrNull() ?: it.tires,
                    errorCarga = result.isFailure,
                )
            }
            applyFilter()
        }
    }

    private fun applyFilter() {
        val query = _uiState.value.searchQuery.trim()
        _uiState.update { state ->
            state.copy(
                displayedTires = if (query.isEmpty()) state.tires else state.tires.filter { tire ->
                    listOf(tire.brand, tire.model, tire.size, tire.typeAcquisition, tire.idTire.toString())
                        .any { it.contains(query, ignoreCase = true) }
                }
            )
        }
    }
}
