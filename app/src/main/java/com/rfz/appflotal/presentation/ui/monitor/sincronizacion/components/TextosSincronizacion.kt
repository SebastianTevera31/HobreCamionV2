package com.rfz.appflotal.presentation.ui.monitor.sincronizacion.components

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.EfectoSincronizacion
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.EstadoFilaSincronizacion
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.TipoFilaSincronizacion
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal val AmbarSincronizacion = Color(0xFFFFB300)
internal val RojoSincronizacion = Color(0xFFC62828)

@StringRes
internal fun TipoFilaSincronizacion.nombre(): Int = when (this) {
    TipoFilaSincronizacion.MONTAJE -> R.string.sinc_tipo_montaje
    TipoFilaSincronizacion.DESMONTAJE -> R.string.sinc_tipo_desmontaje
    TipoFilaSincronizacion.INSPECCION -> R.string.sinc_tipo_inspeccion
    TipoFilaSincronizacion.DESMONTAJE_CON_INSPECCION -> R.string.sinc_tipo_desmontaje_inspeccion
}

@StringRes
internal fun EstadoFilaSincronizacion.etiqueta(): Int = when (this) {
    EstadoFilaSincronizacion.ENVIANDO -> R.string.sinc_fila_enviando
    EstadoFilaSincronizacion.EN_ESPERA -> R.string.sinc_fila_en_espera
    else -> R.string.sinc_fila_pendiente
}

@StringRes
internal fun EfectoSincronizacion.mensaje(): Int = when (this) {
    EfectoSincronizacion.OperacionDescartada -> R.string.sinc_operacion_descartada
    EfectoSincronizacion.SeEnviaraDespues -> R.string.sinc_se_enviara_despues
    EfectoSincronizacion.AccionFallida -> R.string.sinc_accion_fallida
}

internal fun formatearMomento(momento: Long): String =
    SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(momento))
