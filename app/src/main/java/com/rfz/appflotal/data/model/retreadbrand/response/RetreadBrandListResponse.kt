package com.rfz.appflotal.data.model.retreadbrand.response

import com.google.gson.annotations.SerializedName
import com.rfz.appflotal.domain.CatalogItem

data class RetreadBrandListResponse(

    @SerializedName("id_retreadBrand")
    val idRetreadBrand: Int,

    @SerializedName("fld_description")
    val description: String
) {
    fun toDomain(): CatalogItem {
        return CatalogItem(
            id = idRetreadBrand,
            description = description
        )
    }
}
