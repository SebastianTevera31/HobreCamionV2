package com.rfz.appflotal.data.model.provider.dto

import com.google.gson.annotations.SerializedName

data class ProviderDto(

    @SerializedName("id_provider")
    val idProvider: Int,

    @SerializedName("fld_description")
    val description: String,

    @SerializedName("c_typeProvider_fk_2")
    val typeProviderFk: Int
)
