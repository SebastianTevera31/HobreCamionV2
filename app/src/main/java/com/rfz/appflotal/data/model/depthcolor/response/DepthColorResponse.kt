package com.rfz.appflotal.data.model.depthcolor.response

import com.google.gson.annotations.SerializedName

data class DepthColorResponse(
    @SerializedName("id_deepthColor")
    val idDepthColor: Int,

    @SerializedName("fld_min")
    val min: Double,

    @SerializedName("fld_max")
    val max: Double,

    @SerializedName("fld_description")
    val description: String,

    @SerializedName("fld_color")
    val color: String,

    @SerializedName("c_user_fk_1")
    val userId: Int
)
