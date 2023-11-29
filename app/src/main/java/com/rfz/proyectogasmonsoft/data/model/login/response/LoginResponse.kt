package com.rfz.proyectogasmonsoft.data.model.login.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("id") var id: Int,
    @SerializedName("message") var message: String,
    @SerializedName("c_tipo_usuario") var c_tipo_usuario: Int,
    @SerializedName("id_usuario") var id_usuario: Int,
    @SerializedName("fld_usuario") var fld_usuario: String,
    @SerializedName("id_empresa") var id_empresa: Int,
    @SerializedName("fld_encontrado") var fld_encontrado: Int,
    @SerializedName("fld_correo") var fld_correo: String,
    @SerializedName("token") var token: String,
    @SerializedName("id_empresaReembolso") var id_empresaReembolso: Int,
    @SerializedName("id_tipoEmpresaSoftware") var id_tipoEmpresaSoftware: Int,

    )
