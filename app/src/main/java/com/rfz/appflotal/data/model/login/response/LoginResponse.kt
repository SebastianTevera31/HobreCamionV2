package com.rfz.appflotal.data.model.login.response

import com.google.gson.annotations.SerializedName
import com.rfz.appflotal.data.model.flotalSoft.AppHCEntity
import javax.inject.Inject
import javax.inject.Singleton


sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val userData: LoginResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}

data class LoginResponse(
    @SerializedName("id_user") val id_user: Int,
    @SerializedName("fld_name") val fld_name: String,
    @SerializedName("fld_username") val fld_username: String,
    @SerializedName("fld_email") val fld_email: String,
    @SerializedName("fld_password") val fld_password: String,
    @SerializedName("fld_token") val fld_token: String,
    @SerializedName("id") val id: Int,
    @SerializedName("id_monitor") val id_monitor: Int,
    @SerializedName("fld_mac") val fldMac: String,
    @SerializedName("id_configuration") val id_configuracion: Int,
    @SerializedName("paymentPlan") val paymentPlan: String,
    var fecha: String? = null
)


data class LoginSuccessResponse(
    @SerializedName("id_user") val idUser: Int,
    @SerializedName("fld_name") val name: String,
    @SerializedName("fld_username") val username: String,
    @SerializedName("fld_email") val email: String,
    @SerializedName("fld_token") val token: String,
    @SerializedName("id") val statusCode: Int,
    @SerializedName("fld_active") val isActive: Boolean
)


data class LoginErrorResponse(
    @SerializedName("mensaje") val message: ErrorMessage
)

data class ErrorMessage(
    @SerializedName("name") val errorName: String,
    @SerializedName("value") val errorValue: String,
    @SerializedName("resourceNotFound") val resourceNotFound: Boolean,
    @SerializedName("searchedLocation") val searchedLocation: String
)


@Singleton
class AppFlotalMapper @Inject constructor() {
    fun fromLoginResponseToEntity(response: LoginResponse): AppHCEntity {
        return AppHCEntity(
            id = 0,
            id_user = response.id_user,
            fld_name = response.fld_name ?: "",
            fld_email = response.fld_email ?: "",
            fld_token = response.fld_token ?: "",
            id_monitor = response.id_monitor,
            id_configuration = response.id_configuracion,
            monitorMac = response.fldMac,
            paymentPlan = response.paymentPlan,
            fecha = response.fecha ?: ""
        )
    }
}


