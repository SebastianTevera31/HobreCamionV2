package com.rfz.appflotal.data.model.provider.response

import com.google.gson.annotations.SerializedName

data class ProviderListResponse(

    @SerializedName("id_provider")
    val idProvider: Int,

    @SerializedName("fld_description")
    val description: String
)
