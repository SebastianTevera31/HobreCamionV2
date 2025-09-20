package com.rfz.appflotal.data.model.imperfectpair

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("message")
    val message: String
)
