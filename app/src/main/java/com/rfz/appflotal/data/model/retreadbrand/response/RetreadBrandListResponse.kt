package com.rfz.appflotal.data.model.retreadbrand.response

import com.google.gson.annotations.SerializedName
import com.rfz.appflotal.domain.retreadbrand.RetreadBrand

data class RetreadBrandListResponse(

    @SerializedName("id_retreadBrand")
    val idRetreadBrand: Int,

    @SerializedName("fld_description")
    val description: String
) {
    fun toDomain(): RetreadBrand {
        return RetreadBrand(
            id = idRetreadBrand,
            description = description
        )
    }
}
