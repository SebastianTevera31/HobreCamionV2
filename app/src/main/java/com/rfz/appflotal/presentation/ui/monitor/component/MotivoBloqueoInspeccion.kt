package com.rfz.appflotal.presentation.ui.monitor.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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

/**
 * Ocupa el lugar del botón "Inspeccionar" cuando hoy no se puede: muestra el estado en vez de
 * un botón muerto. Misma altura y forma que [MonitorMenuButton] para no mover el panel.
 */
@Composable
fun MotivoBloqueoInspeccion(
    bloqueo: BloqueoInspeccion,
    modifier: Modifier = Modifier,
) {
    val (icono, texto) = when (bloqueo) {
        BloqueoInspeccion.INSPECCIONADA_HOY ->
            Icons.Filled.CheckCircle to R.string.inspeccion_bloqueada_inspeccionada_hoy
        BloqueoInspeccion.MONTADA_HOY -> Icons.Outlined.Info to R.string.inspeccion_bloqueada_montada_hoy
    }
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = ButtonDefaults.MinHeight)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            modifier = Modifier.padding(ButtonDefaults.ContentPadding)
        ) {
            Icon(imageVector = icono, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(text = stringResource(texto), style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MotivoBloqueoInspeccionPreview() {
    HombreCamionTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(8.dp)) {
            MotivoBloqueoInspeccion(BloqueoInspeccion.INSPECCIONADA_HOY)
            MotivoBloqueoInspeccion(BloqueoInspeccion.MONTADA_HOY)
        }
    }
}
