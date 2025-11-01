package com.rfz.appflotal.data.model.retreaddesing.response

import com.google.gson.annotations.SerializedName

data class RetreadDesignByIdResponse(
    @SerializedName("id_retreadDesign")
    val idRetreadDesign: Int,
    @SerializedName("retreatedBrand_fk_1") val retreadBrandId: Int,
    @SerializedName("c_utilization_fk_2") val utilizationId: Int,
    @SerializedName("fld_active") val active: Boolean,
)
