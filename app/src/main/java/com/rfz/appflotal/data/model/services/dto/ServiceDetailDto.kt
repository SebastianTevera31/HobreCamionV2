package com.rfz.appflotal.data.model.services.dto

import com.google.gson.annotations.SerializedName

data class ServiceDetailDto(
    @SerializedName("id_service") val idService: Int,
    @SerializedName("fld_description") val description: String,
    @SerializedName("id_serviceType") val idServiceType: Int,
    @SerializedName("fld_price") val price: Int,
    @SerializedName("fld_date") val date: String,
    @SerializedName("fld_provider") val provider: String,
    @SerializedName("fld_cant") val quantity: Int,
    @SerializedName("p_vehicle_fk_1") val vehicleId: Int,
)
