package com.rfz.appflotal.presentation.ui.dissassembly.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.domain.sincronizacion.BloqueoInspeccion
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

/** Explica por qué el desmontaje abre sin el paso de inspección. */
@Composable
fun AvisoInspeccionOmitida(
    motivo: BloqueoInspeccion,
    odometro: Int,
    unidadOdometro: String,
    modifier: Modifier = Modifier,
) {
    val (icono, texto) = when (motivo) {
        BloqueoInspeccion.INSPECCIONADA_HOY ->
            Icons.Filled.CheckCircle to R.string.desmontaje_inspeccion_de_hoy
        BloqueoInspeccion.MONTADA_HOY -> Icons.Outlined.Info to R.string.desmontaje_montada_hoy
    }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(imageVector = icono, contentDescription = null)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = stringResource(texto), style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = stringResource(R.string.odometro_registrado, odometro, unidadOdometro),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AvisoInspeccionOmitidaPreview() {
    HombreCamionTheme {
        AvisoInspeccionOmitida(
            motivo = BloqueoInspeccion.INSPECCIONADA_HOY,
            odometro = 152300,
            unidadOdometro = "km"
        )
    }
}
