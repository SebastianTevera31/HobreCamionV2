package com.rfz.appflotal.data.model.brand.dto

import com.google.gson.annotations.SerializedName

class BrandCrudDto (
    @SerializedName("id_brand")
    val idBrand: Int,

    @SerializedName("fld_description")
    val description: String
)