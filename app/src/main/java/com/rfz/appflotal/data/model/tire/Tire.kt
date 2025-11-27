package com.rfz.appflotal.data.model.tire

import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.data.model.tire.response.TireListResponse

data class Tire(
    override val id: Int,
    override val description: String,
    val size: String,
    val brand: String,
    val model: String,
    val thread: Double,
    val loadingCapacity: String
) : CatalogItem

fun TireListResponse.toTire(): Tire {
    return Tire(
        id = idTire,
        description = "$brand - size: $size",
        size = size,
        brand = brand,
        model = model,
        thread = treadDepthAssembly,
        loadingCapacity = loadingCapacity
    )
}