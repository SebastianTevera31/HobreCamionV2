package com.rfz.appflotal.data.model.retreadbrand.response

import com.google.gson.annotations.SerializedName
import com.rfz.appflotal.domain.Catalog

data class RetreadBrandListResponse(

    @SerializedName("id_retreadBrand")
    val idRetreadBrand: Int,

    @SerializedName("fld_description")
    val description: String
) {
    fun toDomain(): Catalog {
        return Catalog(
            id = idRetreadBrand,
            description = description
        )
    }
}
