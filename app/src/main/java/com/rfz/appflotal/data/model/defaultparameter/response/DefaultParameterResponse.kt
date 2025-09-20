package com.rfz.appflotal.data.model.defaultparameter.response

import com.google.gson.annotations.SerializedName

data class DefaultParameterResponse(

    @SerializedName("id_parameter")
    val idParameter: Int,

    @SerializedName("fld_description")
    val description: String,

    @SerializedName("fld_currentValue")
    val currentValue: Int,

    @SerializedName("fld_previousValue")
    val previousValue: Float,

    @SerializedName("fld_notes")
    val notes: String,


    @SerializedName("fld_active")
    val isActive: Boolean
)
