package com.rfz.appflotal.data.model.tire.dto

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class InspectionTireDto (
    @SerializedName("c_user_fk_7")
    val userId: Int,

    @SerializedName("p_tire_fk_2")
    val tireId: Int,

    @SerializedName("fld_treadDepth")
    val treadDepth: Int,

    @SerializedName("fld_treadDepth2")
    val treadDepth2: Int,

    @SerializedName("fld_treadDepth3")
    val treadDepth3: Int,

    @SerializedName("fld_treadDepth4")
    val treadDepth4: Int,

    @SerializedName("fld_dateOperation")
    val dateOperation: LocalDateTime,

    @SerializedName("c_tireInspectionReport_fk_4")
    val tireInspectionReportId: Int,

    @SerializedName("fld_pressureInspected")
    val pressureInspected: Int,

    @SerializedName("fld_dateInspection")
    val dateInspection: LocalDateTime,

    @SerializedName("c_position_fk_5")
    val positionId: Int,

    @SerializedName("p_vehicle_fk_6")
    val vehicleId: Int,

    @SerializedName("fld_odometer")
    val odometer: Int,

    @SerializedName("fld_lifeCycle")
    val lifeCycle: Int,

    @SerializedName("fld_letterAxis")
    val letterAxis: String,

    @SerializedName("fld_pressureAdjusted")
    val pressureAdjusted: Int,

    @SerializedName("p_assembly_fk_8")
    val assemblyId: Int

)