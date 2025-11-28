package com.rfz.appflotal.data.model.disassembly.tire

import com.google.gson.annotations.SerializedName

data class DisassemblyTireRequestDto(
    @SerializedName("c_disassemblyCauses_fk_1") val disassemblyCause: Int,
    @SerializedName("c_destination_fk_3") val destination: Int,
    @SerializedName("fld_dateOperation") val dateOperation: String,
    @SerializedName("positionTire") val positionTire: String,
    @SerializedName("fld_odometer") val odometer: Int
)

data class DisassemblyTire(
    val disassemblyCause: Int,
    val destination: Int,
    val dateOperation: String,
    val positionTire: String,
    val odometer: Int
)