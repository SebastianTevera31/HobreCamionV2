package com.rfz.appflotal.data.model.services.response

import com.google.gson.annotations.SerializedName

data class ServiceResponseDto(
    @SerializedName("id_service") val idService: Int,
    @SerializedName("fld_typeService") val typeService: String,
    @SerializedName("fld_description") val description: String,
    @SerializedName("fld_price") val price: Int,
    @SerializedName("fld_cant") val quantity: Int,
    @SerializedName("fld_vehicleNumber") val vehicleNumber: String,
)

data class TypeServiceDto(
    @SerializedName("id_serviceType") val idTypeService: Int,
    @SerializedName("fld_serviceType") val description: Int,
)