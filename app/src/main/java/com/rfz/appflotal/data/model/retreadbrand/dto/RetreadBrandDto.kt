package com.rfz.appflotal.data.model.retreadbrand.dto

import com.google.gson.annotations.SerializedName

data class RetreadBrandDto(
    @SerializedName("id_retreadBrand")
    val idRetreadBrand: Int,

    @SerializedName("fld_description")
    val description: String
)