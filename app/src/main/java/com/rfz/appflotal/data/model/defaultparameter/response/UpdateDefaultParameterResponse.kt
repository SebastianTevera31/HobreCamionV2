package com.rfz.appflotal.data.model.defaultparameter.response

import com.google.gson.annotations.SerializedName

data class UpdateDefaultParameterResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("message")
    val message: String
)
