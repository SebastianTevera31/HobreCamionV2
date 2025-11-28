package com.rfz.appflotal.data.model.destination

import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.data.model.destination.response.DestinationResponse

data class Destination(
    override val id: Int, override val description: String
) : CatalogItem

fun DestinationResponse.toDomain(): Destination {
    return Destination(
        id = idDestination, description = fldDescription
    )
}



