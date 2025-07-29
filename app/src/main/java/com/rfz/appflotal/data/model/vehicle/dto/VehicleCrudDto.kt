package com.rfz.appflotal.data.model.vehicle.dto

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

class VehicleCrudDto (
    @SerializedName("id_vehicle")
    val idVehicle: Int,

    @SerializedName("c_typeVehicle_fk_1")
    val typeVehicleId: Int,

    @SerializedName("fld_spareTires")
    val spareTires: Int,

    @SerializedName("c_typeControl_fk_2")
    val typeControlId: Int,

    @SerializedName("c_route_fk_4")
    val routeId: Int,

    @SerializedName("fld_vehicleNumber")
    val vehicleNumber: String,

    @SerializedName("fld_plates")
    val plates: String,

    @SerializedName("fld_dailyMaximumkm")
    val dailyMaximumKm: Int,

    @SerializedName("fld_odometerStartDate")
    val odometerStartDate: String,

    @SerializedName("fld_initialValueOdometer")
    val initialOdometerValue: Int,

    @SerializedName("fld_averageDailyKilometers")
    val averageDailyKilometers: Int,

    @SerializedName("c_user_fk_6")
    val userId: Int,

    @SerializedName("fld_active")
    val isActive: Boolean,

    @SerializedName("fld_odometerEvent")
    val odometerEvent: Int,

    @SerializedName("fld_dateEventOdometer")
    val dateEventOdometer: String,

    @SerializedName("fld_registrationDate")
    val registrationDate: String
)