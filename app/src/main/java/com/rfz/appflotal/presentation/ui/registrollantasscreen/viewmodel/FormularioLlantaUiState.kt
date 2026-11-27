package com.rfz.appflotal.presentation.ui.registrollantasscreen.viewmodel

import com.rfz.appflotal.presentation.ui.registrollantasscreen.model.CampoLlanta
import com.rfz.appflotal.presentation.ui.registrollantasscreen.model.CamposLlanta
import com.rfz.appflotal.presentation.ui.registrollantasscreen.model.ErrorCampo
import com.rfz.appflotal.presentation.ui.registrollantasscreen.model.ProductoUi
import com.rfz.appflotal.presentation.ui.registrollantasscreen.model.TipoAdquisicionUi

enum class EstadoCarga { CARGANDO, LISTO, ERROR }

data class FormularioLlantaUiState(
    val esEdicion: Boolean = false,
    val estadoCarga: EstadoCarga = EstadoCarga.CARGANDO,
    val tiposAdquisicion: List<TipoAdquisicionUi> = emptyList(),
    val productos: List<ProductoUi> = emptyList(),
    val campos: CamposLlanta = CamposLlanta(),
    /** Ver TireEditPolicy; siempre true en el alta. */
    val adquisicionEditable: Boolean = true,
    val errores: Map<CampoLlanta, ErrorCampo> = emptyMap(),
    val guardando: Boolean = false,
    val hayCambios: Boolean = false,
) {
    val camposHabilitados: Boolean get() = estadoCarga == EstadoCarga.LISTO && !guardando
}

sealed interface EventoFormularioLlanta {
    data object Guardada : EventoFormularioLlanta
    data object CamposIncompletos : EventoFormularioLlanta

    /** [mensaje] es el que devuelve el servidor; null si no mandó ninguno. */
    data class ErrorAlGuardar(val mensaje: String?) : EventoFormularioLlanta
}
