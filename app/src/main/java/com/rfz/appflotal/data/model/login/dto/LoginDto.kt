package com.rfz.appflotal.data.model.login.dto

import com.google.gson.annotations.SerializedName

data class LoginDto(
    @SerializedName("fld_email") var fldEmail: String,
    @SerializedName("fld_password") var fldPassword: String,
)