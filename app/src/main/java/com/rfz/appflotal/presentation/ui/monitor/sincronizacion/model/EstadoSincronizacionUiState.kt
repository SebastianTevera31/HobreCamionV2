package com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model

import com.rfz.appflotal.data.model.sincronizacion.EstadoOperacion
import com.rfz.appflotal.data.model.sincronizacion.EstadoSincronizacion
import com.rfz.appflotal.data.model.sincronizacion.TipoOperacion

enum class IndicadorSincronizacion { SINCRONIZADO, PENDIENTE, ENVIANDO, SIN_CONEXION, REQUIERE_ATENCION }

enum class TipoFilaSincronizacion { MONTAJE, DESMONTAJE, INSPECCION, DESMONTAJE_CON_INSPECCION }

enum class EstadoFilaSincronizacion { ENVIANDO, PENDIENTE, EN_ESPERA, CON_ERROR }

enum class CargaSincronizacion { CARGANDO, LISTA, ERROR }

/** Una fila de la hoja; el desmontaje y su inspección se muestran juntos. */
data class FilaSincronizacionUi(
    /** La operación rechazada del grupo, o la primera: es la que se reintenta o descarta. */
    val id: Long,
    val tipo: TipoFilaSincronizacion,
    val posicion: String,
    val creadoEn: Long,
    val estado: EstadoFilaSincronizacion,
    val mensaje: String?,
)

/** Lo que se muestra de una posición en el diagrama y en el panel de la llanta. */
data class AvisoLlantaUi(
    val tipo: TipoFilaSincronizacion,
    val conError: Boolean,
    val mensaje: String?,
)

data class EstadoSincronizacionUiState(
    val carga: CargaSincronizacion = CargaSincronizacion.CARGANDO,
    val indicador: IndicadorSincronizacion = IndicadorSincronizacion.SINCRONIZADO,
    val conConexion: Boolean = true,
    val sesionVencida: Boolean = false,
    val ultimaSincronizacion: Long? = null,
    val requierenAtencion: List<FilaSincronizacionUi> = emptyList(),
    val enCola: List<FilaSincronizacionUi> = emptyList(),
    val porPosicion: Map<String, AvisoLlantaUi> = emptyMap(),
    val hojaVisible: Boolean = false,
    val porDescartar: FilaSincronizacionUi? = null,
    val sincronizando: Boolean = false,
) {
    /** Número del icono: los rechazos si hay que actuar; si no, lo que falta por enviar. */
    val cantidadIndicador: Int
        get() = if (indicador == IndicadorSincronizacion.REQUIERE_ATENCION) requierenAtencion.size
        else enCola.size
}

sealed interface EfectoSincronizacion {
    data object OperacionDescartada : EfectoSincronizacion
    data object SeEnviaraDespues : EfectoSincronizacion
    data object AccionFallida : EfectoSincronizacion
}

fun EstadoSincronizacionUiState.conEstado(
    estado: EstadoSincronizacion,
    conConexion: Boolean
): EstadoSincronizacionUiState {
    val filas = estado.aFilas()
    val (rechazadas, enCola) = filas.partition { it.estado == EstadoFilaSincronizacion.CON_ERROR }
    return copy(
        carga = CargaSincronizacion.LISTA,
        indicador = indicadorPara(
            rechazadas = rechazadas.size,
            enCola = enCola.size,
            sesionVencida = estado.envio.sesionVencida,
            conConexion = conConexion,
            procesando = estado.envio.procesando
        ),
        conConexion = conConexion,
        sesionVencida = estado.envio.sesionVencida,
        ultimaSincronizacion = estado.envio.ultimaSincronizacion,
        requierenAtencion = rechazadas,
        enCola = enCola,
        porPosicion = filas.groupBy { it.posicion }.mapValues { (_, filasPosicion) ->
            // Un rechazo pesa más que lo pendiente; si no hay, se muestra lo primero que se enviará.
            val fila = filasPosicion.firstOrNull { it.estado == EstadoFilaSincronizacion.CON_ERROR }
                ?: filasPosicion.first()
            AvisoLlantaUi(fila.tipo, fila.estado == EstadoFilaSincronizacion.CON_ERROR, fila.mensaje)
        },
        // Si la operación por descartar ya no existe (se envió o se descartó), se cierra el diálogo.
        porDescartar = porDescartar?.takeIf { pendiente -> filas.any { it.id == pendiente.id } }
    )
}

internal fun indicadorPara(
    rechazadas: Int,
    enCola: Int,
    sesionVencida: Boolean,
    conConexion: Boolean,
    procesando: Boolean
): IndicadorSincronizacion = when {
    rechazadas > 0 || (sesionVencida && enCola > 0) -> IndicadorSincronizacion.REQUIERE_ATENCION
    enCola == 0 -> IndicadorSincronizacion.SINCRONIZADO
    !conConexion -> IndicadorSincronizacion.SIN_CONEXION
    procesando -> IndicadorSincronizacion.ENVIANDO
    else -> IndicadorSincronizacion.PENDIENTE
}

internal fun EstadoSincronizacion.aFilas(): List<FilaSincronizacionUi> =
    operaciones.groupBy { it.idGrupo ?: "operacion-${it.id}" }.values.map { grupo ->
        val rechazada = grupo.firstOrNull { it.estado == EstadoOperacion.CON_ERROR }
        val estadoFila = when {
            rechazada != null -> EstadoFilaSincronizacion.CON_ERROR
            grupo.any { it.id in bloqueadas } -> EstadoFilaSincronizacion.EN_ESPERA
            envio.procesando && grupo.any { it.estado == EstadoOperacion.ENVIANDO } ->
                EstadoFilaSincronizacion.ENVIANDO
            else -> EstadoFilaSincronizacion.PENDIENTE
        }
        FilaSincronizacionUi(
            id = (rechazada ?: grupo.first()).id,
            tipo = tipoFila(grupo.map { it.tipo }),
            posicion = grupo.first().posicion,
            creadoEn = grupo.minOf { it.creadoEn },
            estado = estadoFila,
            mensaje = rechazada?.ultimoError
        )
    }

private fun tipoFila(tipos: List<TipoOperacion>): TipoFilaSincronizacion = when {
    TipoOperacion.DESMONTAJE in tipos && TipoOperacion.INSPECCION in tipos ->
        TipoFilaSincronizacion.DESMONTAJE_CON_INSPECCION
    TipoOperacion.DESMONTAJE in tipos -> TipoFilaSincronizacion.DESMONTAJE
    TipoOperacion.MONTAJE in tipos -> TipoFilaSincronizacion.MONTAJE
    else -> TipoFilaSincronizacion.INSPECCION
}
