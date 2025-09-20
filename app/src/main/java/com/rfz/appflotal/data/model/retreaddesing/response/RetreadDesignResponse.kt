package com.rfz.appflotal.data.model.retreaddesing.response

import com.google.gson.annotations.SerializedName

data class RetreadDesignResponse(
    @SerializedName("id_retreadDesign")
    val id: Int,

    @SerializedName("fld_description")
    val description: String,

    @SerializedName("fld_retreadBrand")
    val retreadBrand: String,

    @SerializedName("fld_utilization")
    val utilization: String,

    @SerializedName("fld_treadDepth")
    val treadDepth: Int,

    @SerializedName("c_user_fk_1")
    val userId: Int
)
