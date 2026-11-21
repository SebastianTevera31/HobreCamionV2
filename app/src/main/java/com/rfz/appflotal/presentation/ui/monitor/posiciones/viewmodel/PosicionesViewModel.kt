package com.rfz.appflotal.presentation.ui.monitor.posiciones.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.domain.monitor.LecturaPosicion
import com.rfz.appflotal.domain.monitor.ObtenerHistorialPosicionUseCase
import com.rfz.appflotal.domain.monitor.ObtenerUltimasLecturasUseCase
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.ListOfTireData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Se conserva ApiResult porque es lo que esperan las vistas de Posiciones que ya existen. */
data class PosicionesUiState(
    val ultimas: ApiResult<List<ListOfTireData>?> = ApiResult.Loading,
    val historial: ApiResult<List<ListOfTireData>?> = ApiResult.Success(emptyList()),
)

/** Pestaña Posiciones del Monitor: últimas lecturas y lecturas por fecha de una posición. */
@HiltViewModel
class PosicionesViewModel @Inject constructor(
    private val obtenerUltimasLecturas: ObtenerUltimasLecturasUseCase,
    private val obtenerHistorial: ObtenerHistorialPosicionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PosicionesUiState())
    val uiState: StateFlow<PosicionesUiState> = _uiState.asStateFlow()

    fun cargarUltimas() {
        _uiState.update { it.copy(ultimas = ApiResult.Loading) }
        viewModelScope.launch {
            val lecturas = runCatching { obtenerUltimasLecturas() }.getOrNull()
            _uiState.update {
                it.copy(ultimas = lecturas?.let { lista -> ApiResult.Success(lista.map { l -> l.aTireData() }) }
                    ?: ApiResult.Error())
            }
        }
    }

    fun cargarHistorial(posicion: String, fecha: String) {
        _uiState.update { it.copy(historial = ApiResult.Loading) }
        viewModelScope.launch {
            val lecturas = runCatching { obtenerHistorial(posicion, fecha) }.getOrNull()
            _uiState.update {
                it.copy(historial = lecturas?.let { lista -> ApiResult.Success(lista.map { l -> l.aTireData() }) }
                    ?: ApiResult.Error())
            }
        }
    }

    fun limpiarHistorial() = _uiState.update { it.copy(historial = ApiResult.Success(emptyList())) }
}

private fun LecturaPosicion.aTireData() = ListOfTireData(
    tirePosition = posicion,
    tireNumber = numeroLlanta,
    sensorDate = fecha,
    psi = presion,
    temperature = temperatura,
)
