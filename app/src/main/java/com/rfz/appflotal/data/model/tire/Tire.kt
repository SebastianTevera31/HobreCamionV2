package com.rfz.appflotal.data.model.tire

import com.rfz.appflotal.data.model.tire.response.TireListResponse
import com.rfz.appflotal.domain.CatalogItem


fun TireListResponse.toCatalogItem(): CatalogItem {
    return CatalogItem(
        id = idTire,
        description = "$brand - size: $size",
    )
}