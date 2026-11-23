package com.rfz.appflotal.presentation.ui.monitor.sincronizacion.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.ui.languaje.LocalizedApp
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.AvisoLlantaUi
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.EstadoFilaSincronizacion
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.FilaSincronizacionUi
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.TipoFilaSincronizacion

/**
 * Icono del encabezado del panel cuando la llanta tiene algo sin enviar; al tocarlo muestra
 * el detalle. Se ve igual que la insignia de la llanta en el diagrama.
 */
@Composable
fun IndicadorSincronizacionLlanta(
    posicion: String,
    aviso: AvisoLlantaUi,
    onVerSincronizacion: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var dialogoVisible by remember { mutableStateOf(false) }

    IconButton(onClick = { dialogoVisible = true }, modifier = modifier.size(36.dp)) {
        Icon(
            imageVector = if (aviso.conError) Icons.Filled.Warning else Icons.Filled.CloudUpload,
            contentDescription = stringResource(R.string.sinc_desc_icono_llanta),
            tint = if (aviso.conError) RojoSincronizacion else AmbarSincronizacion
        )
    }

    if (dialogoVisible) {
        DialogoSincronizacionLlanta(
            posicion = posicion,
            filas = aviso.filas,
            onVerSincronizacion = {
                dialogoVisible = false
                onVerSincronizacion()
            },
            onCerrar = { dialogoVisible = false }
        )
    }
}

@Composable
fun DialogoSincronizacionLlanta(
    posicion: String,
    filas: List<FilaSincronizacionUi>,
    onVerSincronizacion: () -> Unit,
    onCerrar: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = {
            LocalizedApp {
                Text(stringResource(R.string.sinc_llanta_titulo, posicion))
            }
        },
        text = {
            LocalizedApp {
                Column {
                    filas.forEach { fila ->
                        ListItem(
                            leadingContent = {
                                Icon(
                                    imageVector = when (fila.estado) {
                                        EstadoFilaSincronizacion.CON_ERROR -> Icons.Filled.Warning
                                        EstadoFilaSincronizacion.ENVIANDO -> Icons.Filled.Sync
                                        EstadoFilaSincronizacion.EN_ESPERA -> Icons.Filled.PauseCircle
                                        EstadoFilaSincronizacion.PENDIENTE -> Icons.Filled.CloudUpload
                                    },
                                    contentDescription = null,
                                    tint = if (fila.estado == EstadoFilaSincronizacion.CON_ERROR) RojoSincronizacion
                                    else AmbarSincronizacion
                                )
                            },
                            headlineContent = {
                                Text(
                                    "${stringResource(fila.tipo.nombre())} · ${
                                        formatearMomento(
                                            fila.creadoEn
                                        )
                                    }"
                                )
                            },
                            supportingContent = { Text(fila.detalle()) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            LocalizedApp {
                TextButton(onClick = onVerSincronizacion) {
                    Text(stringResource(R.string.sinc_ver_sincronizacion))
                }
            }
        },
        dismissButton = {
            LocalizedApp {
                TextButton(onClick = onCerrar) { Text(stringResource(R.string.sinc_cerrar)) }
            }
        }
    )
}

@Composable
private fun FilaSincronizacionUi.detalle(): String = when (estado) {
    EstadoFilaSincronizacion.CON_ERROR -> mensaje ?: stringResource(R.string.sinc_sin_motivo)
    EstadoFilaSincronizacion.ENVIANDO -> stringResource(R.string.sinc_enviando)
    EstadoFilaSincronizacion.EN_ESPERA -> stringResource(R.string.sinc_llanta_en_espera)
    EstadoFilaSincronizacion.PENDIENTE -> stringResource(R.string.sinc_llanta_pendiente)
}

@Preview
@Composable
private fun DialogoSincronizacionLlantaPreview() {
    DialogoSincronizacionLlanta(
        posicion = "P2",
        filas = listOf(
            FilaSincronizacionUi(
                1,
                TipoFilaSincronizacion.INSPECCION,
                "P2",
                0,
                EstadoFilaSincronizacion.PENDIENTE,
                null
            ),
            FilaSincronizacionUi(
                2,
                TipoFilaSincronizacion.DESMONTAJE,
                "P2",
                0,
                EstadoFilaSincronizacion.CON_ERROR,
                null
            ),
        ),
        onVerSincronizacion = {},
        onCerrar = {}
    )
}
