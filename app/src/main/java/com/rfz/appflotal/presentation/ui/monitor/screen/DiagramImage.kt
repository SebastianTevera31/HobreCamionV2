package com.rfz.appflotal.presentation.ui.monitor.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.data.model.tpms.PositionCoordinatesResponse
import kotlin.math.max

@Composable
fun DiagramImage(
    coordinates: List<PositionCoordinatesResponse>,
    image: Bitmap,
    alertTires: Map<String, Boolean>,
    tireSelected: String,
    modifier: Modifier = Modifier,
) {
    val imageW = image.width
    val imageH = image.height

    val coordinatesMapped = coordinates.map { it ->
        Hotspot.fromPixelCenter(
            id = it.position,
            px = it.fldPositionX.toFloat(),
            py = it.fldPositionY.toFloat(),
            imageHeightPx = imageH,
            imageWidthPx = imageW,
            label = it.position
        )
    }

    ImageWithHotspots(
        img = image.asImageBitmap(),
        hotspots = coordinatesMapped,
        alertTires = alertTires,
        tireSelected = tireSelected,
        modifier = modifier
            .fillMaxWidth()
            .height(170.dp)
    )
}

@Composable
fun ImageWithHotspots(
    img: ImageBitmap,
    hotspots: List<Hotspot>,
    alertTires: Map<String, Boolean>,
    tireSelected: String,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    // Guardamos los bounds de cada burbuja para detectar taps
    val bubbleBounds = remember { mutableStateMapOf<String, Rect>() }

    val imageHeightDp = 200.dp
    val scalePxPerImgPx = with(density) { imageHeightDp.toPx() } / img.height.toFloat()
    val imageWidthDp = with(density) { (img.width * scalePxPerImgPx).toDp() }

    var targetContentX by remember { mutableStateOf<Int?>(null) }
    var viewportWidthPx by remember { mutableIntStateOf(0) }
    var contentWidthPx by remember { mutableIntStateOf(0) }

    LaunchedEffect(targetContentX, contentWidthPx, viewportWidthPx) {
        val x = targetContentX ?: return@LaunchedEffect
        if (contentWidthPx == 0 || viewportWidthPx == 0) return@LaunchedEffect

        // Calcula el desplazamiento deseado
        var desired = x - viewportWidthPx / 2
        // Limita a los bordes
        val maxScroll = (contentWidthPx - viewportWidthPx).coerceAtLeast(0)
        desired = desired.coerceIn(0, maxScroll)

        scrollState.animateScrollTo(desired)
    }

    Box(
        modifier = modifier
            .pointerInput(hotspots) {
                detectTapGestures { tap ->
                    // Checamos de arriba hacia abajo (último dibujado arriba)
                    bubbleBounds.entries.reversed().firstOrNull { (_, rect) ->
                        rect.contains(tap)
                    }?.let { (_, _) -> // id, _
                        // onHotspotClick(id)
                    }
                }
            }
            .height(imageHeightDp)
            .fillMaxWidth()
            .onSizeChanged { viewportWidthPx = it.width }
            .horizontalScroll(scrollState),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .height(imageHeightDp)
                .width(imageWidthDp)
        ) {
            // Capa 1: Imagen (FillBounds para evitar letterboxing)
            Image(
                bitmap = img,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { contentWidthPx = it.width }
            )

            // Capa 2: Canvas con burbujas
            Canvas(Modifier.fillMaxSize()) {
                bubbleBounds.clear()

                hotspots.forEach { h ->
                    val colorStatus = if (alertTires[h.id] == true)
                        Pair(Color.Red, Color.White)
                    else Pair(h.bubbleBg, Color.Black)

                    // Posición del centro en pixeles del canvas
                    val cx = h.center01.x * size.width
                    val cy = h.center01.y * size.height

                    // Medimos el texto
                    val layout = textMeasurer.measure(buildAnnotatedString { append(h.label) })
                    val lbw = 0f
                    val lbh = 0f

                    // Padding de la burbuja (en px) = Tamanio del componente
                    val padH = 22.dp.toPx()
                    val padV = 10.dp.toPx()

                    val bw = lbw + padH * 2f
                    val bh = lbh + padV * 2f

                    // Ancla: centramos la burbuja sobre el punto (puedes ajustar anclaY = 1.0f para ponerla encima)
                    val anchorX = 0.5f
                    val anchorY = 0.5f
                    val left = cx - bw * anchorX
                    val top = cy - bh * anchorY
                    val rect = Rect(left, top, left + bw, top + bh)

                    // Fondo redondeado
                    val radius = max(12.dp.toPx(), bh * 0.4f)
                    drawRoundRect(
                        color = colorStatus.first,
                        topLeft = Offset(rect.left, rect.top),
                        size = Size(rect.width, rect.height),
                        cornerRadius = CornerRadius(radius, radius)
                    )

                    // Trazo suave opcional
                    drawRoundRect(
                        color = if (tireSelected == h.id) Color.Green else h.bubbleStroke,
                        topLeft = Offset(rect.left, rect.top),
                        size = Size(rect.width, rect.height),
                        cornerRadius = CornerRadius(radius, radius),
                        style = if (tireSelected == h.id) Stroke(width = 4.dp.toPx()) else Stroke(
                            width = 1.dp.toPx()
                        )
                    )

                    // Texto centrado
                    val textX =
                        rect.left + (rect.width - layout.size.width) / 2f // Centrado en X
                    val textY =
                        rect.top + (rect.height - layout.size.height) / 2f // Centrado en Y

                    if (tireSelected == h.id) {
                        targetContentX = textX.toInt()
                    }

                    drawText(
                        textLayoutResult = layout,
                        topLeft = Offset(textX, textY),
                        color = h.bubbleText
                    )

                    // Guardamos bounds para taps
                    bubbleBounds[h.id] = rect
                }
            }
        }
    }
}

data class Hotspot(
    val id: String,
    /** Centro normalizado [0f..1f] relativo a ancho/alto de la imagen */
    val center01: Offset,
    val label: String,
    val bubbleBg: Color = Color(0xCC212121),
    val bubbleText: Color = Color.White,
    val bubbleStroke: Color = Color(0x55FFFFFF)
) {
    companion object {
        /** Helper para crear desde pixeles de imagen original (w,h en pixeles) */
        fun fromPixelCenter(
            id: String,
            px: Float,
            py: Float,
            imageWidthPx: Int,
            imageHeightPx: Int,
            label: String,
            bubbleBg: Color = Color(0xCC212121),
            bubbleText: Color = Color.White,
            bubbleStroke: Color = Color(0x55FFFFFF)
        ) = Hotspot(
            id = id,
            center01 = Offset(px / imageWidthPx, py / imageHeightPx),
            label = label,
            bubbleBg = bubbleBg,
            bubbleText = bubbleText,
            bubbleStroke = bubbleStroke
        )
    }
}