package com.rfz.appflotal.data.model.lastodometer

import com.google.gson.annotations.SerializedName

data class LastOdometerResponseDto(
    @SerializedName("id_vehicle") val idVehicle: Int,
    @SerializedName("lastOdometer") val lastOdometer: Int,
    @SerializedName("dateEventOdometer") val dateOdometer: String
)

data class LastOdometer(
    val idVehicle: Int,
    val lastOdometer: Int,
    val dateOdometer: String
)
