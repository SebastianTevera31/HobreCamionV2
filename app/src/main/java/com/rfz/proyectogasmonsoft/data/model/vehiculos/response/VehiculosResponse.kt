package com.rfz.proyectogasmonsoft.data.model.vehiculos.response

import com.google.gson.annotations.SerializedName

data class VehiculosResponse(

    @SerializedName("id_vehiculo") var id_vehiculo: Int,
    @SerializedName("fld_noEconomico") var fld_noEconomico: String,
    @SerializedName("fld_placas") var fld_placas: String,
    @SerializedName("fld_serie") var fld_serie: String
)
