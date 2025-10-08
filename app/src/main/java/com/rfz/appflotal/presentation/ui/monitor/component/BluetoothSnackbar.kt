package com.rfz.appflotal.presentation.ui.monitor.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt

@Composable
fun BluetoothSnackBanner(
    visible: Boolean,
    message: String,
    modifier: Modifier = Modifier,
    containerColor: Color = Color("#FFF3E0".toColorInt()), // puedes personalizar
    contentColor: Color = Color("#D84315".toColorInt())
) {
    val density = LocalDensity.current

    // Se monta/desmonta con animación de entrada/salida desde el borde superior
    Box(modifier.fillMaxWidth()) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                animationSpec = tween(2000)
            ) { fullHeight -> -fullHeight } + expandVertically(
                expandFrom = Alignment.Top
            ) + fadeIn(initialAlpha = 0.3f),
            exit = slideOutVertically(animationSpec = tween(2000)) { with(density) { -40.dp.roundToPx() } } + shrinkVertically() + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Surface(
                color = containerColor,
                contentColor = contentColor,
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 3.dp,
                shadowElevation = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 800.dp)
            ) {
                Row(
                    Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}