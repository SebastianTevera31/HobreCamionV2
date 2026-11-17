package com.rfz.appflotal.data.model.services.dto

import com.rfz.appflotal.data.network.multipart.toDecimalPart
import com.rfz.appflotal.data.network.multipart.toTextPart
import okhttp3.MultipartBody
import okhttp3.RequestBody

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
    val odometer: RequestBody,
    val notes: RequestBody,
    val image: MultipartBody.Part?
)

fun ServiceDetailDto.toMultipart(image: MultipartBody.Part?): ServiceDetailMultipart =
    ServiceDetailMultipart(
        idService = idService.toTextPart(),
        description = description.toTextPart(),
        idServiceType = idServiceType.toTextPart(),
        price = price.toDecimalPart(),
        date = date.toTextPart(),
        provider = provider.toTextPart(),
        quantity = quantity.toTextPart(),
        vehicleId = vehicleId.toTextPart(),
        odometer = odometer.toTextPart(),
        notes = notas.toTextPart(),
        image = image
    )
