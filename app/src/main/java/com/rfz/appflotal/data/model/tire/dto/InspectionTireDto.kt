package com.rfz.appflotal.data.model.tire.dto

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class InspectionTireDto(

    @SerializedName("positionTire")
    val positionTire: String,

    @SerializedName("fld_treadDepth")
    val treadDepth: Float,

    @SerializedName("fld_treadDepth2")
    val treadDepth2: Float,

    @SerializedName("fld_treadDepth3")
    val treadDepth3: Float,

    @SerializedName("fld_treadDepth4")
    val treadDepth4: Float,

    @SerializedName("c_tireInspectionReport_fk_4")
    val tireInspectionReportId: Int,

    @SerializedName("fld_pressureInspected")
    val pressureInspected: Int,

    @SerializedName("fld_dateInspection")
    val dateInspection: String,

    @SerializedName("fld_odometer")
    val odometer: Int,

    @SerializedName("fld_temperatureInspected")
    val temperatureInspected: Int,

    @SerializedName("fld_pressureAdjusted")
    val pressureAdjusted: Int,
)