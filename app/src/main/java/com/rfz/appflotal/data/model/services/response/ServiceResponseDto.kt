package com.rfz.appflotal.data.model.services.response

import com.google.gson.annotations.SerializedName

data class ServiceResponseDto(
    @SerializedName("id_service")
    val idService: Int,

    @SerializedName("fld_name")
    val name: String
)
