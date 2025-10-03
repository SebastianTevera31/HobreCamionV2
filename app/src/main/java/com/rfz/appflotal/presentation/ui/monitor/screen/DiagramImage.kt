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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.Tire
import kotlin.math.max

@Composable
fun DiagramImage(
    tires: List<Tire>,
    image: Bitmap,
    width: Int,
    height: Int,
    tireSelected: String,
    modifier: Modifier = Modifier,
) {
    val tireMapped = tires.map { it ->
        Hotspot.fromPixelCenter(
            id = it.sensorPosition,
            px = it.xPosition.toFloat(),
            py = it.yPosition.toFloat(),
            inAlert = it.inAlert,
            isActive = it.isActive,
            imageHeightPx = height,
            imageWidthPx = width,
            label = it.sensorPosition
        )
    }

    ImageWithHotspotsProportional(
        img = image.asImageBitmap(),
        hotspots = tireMapped,
        tireSelected = tireSelected,
        modifier = modifier
            .fillMaxWidth()
            .height(170.dp)
    )
}

@Composable
fun ImageWithHotspotsProportional(
    img: ImageBitmap,
    hotspots: List<Hotspot>,
    tireSelected: String,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val scrollState = rememberScrollState()

    var viewportWidthPx by remember { mutableIntStateOf(0) }
    var targetContentX by remember { mutableStateOf<Int?>(null) }
    val bubbleBounds = remember { mutableStateMapOf<String, Rect>() }

    // Calculamos la proporción de la imagen
    val imageHeightDp = 200.dp
    val aspectRatio = img.width.toFloat() / img.height.toFloat()
    val imageWidthDp = imageHeightDp * aspectRatio

    LaunchedEffect(targetContentX, viewportWidthPx) {
        val x = targetContentX ?: return@LaunchedEffect
        if (viewportWidthPx == 0) return@LaunchedEffect
        scrollState.animateScrollTo((x - viewportWidthPx / 2).coerceAtLeast(0))
    }

    Box(
        modifier = modifier
            .pointerInput(hotspots) {
                detectTapGestures { tap ->
                    bubbleBounds.entries.reversed().firstOrNull { it.value.contains(tap) }
                        ?.let { (id, _) ->
                            // onHotspotClick(id)
                        }
                }
            }
            .onSizeChanged { viewportWidthPx = it.width }
            .horizontalScroll(scrollState),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .height(imageHeightDp)
                .width(imageWidthDp)
        ) {
            // Imagen proporcional
            Image(
                bitmap = img,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            // Canvas de hotspots
            Canvas(modifier = Modifier.fillMaxSize()) {
                bubbleBounds.clear()
                hotspots.forEach { h ->
                    val colorStatus = if (h.inAlert)
                        Pair(Color.Red, Color.White)
                    else if (h.isActive) {
                        Pair(h.bubbleBg, Color.Black)
                    } else Pair(
                        Color.Gray,
                        Color.Gray
                    )

                    // Posición del centro en pixeles del canvas
                    val cx = h.center01.x * size.width
                    val cy = h.center01.y * size.height

                    // Tamaño de burbuja
                    val padH = 22.dp.toPx()
                    val padV = 10.dp.toPx()
                    val bw = padH * 2f
                    val bh = padV * 2f

                    val left = cx - bw / 2f
                    val top = cy - bh / 2f
                    val rect = Rect(left, top, left + bw, top + bh)

                    // Fondo redondeado
                    val radius = max(12.dp.toPx(), bh * 0.4f)
                    drawRoundRect(
                        color = colorStatus.first,
                        topLeft = Offset(rect.left, rect.top),
                        size = Size(rect.width, rect.height),
                        cornerRadius = CornerRadius(radius, radius)
                    )

                    // Trazo
                    drawRoundRect(
                        color = if (tireSelected == h.id) Color.Green else h.bubbleStroke,
                        topLeft = Offset(rect.left, rect.top),
                        size = Size(rect.width, rect.height),
                        cornerRadius = CornerRadius(radius, radius),
                        style = Stroke(width = if (tireSelected == h.id) 4.dp.toPx() else 1.dp.toPx())
                    )

                    // Texto centrado
                    val layout = textMeasurer.measure(buildAnnotatedString { append(h.label) })
                    val textX = rect.left + (rect.width - layout.size.width) / 2f
                    val textY = rect.top + (rect.height - layout.size.height) / 2f
                    drawText(layout, topLeft = Offset(textX, textY), color = h.bubbleText)

                    // Guardamos bounds
                    bubbleBounds[h.id] = rect

                    // Scroll automático
                    if (tireSelected == h.id) {
                        targetContentX = cx.toInt()
                    }
                }
            }
        }
    }
}

data class Hotspot(
    val id: String,
    /** Centro normalizado [0f..1f] relativo a ancho/alto de la imagen */
    val center01: Offset,
    val inAlert: Boolean,
    val isActive: Boolean,
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
            inAlert: Boolean,
            isActive: Boolean,
            imageWidthPx: Int,
            imageHeightPx: Int,
            label: String,
            bubbleBg: Color = Color(0xCC212121),
            bubbleText: Color = Color.White,
            bubbleStroke: Color = Color(0x55FFFFFF)
        ) = Hotspot(
            id = id,
            center01 = Offset(px / imageWidthPx.toFloat(), py / imageHeightPx.toFloat()),
            label = label,
            inAlert = inAlert,
            isActive = isActive,
            bubbleBg = bubbleBg,
            bubbleText = bubbleText,
            bubbleStroke = bubbleStroke
        )
    }
}

//@Composable
//@Preview(showBackground = true, showSystemUi = true)
//fun DiagramaImagenPreview() {
//    val context = LocalContext.current
//    val drawable = ContextCompat.getDrawable(context, R.drawable.base32) as BitmapDrawable
//    val bitmap = drawable.bitmap
//    val coordinates = listOf(
//        PositionCoordinatesResponse(38, 0,0, 133, 30,  "P1"),
//        PositionCoordinatesResponse(38, 0, 133, 234,  "P2"),
//        PositionCoordinatesResponse(38, 0, 323, 234, , "P3"),
//        PositionCoordinatesResponse(38, 0, 323, 197, , "P4"),   // +5
//        PositionCoordinatesResponse(38, 0, 323, 67,  "P5"),  // -5
//        PositionCoordinatesResponse(38, 0, 323, 30, , "P6"),
//        PositionCoordinatesResponse(38, 0, 457, 30,  "P7"),
//        PositionCoordinatesResponse(38, 0, 457, 67,  "P8"),   // +5
//        PositionCoordinatesResponse(38, 0, 457, 234,  "P9"),  // -5
//        PositionCoordinatesResponse(38, 0, 457, 197,  "P10"),
//        PositionCoordinatesResponse(38, 0, 563, 30, , "P11"),
//        PositionCoordinatesResponse(38, 0, 563, 67, 0, "P12"),  // +5
//        PositionCoordinatesResponse(38, 0, 563, 234, 0, "P13"), // -5
//        PositionCoordinatesResponse(38, 0, 563, 197, 0, "P14"),
//        PositionCoordinatesResponse(38, 0, 740, 30, 0, "P15"),
//        PositionCoordinatesResponse(38, 0, 740, 67, 0, "P16"),  // +5
//        PositionCoordinatesResponse(38, 0, 740, 234, 0, "P17"), // -5
//        PositionCoordinatesResponse(38, 0, 740, 197, 0, "P18"),
//        PositionCoordinatesResponse(38, 0, 853, 30, 0, "P19"),
//        PositionCoordinatesResponse(38, 0, 853, 67, 0, "P20"),  // +5
//        PositionCoordinatesResponse(38, 0, 853, 234, 0, "P21"), // -5
//        PositionCoordinatesResponse(38, 0, 853, 197, 0, "P22"),
//        PositionCoordinatesResponse(38, 0, 967, 30, 0, "P23"),
//        PositionCoordinatesResponse(38, 0, 967, 67, 0, "P24"),  // +5
//        PositionCoordinatesResponse(38, 0, 967, 234, 0, "P25"), // -5
//        PositionCoordinatesResponse(38, 0, 967, 197, 0, "P26"),
//        PositionCoordinatesResponse(38, 0, 1163, 30, 0, "P27"),
//        PositionCoordinatesResponse(38, 0, 1163, 67, 0, "P28"),  // +5
//        PositionCoordinatesResponse(38, 0, 1163, 234, 0, "P29"), // -5
//        PositionCoordinatesResponse(38, 0, 1163, 197, 0, "P30"),
//        PositionCoordinatesResponse(38, 0, 1275, 30, 0, "P31"),
//        PositionCoordinatesResponse(38, 0, 1275, 67, 0, "P32"),  // +5
//        PositionCoordinatesResponse(38, 0, 1275, 234, 0, "P33"), // -5
//        PositionCoordinatesResponse(38, 0, 1275, 197, 0, "P34"),
//        PositionCoordinatesResponse(38, 0, 1390, 30, 0, "P35"),
//        PositionCoordinatesResponse(38, 0, 1390, 67, 0, "P36"),  // +5
//        PositionCoordinatesResponse(38, 0, 1390, 234, 0, "P37"), // -5
//        PositionCoordinatesResponse(38, 0, 1390, 197, 0, "P38")
//    )
//
//    if (bitmap != null) {
//        DiagramImage(
//            coordinates = coordinates,
//            image = bitmap,
//            alertTires = emptyMap(),
//            tireSelected = "",
//            height = 123,
//            width = 123,
//        )
//    }
//
//}