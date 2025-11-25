package com.rfz.appflotal.data.model.axle

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.rfz.appflotal.data.model.commons.Catalog

data class GetAxleResponseDto(
    @SerializedName("id_axle") val idAxle: Int,
    @SerializedName("fld_description") val fldDescription: String,
    @SerializedName("fld_letter") val fldLetter: String
)

@Entity(tableName = "axle_table")
data class AxleEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_axle")
    val idAxle: Int,
    @ColumnInfo(name = "fld_description")
    val fldDescription: String,
    @ColumnInfo(name = "fld_letter")
    val fldLetter: String
)

data class Axle(
    override val id: Int,
    override val description: String,
) : Catalog