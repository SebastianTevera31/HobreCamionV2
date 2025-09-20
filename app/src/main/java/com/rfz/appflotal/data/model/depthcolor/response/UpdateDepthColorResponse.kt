package com.rfz.appflotal.data.model.depthcolor.response

import com.google.gson.annotations.SerializedName

data class UpdateDepthColorResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("message")
    val message: String
)
