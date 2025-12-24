package com.rfz.appflotal.data.model.vehicle

import com.google.gson.annotations.SerializedName

data class UpdateVehicle(
    val vehicleId: Int,
    val typeVehicle: String,
    val spareTires: Int,
    val vehicleNumber: String,
    val plates: String,
    val dailyMaximumDistance: Int,
    val averageDailyDistances: Int
)

data class UpdateVehicleDto(

    @SerializedName("id_vehicle")
    val idVehicle: Int,

    @SerializedName("fld_typeVehicle")
    val typeVehicle: String,

    @SerializedName("fld_spareTires")
    val spareTires: Int,

    @SerializedName("fld_vehicleNumber")
    val vehicleNumber: String,

    @SerializedName("fld_plates")
    val plates: String,

    @SerializedName("fld_dailyMaximumDistance")
    val dailyMaximumDistance: Int,

    @SerializedName("fld_averageDailyDistance")
    val averageDailyDistance: Int
)