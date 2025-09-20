package com.rfz.appflotal.data.model.defaultparameter.dto

import com.google.gson.annotations.SerializedName

data class CreateDefaultParameterRequest(
    @SerializedName("fld_description")
    val description: String,

    @SerializedName("fld_currentValue")
    val currentValue: Int,

    @SerializedName("fld_previousValue")
    val previousValue: Int,

    @SerializedName("fld_notes")
    val notes: String
)

