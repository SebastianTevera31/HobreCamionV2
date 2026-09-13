package com.rfz.appflotal.data.model.services.dto

import com.google.gson.annotations.SerializedName

data class ServiceOrderDto(
    @SerializedName("id_serviceOrder")
    val idServiceOrder: Int,

    @SerializedName("id_serviceCustom")
    val idServiceCustom: String,

    @SerializedName("p_vehicle_fk_1")
    val vehicleId: Int,

    @SerializedName("fld_notes")
    val notes: String,

    @SerializedName("fld_odometer")
    val odometer: Int,

    @SerializedName("fld_registrationDate")
    val registrationDate: String
)
