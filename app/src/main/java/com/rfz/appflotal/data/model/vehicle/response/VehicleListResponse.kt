package com.rfz.appflotal.data.model.vehicle.response

import com.google.gson.annotations.SerializedName

data class VehicleListResponse(

    @SerializedName("id_vehicle")
    val idVehicle: Int,

    @SerializedName("fld_vehicleNumber")
    val fldVehicleNumber: String,

    @SerializedName("fld_plates")
    val fldPlates: String
)
