package com.rfz.appflotal.presentation.ui.monitor.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.tpms.SensorAlerts
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.AvisoAlertaUi
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.message

internal val FondoAlerta = Color(0xFFFCEBEB)
internal val TextoAlerta = Color(0xFFA32D2D)

/** Con una llanta fijada, avisa de la alerta de otra que puede quedar fuera de la pantalla. */
@Composable
fun AvisoAlertaLlanta(
    aviso: AvisoAlertaUi?,
    onVer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(visible = aviso != null, modifier = modifier) {
        if (aviso == null) return@AnimatedVisibility
        val alerta = stringResource(aviso.alerta.message)
        val texto = if (aviso.adicionales > 0) {
            stringResource(R.string.monitor_aviso_alerta_mas, aviso.posicion, alerta, aviso.adicionales)
        } else stringResource(R.string.monitor_aviso_alerta, aviso.posicion, alerta)

        Surface(
            color = FondoAlerta,
            contentColor = TextoAlerta,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .semantics { liveRegion = LiveRegionMode.Polite }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(start = 12.dp, end = 4.dp)
            ) {
                Icon(Icons.Filled.Warning, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(
                    text = texto,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onVer) {
                    Text(stringResource(R.string.monitor_ver_alerta), color = TextoAlerta)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AvisoAlertaLlantaPreview() {
    AvisoAlertaLlanta(
        aviso = AvisoAlertaUi("P14", SensorAlerts.LOW_PRESSURE, adicionales = 2, posiciones = setOf("P14")),
        onVer = {}
    )
}
