package com.rfz.appflotal.data.model.assembly

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class AssemblyTireDto(
    @SerializedName("id_axle") val idAxle: Int,
    @SerializedName("id_tire") val idTire: Int,
    @SerializedName("id_monitor") val idMonitor: Int,
    @SerializedName("positionTire") val positionTire: String,
    @SerializedName("fld_odometer") val odometer: Int,
    @SerializedName("fld_assemblyDate") val assemblyDate: String,
)

data class AssemblyTireResponse(
    @SerializedName("axleID") val axleId: Int,
    @SerializedName("tireID") val tireId: Int,
    @SerializedName("odometer") val odometer: Int,
    @SerializedName("assemblyDate") val assemblyDate: String,
    @SerializedName("tirePosition") val positionTire: String,
)

@Entity(tableName = "assembly_tire_table")
data class AssemblyTireEntity(
    @PrimaryKey
    @ColumnInfo(name = "positionTire")
    val positionTire: String,
    @ColumnInfo(name = "id_axle")
    val idAxle: Int,
    @ColumnInfo(name = "id_tire")
    val idTire: Int,
    @ColumnInfo(name = "id_monitor")
    val idMonitor: Int,
    @ColumnInfo(name = "fld_odometer")
    val odometer: Int,
    @ColumnInfo(name = "fld_assemblyDate")
    val assemblyDate: String,
    @ColumnInfo(name = "updatedAt")
    val updatedAt: Long
)

data class AssemblyTire(
    val idAxle: Int,
    val idTire: Int,
    val positionTire: String,
    val odometer: Int,
    val assemblyDate: String,
    val updatedAt: Long
)