package com.rfz.appflotal.data.model.tire.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
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

@Entity(tableName = "inspection_tire_table")
data class InspectionTireEntity(
    @PrimaryKey
    val positionTire: String,
    val treadDepth: Float,
    val treadDepth2: Float,
    val treadDepth3: Float,
    val treadDepth4: Float,
    val tireInspectionReportId: Int,
    val pressureInspected: Int,
    val dateInspection: String,
    val odometer: Int,
    val temperatureInspected: Int,
    val pressureAdjusted: Int,
    val updatedAt: Long
)

fun InspectionTireEntity.toDto(): InspectionTireDto = InspectionTireDto(
    positionTire = positionTire,
    treadDepth = treadDepth,
    treadDepth2 = treadDepth2,
    treadDepth3 = treadDepth3,
    treadDepth4 = treadDepth4,
    tireInspectionReportId = tireInspectionReportId,
    pressureInspected = pressureInspected,
    dateInspection = dateInspection,
    odometer = odometer,
    temperatureInspected = temperatureInspected,
    pressureAdjusted = pressureAdjusted
)

fun InspectionTireDto.toEntity(updatedAt: Long): InspectionTireEntity = InspectionTireEntity(
    positionTire = positionTire,
    treadDepth = treadDepth,
    treadDepth2 = treadDepth2,
    treadDepth3 = treadDepth3,
    treadDepth4 = treadDepth4,
    tireInspectionReportId = tireInspectionReportId,
    pressureInspected = pressureInspected,
    dateInspection = dateInspection,
    odometer = odometer,
    temperatureInspected = temperatureInspected,
    pressureAdjusted = pressureAdjusted,
    updatedAt = updatedAt
)