package com.rfz.appflotal.data.model.login.response

import com.google.gson.annotations.SerializedName
import com.rfz.appflotal.data.model.database.AppHCEntity
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
    @SerializedName("id_country_fk_2") val idCountry: Int,
    @SerializedName("id_sector_fk_3") val idIndustry: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("id_monitor") val idMonitor: Int,
    @SerializedName("fld_mac") val fldMac: String,
    @SerializedName("baseConfiguration") val baseConfiguration: String,
    @SerializedName("id_vehicle") val idVehicle: Int,
    @SerializedName("vehicle_plates") val vehiclePlates: String,
    @SerializedName("typeVehicle") val typeVehicle: String,
    @SerializedName("paymentPlan") val paymentPlan: String,
    @SerializedName("termsAndConditions") val termsGranted: Boolean,
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

data class RegisterBody(
    @SerializedName("fld_name") val fldName: String,
    @SerializedName("fld_email") val fldEmail: String,
    @SerializedName("fld_password") val fldPassword: String,
    @SerializedName("id_country") val idCountry: Int,
    @SerializedName("id_sector") val idSector: Int,
    @SerializedName("typeVehicle") val typeVehicle: String,
    @SerializedName("plates") val plates: String,
    @SerializedName("termsAndConditions") val termsGranted: Boolean,
    @SerializedName("registerDate") val registerDate: String,
)

data class UpdateUserBody(
    @SerializedName("fld_name") val fldName: String,
    @SerializedName("fld_email") val fldEmail: String,
    @SerializedName("fld_password") val fldPassword: String,
    @SerializedName("id_country") val idCountry: Int,
    @SerializedName("id_sector") val idSector: Int,
    @SerializedName("typeVehicle") val typeVehicle: String,
    @SerializedName("plates") val plates: String,
)


@Singleton
class AppFlotalMapper @Inject constructor() {
    fun fromLoginResponseToEntity(response: LoginResponse): AppHCEntity {
        return AppHCEntity(
            id = 0,
            idUser = response.id_user,
            fld_name = response.fld_name ?: "",
            fld_username = response.fld_username,
            fld_email = response.fld_email ?: "",
            fld_token = response.fld_token ?: "",
            id_monitor = response.idMonitor,
            baseConfiguration = response.baseConfiguration,
            monitorMac = response.fldMac,
            paymentPlan = response.paymentPlan,
            fecha = response.fecha ?: "",
            idVehicle = response.idVehicle,
            vehiclePlates = response.vehiclePlates,
            country = response.idCountry,
            industry = response.idIndustry,
            vehicleType = response.typeVehicle,
            termsGranted = response.termsGranted
        )
    }
}


