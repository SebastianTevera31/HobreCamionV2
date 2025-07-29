package com.rfz.appflotal.data.model.location.dto

import com.google.gson.annotations.SerializedName

data class LocationDto (

    @SerializedName("fld_latitud") var fld_latitud: String,
    @SerializedName("fld_longitud") var fld_longitud: String,
    @SerializedName("id_vehiculo") var id_vehiculo: Int,
    @SerializedName("id_usuario") var id_usuario: Int,
    @SerializedName("fld_fecha") var fld_fecha: String,
)