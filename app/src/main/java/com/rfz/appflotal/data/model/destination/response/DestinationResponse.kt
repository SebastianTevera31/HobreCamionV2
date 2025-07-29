package com.rfz.appflotal.data.model.destination.response

import com.google.gson.annotations.SerializedName

class DestinationResponse (
    @SerializedName("id_destination")
    val idDestination: Int,

    @SerializedName("fld_description")
    val fldDescription: String
)