package com.rfz.appflotal.presentation.commons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.CompletePlanColors.SubtleText
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.CompletePlanColors.TealDark
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.CompletePlanColors.TealMid
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.CompletePlanColors.TealSoftBg

/**
 * Aviso destacado de "sin conexión" para el tope de una pantalla completa (Home, Foro).
 */
@Composable
fun NoInternetCard(message: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TealSoftBg),
        border = BorderStroke(1.dp, TealMid.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.WifiOff,
                contentDescription = null,
                tint = TealDark
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = TealDark
            )
        }
    }
}

/**
 * Aviso compacto para insertar dentro de una lista/pantalla existente (Alertas, Reportes)
 * cuando ver más datos requiere conexión a internet.
 */
@Composable
fun RequiresInternetNotice(message: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.WifiOff,
            contentDescription = null,
            tint = SubtleText,
            modifier = Modifier.padding(top = 1.dp)
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = SubtleText
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NoInternetCardPreview() {
    HombreCamionTheme {
        NoInternetCard(message = "Sin conexión a internet. Se muestra la última información guardada.")
    }
}

@Preview(showBackground = true)
@Composable
private fun RequiresInternetNoticePreview() {
    HombreCamionTheme {
        RequiresInternetNotice(message = "Sin conexión: se muestran los datos más recientes guardados.")
    }
}
