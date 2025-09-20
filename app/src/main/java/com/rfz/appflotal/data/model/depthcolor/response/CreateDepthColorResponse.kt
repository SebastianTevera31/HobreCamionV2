package com.rfz.appflotal.data.model.depthcolor.response

import com.google.gson.annotations.SerializedName

data class CreateDepthColorResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("message")
    val message: String
)
