package com.rfz.appflotal.presentation.ui.monitor.sincronizacion.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.EstadoFilaSincronizacion
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.FilaSincronizacionUi

@Composable
private fun FilaSincronizacionUi.detalle(): String = stringResource(
    R.string.sinc_fila_detalle,
    stringResource(tipo.nombre()),
    posicion,
    formatearMomento(creadoEn)
)

@Composable
fun FilaRechazada(
    fila: FilaSincronizacionUi,
    habilitada: Boolean,
    onReintentar: () -> Unit,
    onDescartar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(bottom = 8.dp)) {
            ListItem(
                leadingContent = {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = RojoSincronizacion)
                },
                headlineContent = { Text(fila.detalle()) },
                supportingContent = {
                    Text(fila.mensaje ?: stringResource(R.string.sinc_sin_motivo))
                }
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.End),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                TextButton(onClick = onDescartar, enabled = habilitada) {
                    Text(stringResource(R.string.sinc_descartar))
                }
                FilledTonalButton(onClick = onReintentar, enabled = habilitada) {
                    Text(stringResource(R.string.sinc_reintentar))
                }
            }
        }
    }
}

@Composable
fun FilaEnCola(fila: FilaSincronizacionUi, modifier: Modifier = Modifier) {
    val icono = when (fila.estado) {
        EstadoFilaSincronizacion.ENVIANDO -> Icons.Filled.Sync
        EstadoFilaSincronizacion.EN_ESPERA -> Icons.Filled.PauseCircle
        else -> Icons.Filled.CloudUpload
    }
    ListItem(
        leadingContent = { Icon(icono, contentDescription = null) },
        headlineContent = { Text(fila.detalle()) },
        trailingContent = {
            Text(
                text = stringResource(fila.estado.etiqueta()),
                style = MaterialTheme.typography.labelMedium
            )
        },
        modifier = modifier
    )
}

@Composable
fun DialogoDescartarOperacion(
    fila: FilaSincronizacionUi,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = {
            Text(
                stringResource(
                    R.string.sinc_descartar_titulo,
                    stringResource(fila.tipo.nombre()).lowercase(),
                    fila.posicion
                )
            )
        },
        text = { Text(stringResource(R.string.sinc_descartar_mensaje)) },
        confirmButton = {
            TextButton(onClick = onConfirmar) {
                Text(stringResource(R.string.sinc_descartar), color = RojoSincronizacion)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text(stringResource(R.string.sinc_cancelar)) }
        }
    )
}
