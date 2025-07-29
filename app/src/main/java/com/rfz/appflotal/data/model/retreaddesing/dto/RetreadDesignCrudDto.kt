package com.rfz.appflotal.data.model.retreaddesing.dto

import com.google.gson.annotations.SerializedName

data class RetreadDesignCrudDto(
    @SerializedName("id_retreadDesign")
    val idRetreadDesign: Int,

    @SerializedName("fld_description")
    val description: String,

    @SerializedName("retreadBrand_fk_1")
    val retreadBrandId: Int,

    @SerializedName("c_utilization_fk_2")
    val utilizationId: Int,

    @SerializedName("fld_treadDepth")
    val treadDepth: Int
)
