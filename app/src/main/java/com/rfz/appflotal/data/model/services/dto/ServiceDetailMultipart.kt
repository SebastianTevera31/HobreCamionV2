package com.rfz.appflotal.data.model.services.dto

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/** Nombre de la parte de imagen que espera api/Service/CrudService. */
const val SERVICE_IMAGE_PART = "fld_image"

/** Partes del multipart de api/Service/CrudService (mismo patrón que TicketA en FSC). */
data class ServiceDetailMultipart(
    val idService: RequestBody,
    val description: RequestBody,
    val idServiceType: RequestBody,
    val price: RequestBody,
    val date: RequestBody,
    val provider: RequestBody,
    val quantity: RequestBody,
    val vehicleId: RequestBody,
    val notes: RequestBody,
    val image: MultipartBody.Part?
)

fun ServiceDetailDto.toMultipart(image: MultipartBody.Part?): ServiceDetailMultipart =
    ServiceDetailMultipart(
        idService = idService.toTextPart(),
        description = description.toTextPart(),
        idServiceType = idServiceType.toTextPart(),
        price = java.math.BigDecimal.valueOf(price).toPlainString().toTextPart(),
        date = date.toTextPart(),
        provider = provider.toTextPart(),
        quantity = quantity.toTextPart(),
        vehicleId = vehicleId.toTextPart(),
        notes = notas.toTextPart(),
        image = image
    )

private fun Any.toTextPart(): RequestBody =
    toString().toRequestBody("text/plain".toMediaType())
