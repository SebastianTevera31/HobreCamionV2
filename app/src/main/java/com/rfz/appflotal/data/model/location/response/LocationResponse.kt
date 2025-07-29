package com.rfz.appflotal.data.model.location.response

import com.google.gson.annotations.SerializedName

data class LocationResponse(

    @SerializedName("id") var id: Int,
    @SerializedName("message")var message: String,
)
