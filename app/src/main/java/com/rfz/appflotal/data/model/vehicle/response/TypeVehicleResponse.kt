package com.rfz.appflotal.data.model.vehicle.response

import com.google.gson.annotations.SerializedName

class TypeVehicleResponse (
    @SerializedName("id_typeVehicle")
    val idTypeVehicle: Int,

    @SerializedName("fld_typeVehicle")
    val typeVehicle: String,

    @SerializedName("fld_description")
    val description: String
)