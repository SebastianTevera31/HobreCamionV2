package com.rfz.appflotal.data.model.tire.dto

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class TireCrudDto(

    @SerializedName("id_tire")
    val idTire: Int,

    @SerializedName("c_typeAcquisition_fk_1")
    val typeAcquisitionId: Int,

    @SerializedName("fld_acquisitionDate")
    val acquisitionDate: String,

    @SerializedName("fld_registrationDate")
    val registrationDate: String,

    @SerializedName("fld_document")
    val document: String,

    @SerializedName("fld_treadDepth")
    val treadDepth: Int,

    @SerializedName("fld_unitCost")
    val unitCost: Int,

    @SerializedName("fld_tireNumber")
    val tireNumber: String,

    @SerializedName("c_product_fk_6")
    val productId: Int,

    @SerializedName("fld_dot")
    val dot: String,

    @SerializedName("fld_active")
    val isActive: Boolean,

    @SerializedName("c_retreadDesign_fk_2")
    val retreadDesignId: Int,

    @SerializedName("c_destination_fk_8")
    val destination: Int,

    @SerializedName("fld_lifecycle")
    val lifecycle: Int
)