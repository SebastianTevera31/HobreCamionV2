package com.rfz.appflotal.data.model.depthcolor.dto

import com.google.gson.annotations.SerializedName

data class CreateDepthColorRequest(
    @SerializedName("fld_min")
    val min: Double,

    @SerializedName("fld_max")
    val max: Double,

    @SerializedName("fld_description")
    val description: String,

    @SerializedName("fld_color")
    val color: String
)
