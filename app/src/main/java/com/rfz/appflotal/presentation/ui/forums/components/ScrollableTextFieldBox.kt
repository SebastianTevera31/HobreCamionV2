package com.rfz.appflotal.presentation.ui.forums.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val ScrollbarWidth = 4.dp
private val ScrollbarEdgePadding = 4.dp
private val ScrollbarMinThumbHeight = 24.dp

/**
 * Limita la altura de un campo de texto multilínea a [maxHeight]. Cuando el contenido excede
 * ese alto, el campo se desplaza y se dibuja una barra indicadora en el borde derecho para que
 * el usuario note que hay más texto por ver.
 *
 * El campo recibido en [content] no debe imponer su propio alto máximo.
 */
@Composable
fun ScrollableTextFieldBox(
    maxHeight: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()
    val thumbColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = maxHeight)
            .drawWithContent {
                drawContent()
                if (scrollState.maxValue > 0) {
                    val viewport = size.height
                    val total = viewport + scrollState.maxValue
                    val thumbHeight = (viewport * viewport / total)
                        .coerceAtLeast(ScrollbarMinThumbHeight.toPx())
                        .coerceAtMost(viewport)
                    val thumbTop = scrollState.value.toFloat() / scrollState.maxValue *
                            (viewport - thumbHeight)
                    val width = ScrollbarWidth.toPx()
                    drawRoundRect(
                        color = thumbColor,
                        topLeft = Offset(size.width - width - ScrollbarEdgePadding.toPx(), thumbTop),
                        size = Size(width, thumbHeight),
                        cornerRadius = CornerRadius(width / 2)
                    )
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            content()
        }
    }
}
