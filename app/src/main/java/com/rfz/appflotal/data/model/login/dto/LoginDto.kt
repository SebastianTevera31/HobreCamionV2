package com.rfz.appflotal.data.model.login.dto

import com.google.gson.annotations.SerializedName

data class LoginDto(
    @SerializedName("fld_password") var fld_password: String,
    @SerializedName("fld_usuario") var fld_usuario: String,

)