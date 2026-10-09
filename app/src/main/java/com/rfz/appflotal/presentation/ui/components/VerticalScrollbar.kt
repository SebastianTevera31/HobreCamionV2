package com.rfz.appflotal.presentation.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Dibuja una barra de desplazamiento en el borde derecho, sincronizada con [scrollState].
 * Solo aparece cuando el contenido excede el alto visible.
 *
 * Debe aplicarse al contenedor que tiene el mismo alto que la zona visible del scroll
 * (no al contenido que se desplaza), para que las proporciones sean correctas.
 */
fun Modifier.verticalScrollbar(
    scrollState: ScrollState,
    color: Color,
    width: Dp = 4.dp,
    edgePadding: Dp = 4.dp,
    minThumbHeight: Dp = 24.dp
): Modifier = drawWithContent {
    drawContent()
    if (scrollState.maxValue > 0) {
        val viewport = size.height
        val total = viewport + scrollState.maxValue
        val thumbHeight = (viewport * viewport / total)
            .coerceAtLeast(minThumbHeight.toPx())
            .coerceAtMost(viewport)
        val thumbTop = scrollState.value.toFloat() / scrollState.maxValue *
                (viewport - thumbHeight)
        val thumbWidth = width.toPx()
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width - thumbWidth - edgePadding.toPx(), thumbTop),
            size = Size(thumbWidth, thumbHeight),
            cornerRadius = CornerRadius(thumbWidth / 2)
        )
    }
}
