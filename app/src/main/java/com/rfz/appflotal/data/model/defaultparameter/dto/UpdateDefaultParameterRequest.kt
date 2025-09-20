package com.rfz.appflotal.data.model.defaultparameter.dto

import com.google.gson.annotations.SerializedName

data class UpdateDefaultParameterRequest(
    @SerializedName("id_parameter")
    val idParameter: Int,

    @SerializedName("fld_currentValue")
    val currentValue: Int
)
