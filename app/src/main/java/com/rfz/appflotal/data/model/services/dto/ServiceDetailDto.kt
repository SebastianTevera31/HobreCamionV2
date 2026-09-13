package com.rfz.appflotal.data.model.services.dto

import com.google.gson.annotations.SerializedName

data class ServiceDetailDto(
    @SerializedName("id_serviceOrderDetail")
    val idServiceOrderDetail: Int,

    @SerializedName("c_typeOfOccurrence_fk_1")
    val typeOfOccurrenceId: Int,

    @SerializedName("fld_provider")
    val provider: String,

    @SerializedName("c_service_fk_3")
    val serviceId: Int,

    @SerializedName("fld_quantity")
    val quantity: Double,

    @SerializedName("fld_unitCost")
    val unitCost: Double,

    @SerializedName("p_serviceOrder_fk_4")
    val serviceOrderId: Int,

    @SerializedName("fld_notes")
    val notes: String,

    @SerializedName("language")
    val language: String
)
