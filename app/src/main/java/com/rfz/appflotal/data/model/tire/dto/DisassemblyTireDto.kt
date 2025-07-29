package com.rfz.appflotal.data.model.tire.dto

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class DisassemblyTireDto(

    @SerializedName("c_disassemblyCauses_fk_1")
    val disassemblyCauseId: Int,

    @SerializedName("c_destination_fk_3")
    val destinationId: Int,

    @SerializedName("fld_dateOperation")
    val dateOperation: LocalDateTime,

    @SerializedName("fld_treadDepth")
    val treadDepth: Int,

    @SerializedName("p_tire_fk_6")
    val tireId: Int,

    @SerializedName("c_position_fk_7")
    val positionId: Int,

    @SerializedName("p_vehicle_fk_8")
    val vehicleId: Int,

    @SerializedName("c_user_fk_8")
    val userId: Int,

    @SerializedName("fld_odometer")
    val odometer: Int,

    @SerializedName("fld_lifeCycle")
    val lifeCycle: Int,

    @SerializedName("p_assembly_fk_9")
    val assemblyId: Int,

    @SerializedName("fld_position")
    val position: String,

    @SerializedName("fld_lettersAxis")
    val lettersAxis: String,

    @SerializedName("fld_odometerEvent")
    val odometerEvent: Int,

    @SerializedName("fld_dateEvent")
    val dateEvent: LocalDateTime
)
