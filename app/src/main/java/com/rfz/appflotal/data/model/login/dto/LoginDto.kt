package com.rfz.appflotal.data.model.login.dto

import com.google.gson.annotations.SerializedName

data class LoginDto(
    @SerializedName("fld_usuario") var fldUsuario: String,
    @SerializedName("fld_password") var fldPassword: String,
    @SerializedName("fld_Token") var fldFscToken: String,
)