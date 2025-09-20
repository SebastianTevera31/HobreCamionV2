package com.rfz.appflotal.data.model.vehicle.response

import com.google.gson.annotations.SerializedName

data class VehicleIdResponse(

    @SerializedName("id_vehicle")
    val idVehicle: Int,

    @SerializedName("c_typeVehicle_fk_1")
    val typeVehicleFk: Int,

    @SerializedName("fld_spareTires")
    val spareTires: Int,

    @SerializedName("c_typeControl_fk_2")
    val typeControlFk: Int,

    @SerializedName("c_route_fk_4")
    val routeFk: Int,

    @SerializedName("fld_vehicleNumber")
    val vehicleNumber: String,

    @SerializedName("fld_plates")
    val plates: String,

    @SerializedName("fld_dailyMaximumkm")
    val dailyMaximumKm: Int,

    @SerializedName("fld_odometerStartDate")
    val odometerStartDate: String,

    @SerializedName("fld_initialValueOdometer")
    val initialValueOdometer: Int,

    @SerializedName("fld_averageDailyKilometers")
    val averageDailyKilometers: Int,

    @SerializedName("c_user_fk_6")
    val userFk: Int,

    @SerializedName("fld_active")
    val active: Boolean,

    @SerializedName("fld_odometerEvent")
    val odometerEvent: Int,

    @SerializedName("fld_dateEventOdometer")
    val dateEventOdometer: String,

    @SerializedName("fld_registrationDate")
    val registrationDate: String
)
