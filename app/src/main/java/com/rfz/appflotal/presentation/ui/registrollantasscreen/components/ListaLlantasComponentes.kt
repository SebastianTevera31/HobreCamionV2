package com.rfz.appflotal.presentation.ui.registrollantasscreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.TireRepair
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

/** Tarjeta de la lista: toda la superficie abre la edición. */
@Composable
fun TarjetaLlanta(
    marca: String,
    modelo: String,
    medida: String,
    tipoAdquisicion: String,
    idLlanta: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = Dimens.PaddingMedium, end = Dimens.PaddingSmall)
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .padding(vertical = Dimens.PaddingMedium)
            ) {
                Text(
                    text = "$marca $modelo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(medida, style = MaterialTheme.typography.bodyMedium)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
                ) {
                    SuggestionChip(onClick = onClick, label = { Text(tipoAdquisicion) })
                    Text(
                        stringResource(R.string.id, idLlanta),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EstadoErrorCarga(mensaje: String, onReintentar: () -> Unit, modifier: Modifier = Modifier) {
    EstadoCentrado(
        icono = Icons.Outlined.CloudOff,
        mensaje = mensaje,
        modifier = modifier
    ) {
        OutlinedButton(onClick = onReintentar) { Text(stringResource(R.string.reintentar)) }
    }
}

@Composable
fun EstadoListaVacia(onRegistrar: () -> Unit, modifier: Modifier = Modifier) {
    EstadoCentrado(
        icono = Icons.Outlined.TireRepair,
        mensaje = stringResource(R.string.rl_lista_vacia),
        modifier = modifier
    ) {
        Button(onClick = onRegistrar) {
            Icon(Icons.Default.Add, contentDescription = null)
            Text(stringResource(R.string.rl_registrar_llanta), Modifier.padding(start = 4.dp))
        }
    }
}

@Composable
fun EstadoSinResultados(busqueda: String, modifier: Modifier = Modifier) {
    EstadoCentrado(
        icono = Icons.Outlined.SearchOff,
        mensaje = stringResource(R.string.rl_sin_resultados, busqueda.trim()),
        modifier = modifier
    )
}

@Composable
private fun EstadoCentrado(
    icono: ImageVector,
    mensaje: String,
    modifier: Modifier = Modifier,
    accion: @Composable () -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.PaddingLarge)
    ) {
        Icon(
            icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(56.dp)
        )
        Spacer(Modifier.height(Dimens.PaddingMedium))
        Text(
            mensaje,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(Dimens.PaddingMedium))
        accion()
    }
}

@Preview(showBackground = true)
@Composable
private fun TarjetaLlantaPreview() {
    HombreCamionTheme {
        TarjetaLlanta(
            marca = "Michelin",
            modelo = "X Multi Z",
            medida = "295/80R22.5",
            tipoAdquisicion = "Compra",
            idLlanta = 1023,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EstadoListaVaciaPreview() {
    HombreCamionTheme { EstadoListaVacia(onRegistrar = {}) }
}
