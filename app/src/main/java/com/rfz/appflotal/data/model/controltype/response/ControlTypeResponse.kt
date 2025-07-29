package com.rfz.appflotal.data.model.controltype.response

import com.google.gson.annotations.SerializedName

data class ControlTypeResponse(

    @SerializedName("id_controlType")
    val idControlType: Int,

    @SerializedName("fld_description")
    val fldDescription: String
)
