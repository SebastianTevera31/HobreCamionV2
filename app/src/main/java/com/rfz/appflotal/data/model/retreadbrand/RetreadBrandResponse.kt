package com.rfz.appflotal.data.model.retreadbrand

import com.google.gson.annotations.SerializedName

data class RetreadBrandResponse(
    @SerializedName("id_retreadBrand")
    val id: Int,

    @SerializedName("fld_description")
    val description: String
)
