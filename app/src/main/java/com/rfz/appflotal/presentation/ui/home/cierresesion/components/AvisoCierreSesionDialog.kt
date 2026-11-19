package com.rfz.appflotal.presentation.ui.home.cierresesion.components

import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.ui.home.cierresesion.viewmodel.AvisoCierreSesionUiState
import com.rfz.appflotal.presentation.ui.home.cierresesion.viewmodel.AvisoCierreSesionViewModel
import com.rfz.appflotal.presentation.ui.home.cierresesion.viewmodel.EfectoCierreSesion

/** Escucha la confirmación y muestra el aviso; [onCerrarSesion] hace el cierre real. */
@Composable
fun AvisoCierreSesionRoute(
    viewModel: AvisoCierreSesionViewModel,
    onCerrarSesion: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.efectos.collect { efecto ->
            when (efecto) {
                EfectoCierreSesion.CerrarSesion -> onCerrarSesion()
                EfectoCierreSesion.SeEnviaraDespues -> Toast.makeText(
                    context, R.string.sinc_se_enviara_despues, Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    if (uiState.visible) {
        AvisoCierreSesionDialog(
            uiState = uiState,
            onSincronizarAhora = viewModel::sincronizarAhora,
            onCancelar = viewModel::cancelar,
            onSalir = viewModel::confirmar
        )
    }
}

@Composable
fun AvisoCierreSesionDialog(
    uiState: AvisoCierreSesionUiState,
    onSincronizarAhora: () -> Unit,
    onCancelar: () -> Unit,
    onSalir: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        icon = { Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFFFB300)) },
        title = {
            Text(pluralStringResource(R.plurals.sinc_cierre_titulo, uiState.sinEnviar, uiState.sinEnviar))
        },
        text = { Text(stringResource(R.string.sinc_cierre_mensaje)) },
        dismissButton = {
            Row {
                if (uiState.conConexion) {
                    TextButton(onClick = onSincronizarAhora, enabled = !uiState.sincronizando) {
                        if (uiState.sincronizando) {
                            CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                        } else {
                            Text(stringResource(R.string.sinc_sincronizar_ahora))
                        }
                    }
                }
                TextButton(onClick = onCancelar) { Text(stringResource(R.string.sinc_cancelar)) }
            }
        },
        confirmButton = {
            TextButton(onClick = onSalir, enabled = !uiState.sincronizando) {
                Text(stringResource(R.string.sinc_cierre_salir))
            }
        }
    )
}

@Preview
@Composable
private fun AvisoCierreSesionDialogPreview() {
    AvisoCierreSesionDialog(
        uiState = AvisoCierreSesionUiState(visible = true, sinEnviar = 3),
        onSincronizarAhora = {}, onCancelar = {}, onSalir = {}
    )
}

@Preview
@Composable
private fun AvisoCierreSesionDialogSinConexionPreview() {
    AvisoCierreSesionDialog(
        uiState = AvisoCierreSesionUiState(visible = true, sinEnviar = 1, conConexion = false),
        onSincronizarAhora = {}, onCancelar = {}, onSalir = {}
    )
}
