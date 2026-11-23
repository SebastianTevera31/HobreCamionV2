package com.rfz.appflotal.presentation.ui.monitor.sincronizacion.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.DatosOfflineUi

/** Al final de la hoja: catálogos guardados para montar, desmontar e inspeccionar sin red. */
@Composable
fun SeccionDatosOffline(
    datos: DatosOfflineUi,
    conConexion: Boolean,
    onActualizar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (estado, colorEstado) = when {
        datos.actualizando -> stringResource(R.string.sinc_datos_descargando) to MaterialTheme.colorScheme.onSurfaceVariant
        datos.error -> stringResource(R.string.sinc_datos_error) to RojoSincronizacion
        datos.actualizadoEn == null -> stringResource(R.string.sinc_datos_vacios) to AmbarSincronizacion
        else -> stringResource(R.string.sinc_datos_actualizados, formatearMomento(datos.actualizadoEn)) to
            MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(modifier = modifier.fillMaxWidth().padding(top = 12.dp)) {
        HorizontalDivider()
        Text(
            text = stringResource(R.string.sinc_datos_offline),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 12.dp)
        )
        ListItem(
            leadingContent = { Icon(Icons.Filled.CloudDownload, contentDescription = null) },
            headlineContent = { Text(stringResource(R.string.sinc_datos_detalle)) },
            supportingContent = { Text(estado, color = colorEstado) },
            trailingContent = {
                TextButton(onClick = onActualizar, enabled = conConexion && !datos.actualizando) {
                    Text(stringResource(R.string.sinc_actualizar))
                }
            }
        )
        if (datos.actualizando) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }
}

@Preview(showBackground = true)
@Composable
private fun SeccionDatosOfflinePreview() {
    Column {
        SeccionDatosOffline(DatosOfflineUi(actualizadoEn = 0), conConexion = true, onActualizar = {})
        SeccionDatosOffline(DatosOfflineUi(), conConexion = true, onActualizar = {})
        SeccionDatosOffline(DatosOfflineUi(actualizando = true), conConexion = true, onActualizar = {})
        SeccionDatosOffline(DatosOfflineUi(error = true), conConexion = true, onActualizar = {})
    }
}
