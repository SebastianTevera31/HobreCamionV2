package com.rfz.appflotal.data.model.brand.response

import com.google.gson.annotations.SerializedName

data class BranListResponse(

    @SerializedName("id_brand")
    val idBrand: Int,

    @SerializedName("fld_description")
    val description: String
)
