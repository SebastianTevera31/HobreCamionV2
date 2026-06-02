package com.rfz.appflotal.presentation.ui.couponbook

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class QrCodeConfig(
    val content: String,
    val imageSizePx: Int = 1024,
    val foregroundColor: Int = Color.Black.toColorLong().toInt(),
    val backgroundColor: Int = Color.White.toColorLong().toInt(),
    val marginModules: Int = 4
)

fun generateQrCodeBitmap(
    config: QrCodeConfig
): Result<Bitmap> {
    return runCatching {
        require(config.content.isNotBlank()) {
            "El contenido del código QR no puede estar vacío."
        }

        require(config.imageSizePx > 0) {
            "El tamaño de la imagen debe ser mayor que cero."
        }

        require(config.marginModules >= 0) {
            "El margen no puede ser negativo."
        }

        val hints = EnumMap<EncodeHintType, Any>(
            EncodeHintType::class.java
        ).apply {
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
            put(
                EncodeHintType.ERROR_CORRECTION,
                ErrorCorrectionLevel.M
            )
            put(
                EncodeHintType.MARGIN,
                config.marginModules
            )
        }

        val bitMatrix = QRCodeWriter().encode(
            config.content,
            BarcodeFormat.QR_CODE,
            config.imageSizePx,
            config.imageSizePx,
            hints
        )

        val pixels = IntArray(
            config.imageSizePx * config.imageSizePx
        )

        for (y in 0 until config.imageSizePx) {
            val rowOffset = y * config.imageSizePx

            for (x in 0 until config.imageSizePx) {
                pixels[rowOffset + x] = if (bitMatrix[x, y]) {
                    config.foregroundColor
                } else {
                    config.backgroundColor
                }
            }
        }

        createBitmap(config.imageSizePx, config.imageSizePx).apply {
            setPixels(
                pixels,
                0,
                config.imageSizePx,
                0,
                0,
                config.imageSizePx,
                config.imageSizePx
            )
        }
    }
}

@Composable
fun QrCodeImage(
    content: String,
    modifier: Modifier = Modifier.size(240.dp),
    imageSizePx: Int = 1024,
    foregroundColor: Int = Color.Black.toColorLong().toInt(),
    backgroundColor: Int = Color.White.toColorLong().toInt(),
    marginModules: Int = 4,
    contentDescription: String = "Código QR"
) {
    val config = QrCodeConfig(
        content = content,
        imageSizePx = imageSizePx,
        foregroundColor = foregroundColor,
        backgroundColor = backgroundColor,
        marginModules = marginModules
    )

    val qrCodeResult by produceState<Result<Bitmap>?>(
        initialValue = null,
        key1 = config
    ) {
        value = withContext(Dispatchers.Default) {
            generateQrCodeBitmap(config)
        }
    }

    val bitmap = qrCodeResult?.getOrNull()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when {
            qrCodeResult == null -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp)
                )
            }

            bitmap != null -> {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = contentDescription,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Fit
                )
            }

            else -> {
                Text(
                    text = "No se pudo generar el código QR."
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun QrCodeImagePreview() {
    QrCodeImage(
        content = "https://flotal.mx"
    )
}