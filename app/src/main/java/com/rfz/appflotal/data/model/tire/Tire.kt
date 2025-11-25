package com.rfz.appflotal.data.model.tire

import com.rfz.appflotal.data.model.tire.response.TireListResponse

data class Tire(
    val idTire: Int,
    val typeAcquisition: String,
    val brand: String,
    val size: String
)

fun TireListResponse.toTire(): Tire {
    return Tire(
        idTire = idTire,
        typeAcquisition = typeAcquisition,
        brand = brand,
        size = size
    )
}