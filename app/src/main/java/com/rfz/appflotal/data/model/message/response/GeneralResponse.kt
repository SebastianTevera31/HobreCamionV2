package com.rfz.appflotal.data.model.message.response

import com.google.gson.annotations.SerializedName

data class GeneralResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("message")
    val message: String
)
