package com.rfz.appflotal.data.network.multipart

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/**
 * Arma las partes de imagen de los endpoints multipart (fld_image de servicios,
 * fld_imageTicket de cargas de combustible).
 */
class ImagePartFactory @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Lee la imagen elegida (cámara o galería), la reduce a [MAX_IMAGE_SIDE] px por
     * lado y la comprime a JPEG para no subir fotos de varios MB. Devuelve null si
     * no se puede leer.
     */
    suspend fun fromUri(
        partName: String,
        uri: Uri,
        filePrefix: String
    ): MultipartBody.Part? = withContext(Dispatchers.IO) {
        runCatching {
            val resolver = context.contentResolver
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }

            var sampleSize = 1
            while (maxOf(bounds.outWidth, bounds.outHeight) / (sampleSize * 2) >= MAX_IMAGE_SIDE) {
                sampleSize *= 2
            }
            val bitmap = resolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                })
            } ?: return@runCatching null

            val bytes = ByteArrayOutputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
                bitmap.recycle()
                out.toByteArray()
            }
            MultipartBody.Part.createFormData(
                partName,
                "${filePrefix}_${System.currentTimeMillis()}.jpg",
                bytes.toRequestBody("image/jpeg".toMediaType())
            )
        }.getOrNull()
    }

    /**
     * Parte de imagen vacía (0 bytes), para endpoints que exigen el campo aunque no
     * haya imagen nueva. Lleva nombre de archivo porque ASP.NET ignora los archivos
     * sin nombre y sin contenido, y entonces volvería el error de requerido.
     */
    fun empty(partName: String): MultipartBody.Part =
        MultipartBody.Part.createFormData(
            partName,
            "empty.jpg",
            ByteArray(0).toRequestBody("application/octet-stream".toMediaType())
        )

    private companion object {
        const val MAX_IMAGE_SIDE = 1600
        const val JPEG_QUALITY = 80
    }
}

/** Campo de texto de un multipart. Para Double usar [Double.toDecimalPart]. */
fun Any.toTextPart(): RequestBody =
    toString().toRequestBody("text/plain".toMediaType())

/** Decimal sin notación científica (1.0E7 -> "10000000"). */
fun Double.toDecimalPart(): RequestBody =
    java.math.BigDecimal.valueOf(this).toPlainString().toTextPart()
