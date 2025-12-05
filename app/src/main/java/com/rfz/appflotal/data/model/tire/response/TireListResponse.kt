package com.rfz.appflotal.data.model.tire.response

import com.google.gson.annotations.SerializedName

data class TireListResponse(
    @SerializedName("id_tire") val idTire: Int,
    @SerializedName("fld_provider") val provider: String,
    @SerializedName("fld_size") val size: String,
    @SerializedName("fld_brand") val brand: String,
    @SerializedName("fld_model") val model: String,
    @SerializedName("fld_loadingCapacity") val loadingCapacity: String,
    @SerializedName("fld_destination") val destination: String,
    @SerializedName("fld_typeAcquisition") val typeAcquisition: String,
    @SerializedName("fld_lastMountedPosition") val lastMountedPosition: String,
    @SerializedName("fld_descriptionLastRenovatedDesign") val descriptionLastRenovatedDesign: String,
    @SerializedName("fld_lastMountedPositionDate") val lastMountedPositionDate: String,
    @SerializedName("fld_vehicleNumber") val vehicleNumber: String,
    @SerializedName("fld_dateEventAssembly") val dateEventAssembly: String,
    @SerializedName("fld_dateEventA") val dateEventA: String,
    @SerializedName("fld_treadDepthAssembly") val treadDepthAssembly: Double,
    @SerializedName("fld_odometerAssembly") val odometerAssembly: Int,
    @SerializedName("fld_typeVehicle") val typeVehicle: String
)
