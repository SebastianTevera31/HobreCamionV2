package com.rfz.appflotal.presentation.ui.home.cierresesion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfz.appflotal.data.NetworkStatus
import com.rfz.appflotal.data.model.sincronizacion.OperacionPendiente
import com.rfz.appflotal.domain.sincronizacion.ObservarEstadoSincronizacionUseCase
import com.rfz.appflotal.domain.sincronizacion.SincronizarAhoraUseCase
import com.rfz.appflotal.domain.wifi.WifiUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AvisoCierreSesionUiState(
    val visible: Boolean = false,
    /** Como en la hoja del Monitor: el desmontaje y su inspección cuentan como una. */
    val sinEnviar: Int = 0,
    val conConexion: Boolean = true,
    val sincronizando: Boolean = false,
)

sealed interface EfectoCierreSesion {
    data object CerrarSesion : EfectoCierreSesion
    data object SeEnviaraDespues : EfectoCierreSesion
}

/** Antes de cerrar sesión avisa si quedan operaciones sin enviar. */
@HiltViewModel
class AvisoCierreSesionViewModel @Inject constructor(
    private val observarEstado: ObservarEstadoSincronizacionUseCase,
    wifiUseCase: WifiUseCase,
    private val sincronizarAhoraUseCase: SincronizarAhoraUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AvisoCierreSesionUiState())
    val uiState: StateFlow<AvisoCierreSesionUiState> = _uiState.asStateFlow()

    private val _efectos = Channel<EfectoCierreSesion>(Channel.BUFFERED)
    val efectos: Flow<EfectoCierreSesion> = _efectos.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(observarEstado(), wifiUseCase()) { estado, red ->
                contarSinEnviar(estado.operaciones) to (red == NetworkStatus.Connected)
            }
                // Si la cola no se puede leer, no se bloquea la salida.
                .catch { _uiState.update { it.copy(sinEnviar = 0) } }
                .collect { (sinEnviar, conConexion) ->
                    _uiState.update { it.copy(sinEnviar = sinEnviar, conConexion = conConexion) }
                }
        }
    }

    fun solicitarCierre() {
        if (_uiState.value.sinEnviar == 0) cerrarSesion()
        else _uiState.update { it.copy(visible = true) }
    }

    fun cancelar() = _uiState.update { it.copy(visible = false) }

    fun confirmar() = cerrarSesion()

    /** Si se envía todo, sigue con el cierre que pidió el usuario. */
    fun sincronizarAhora() {
        if (_uiState.value.sincronizando) return
        viewModelScope.launch {
            _uiState.update { it.copy(sincronizando = true) }
            runCatching { sincronizarAhoraUseCase() }
            // Se consulta de nuevo: la observación puede no haber recibido aún el último cambio.
            val restantes = runCatching { contarSinEnviar(observarEstado().first().operaciones) }
                .getOrDefault(_uiState.value.sinEnviar)
            _uiState.update { it.copy(sincronizando = false, sinEnviar = restantes) }
            if (restantes == 0) cerrarSesion()
            else _efectos.send(EfectoCierreSesion.SeEnviaraDespues)
        }
    }

    private fun cerrarSesion() {
        _uiState.update { it.copy(visible = false) }
        viewModelScope.launch { _efectos.send(EfectoCierreSesion.CerrarSesion) }
    }
}

internal fun contarSinEnviar(operaciones: List<OperacionPendiente>): Int =
    operaciones.distinctBy { it.idGrupo ?: "operacion-${it.id}" }.size
