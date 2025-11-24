package com.rfz.appflotal.data.model.axle

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "axle")
data class AxleEntity(
    @ColumnInfo(name = "id_axle")
    val idAxle: Int,
    @ColumnInfo(name = "fld_description")
    val fldDescription: String,
    @ColumnInfo(name = "fld_letter")
    val fldLetter: String
)