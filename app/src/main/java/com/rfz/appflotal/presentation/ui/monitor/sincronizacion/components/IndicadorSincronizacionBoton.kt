package com.rfz.appflotal.presentation.ui.monitor.sincronizacion.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.primaryLight
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.IndicadorSincronizacion

/** Icono de la barra superior del Monitor; abre la hoja de sincronización. */
@Composable
fun IndicadorSincronizacionBoton(
    indicador: IndicadorSincronizacion,
    cantidad: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (icono, tinte) = when (indicador) {
        IndicadorSincronizacion.SINCRONIZADO -> Icons.Filled.CloudDone to Color.White
        IndicadorSincronizacion.PENDIENTE -> Icons.Filled.CloudUpload to Color.White
        IndicadorSincronizacion.ENVIANDO -> Icons.Filled.Sync to Color.White
        IndicadorSincronizacion.SIN_CONEXION -> Icons.Filled.CloudOff to AmbarSincronizacion
        IndicadorSincronizacion.REQUIERE_ATENCION -> Icons.Filled.SyncProblem to Color(0xFFFF8A80)
    }
    val descripcion = when (indicador) {
        IndicadorSincronizacion.SINCRONIZADO -> stringResource(R.string.sinc_todo_sincronizado)
        IndicadorSincronizacion.REQUIERE_ATENCION ->
            pluralStringResource(R.plurals.sinc_desc_rechazos, cantidad, cantidad)
        else -> pluralStringResource(R.plurals.sinc_desc_pendientes, cantidad, cantidad)
    }

    val giro = rememberInfiniteTransition(label = "giroSincronizacion")
    val angulo by giro.animateFloat(
        initialValue = 0f,
        targetValue = -360f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart),
        label = "anguloSincronizacion"
    )

    IconButton(
        onClick = onClick,
        modifier = modifier.semantics { contentDescription = descripcion }
    ) {
        BadgedBox(
            badge = {
                if (indicador != IndicadorSincronizacion.SINCRONIZADO && cantidad > 0) {
                    val esError = indicador == IndicadorSincronizacion.REQUIERE_ATENCION
                    Badge(
                        containerColor = if (esError) RojoSincronizacion else AmbarSincronizacion,
                        contentColor = if (esError) Color.White else Color.Black
                    ) { Text("$cantidad") }
                }
            }
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = tinte,
                modifier = if (indicador == IndicadorSincronizacion.ENVIANDO) Modifier.rotate(angulo)
                else Modifier
            )
        }
    }
}

@Preview
@Composable
private fun IndicadorSincronizacionBotonPreview() {
    Row(Modifier.background(primaryLight)) {
        IndicadorSincronizacion.entries.forEach {
            IndicadorSincronizacionBoton(indicador = it, cantidad = 3, onClick = {})
        }
    }
}
