package com.rfz.appflotal.presentation.ui.monitor.sincronizacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.NetworkStatus
import com.rfz.appflotal.data.model.sincronizacion.ResultadoProcesamiento
import com.rfz.appflotal.domain.sincronizacion.DescartarOperacionUseCase
import com.rfz.appflotal.domain.sincronizacion.ObservarEstadoSincronizacionUseCase
import com.rfz.appflotal.domain.sincronizacion.ReintentarOperacionUseCase
import com.rfz.appflotal.domain.sincronizacion.SincronizarAhoraUseCase
import com.rfz.appflotal.domain.wifi.WifiUseCase
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.CargaSincronizacion
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.EfectoSincronizacion
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.EstadoSincronizacionUiState
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.FilaSincronizacionUi
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.conEstado
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Estatus de la cola de operaciones que se muestra en el Monitor. */
@HiltViewModel
class EstadoSincronizacionViewModel @Inject constructor(
    private val observarEstado: ObservarEstadoSincronizacionUseCase,
    private val wifiUseCase: WifiUseCase,
    private val sincronizarAhoraUseCase: SincronizarAhoraUseCase,
    private val reintentarOperacion: ReintentarOperacionUseCase,
    private val descartarOperacion: DescartarOperacionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EstadoSincronizacionUiState())
    val uiState: StateFlow<EstadoSincronizacionUiState> = _uiState.asStateFlow()

    private val _efectos = Channel<EfectoSincronizacion>(Channel.BUFFERED)
    val efectos: Flow<EfectoSincronizacion> = _efectos.receiveAsFlow()

    private var observacion: Job? = null

    init {
        observar()
    }

    fun observar() {
        observacion?.cancel()
        _uiState.update { it.copy(carga = CargaSincronizacion.CARGANDO) }
        observacion = viewModelScope.launch {
            combine(observarEstado(), wifiUseCase()) { estado, red ->
                estado to (red == NetworkStatus.Connected)
            }
                .catch { _uiState.update { it.copy(carga = CargaSincronizacion.ERROR) } }
                .collect { (estado, conConexion) ->
                    _uiState.update { it.conEstado(estado, conConexion) }
                }
        }
    }

    fun mostrarHoja() = _uiState.update { it.copy(hojaVisible = true) }

    fun ocultarHoja() = _uiState.update { it.copy(hojaVisible = false, porDescartar = null) }

    fun pedirDescartar(fila: FilaSincronizacionUi) = _uiState.update { it.copy(porDescartar = fila) }

    fun cancelarDescartar() = _uiState.update { it.copy(porDescartar = null) }

    fun confirmarDescartar() {
        val fila = _uiState.value.porDescartar ?: return
        _uiState.update { it.copy(porDescartar = null) }
        viewModelScope.launch {
            val efecto = runCatching { descartarOperacion(fila.id) }.fold(
                onSuccess = { EfectoSincronizacion.OperacionDescartada },
                onFailure = { EfectoSincronizacion.AccionFallida }
            )
            _efectos.send(efecto)
        }
    }

    fun reintentar(fila: FilaSincronizacionUi) = ejecutar { reintentarOperacion(fila.id) }

    fun sincronizarAhora() = ejecutar { sincronizarAhoraUseCase() }

    private fun ejecutar(accion: suspend () -> ResultadoProcesamiento) {
        if (_uiState.value.sincronizando) return
        viewModelScope.launch {
            _uiState.update { it.copy(sincronizando = true) }
            val resultado = runCatching { accion() }
            _uiState.update { it.copy(sincronizando = false) }
            when (resultado.getOrNull()) {
                ResultadoProcesamiento.COMPLETA, ResultadoProcesamiento.EN_CURSO -> Unit
                null -> _efectos.send(EfectoSincronizacion.AccionFallida)
                else -> _efectos.send(EfectoSincronizacion.SeEnviaraDespues)
            }
        }
    }
}
