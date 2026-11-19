package com.rfz.appflotal.presentation.ui.monitor.sincronizacion.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.AvisoLlantaUi
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.TipoFilaSincronizacion

/** Línea del panel de la llanta cuando su posición tiene operaciones sin enviar o rechazadas. */
@Composable
fun AvisoSincronizacionLlanta(
    aviso: AvisoLlantaUi,
    onVerDetalle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (fondo, contenido) =
        if (aviso.conError) Color(0xFFFFEBEE) to RojoSincronizacion
        else Color(0xFFFFF8E1) to Color(0xFF7A5900)

    Surface(
        color = fondo,
        contentColor = contenido,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = if (aviso.conError) Icons.Filled.Warning else Icons.Filled.CloudUpload,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = stringResource(aviso.titulo()),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (aviso.conError) {
                Text(
                    text = aviso.mensaje ?: stringResource(R.string.sinc_sin_motivo),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                TextButton(
                    onClick = onVerDetalle,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(R.string.sinc_ver_detalle), color = contenido)
                }
            }
        }
    }
}

@Preview(widthDp = 200)
@Composable
private fun AvisoSincronizacionLlantaPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AvisoSincronizacionLlanta(
            aviso = AvisoLlantaUi(TipoFilaSincronizacion.MONTAJE, conError = false, mensaje = null),
            onVerDetalle = {}
        )
        AvisoSincronizacionLlanta(
            aviso = AvisoLlantaUi(TipoFilaSincronizacion.DESMONTAJE, conError = true, mensaje = null),
            onVerDetalle = {}
        )
    }
}
