package com.rfz.appflotal.data.model.tire.response

import com.google.gson.annotations.SerializedName

data class TirexIdResponse (
    @SerializedName("id_tire") val idTire: Int,
    @SerializedName("c_typeAcquisition_fk_1") val typeAcquisitionId: Int,
    @SerializedName("c_provider_fk_3") val providerId: Int,
    @SerializedName("fld_acquisitionDate") val acquisitionDate: String,
    @SerializedName("fld_registrationDate") val registrationDate: String,
    @SerializedName("fld_document") val document: String,
    @SerializedName("fld_treadDepth") val treadDepth: Int,
    @SerializedName("fld_unitCost") val unitCost: Int,
    @SerializedName("fld_tireNumber") val tireNumber: String,
    @SerializedName("c_user_fk_5") val userId: Int,
    @SerializedName("c_product_fk_6") val productId: Int,
    @SerializedName("fld_dot") val dot: String,
    @SerializedName("fld_active") val isActive: Boolean,
    @SerializedName("c_retreadDesign_fk_2") val retreadDesignId: Int,
    @SerializedName("c_destination_fk_8") val destinationId: Int,
    @SerializedName("fld_lifecycle") val lifecycle: Int
)