package com.rfz.proyectogasmonsoft.data.model.estaciones.response

import com.google.gson.annotations.SerializedName

data class EstacionesReponse(
    @SerializedName("id_estacion") var id_estacion: Int,
    @SerializedName("fld_nombreCorto") var fld_nombreCorto: String,
    @SerializedName("fld_latitud") var fld_latitud: String,
    @SerializedName("fld_longitud") var fld_longitud: String
)