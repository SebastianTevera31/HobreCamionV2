package com.rfz.appflotal.data.network.service.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.services.dto.SERVICE_IMAGE_PART
import com.rfz.appflotal.data.model.services.dto.ServiceDetailDto
import com.rfz.appflotal.data.model.services.dto.toMultipart
import com.rfz.appflotal.data.model.services.response.ServiceResponseDto
import com.rfz.appflotal.data.model.services.response.TypeServiceDto
import com.rfz.appflotal.data.network.client.services.ServiceClient
import com.rfz.appflotal.data.network.networkRequestHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class RemoteServiceDataSource @Inject constructor(
    private val serviceClient: ServiceClient,
    @ApplicationContext private val context: Context
) {
    suspend fun getServices(token: String): Result<List<ServiceResponseDto>> {
        return networkRequestHelper {
            serviceClient.getServices("Bearer $token")
        }
    }

    suspend fun doCrudServiceDetail(
        requestBody: ServiceDetailDto,
        token: String
    ): Result<GeneralResponse> {
        // El backend exige fld_image siempre: sin imagen nueva se manda un archivo vacío.
        val image = requestBody.image?.let { imagePart(it) } ?: emptyImagePart()
        val request = requestBody.toMultipart(image = image)
        return networkRequestHelper {
            serviceClient.doCrudServiceDetail(
                token = "Bearer $token",
                idService = request.idService,
                description = request.description,
                idServiceType = request.idServiceType,
                price = request.price,
                date = request.date,
                provider = request.provider,
                quantity = request.quantity,
                vehicleId = request.vehicleId,
                notes = request.notes,
                image = request.image
            )
        }
    }

    suspend fun deleteService(
        idService: Int,
        token: String
    ): Result<List<GeneralResponse>> {
        return networkRequestHelper {
            serviceClient.deleteService("Bearer $token", idService)
        }
    }

    suspend fun doGetServiceType(
        token: String
    ): Result<List<TypeServiceDto>> {
        return networkRequestHelper {
            serviceClient.doGetServiceType("Bearer $token")
        }
    }

    /**
     * Lee la imagen elegida (cámara o galería), la reduce a [MAX_IMAGE_SIDE] px por
     * lado y la comprime a JPEG para no subir fotos de varios MB. Si no se puede
     * leer, el servicio se guarda sin imagen.
     */
    private suspend fun imagePart(uri: Uri): MultipartBody.Part? = withContext(Dispatchers.IO) {
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
                SERVICE_IMAGE_PART,
                "service_${System.currentTimeMillis()}.jpg",
                bytes.toRequestBody("image/jpeg".toMediaType())
            )
        }.getOrNull()
    }

    /**
     * Parte fld_image vacía (0 bytes). Lleva nombre de archivo porque ASP.NET ignora
     * los archivos sin nombre y sin contenido, y entonces volvería el error de requerido.
     */
    private fun emptyImagePart(): MultipartBody.Part =
        MultipartBody.Part.createFormData(
            SERVICE_IMAGE_PART,
            "empty.jpg",
            ByteArray(0).toRequestBody("application/octet-stream".toMediaType())
        )

    private companion object {
        const val MAX_IMAGE_SIDE = 1600
        const val JPEG_QUALITY = 80
    }
}
