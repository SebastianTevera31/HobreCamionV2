package com.rfz.appflotal.presentation.ui.monitor.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.presentation.ui.home.utils.primaryColor

@Composable
fun MonitorMenuButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val buttonShape = RoundedCornerShape(12.dp)
    Button(
        onClick = onClick,
        enabled = enabled,
        // El fondo deshabilitado es translúcido: con sombra se transparenta un rectángulo blanco.
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (enabled) Modifier.shadow(
                    elevation = 8.dp,
                    shape = buttonShape,
                    ambientColor = primaryColor.copy(alpha = 0.1f)
                ) else Modifier
            ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 12.dp,
            disabledElevation = 0.dp
        ),
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
        shape = buttonShape
    ) {
        Text(text)
    }
}