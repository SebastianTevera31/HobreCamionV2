package com.rfz.appflotal.presentation.ui.updateuserscreen.viewmodel

import com.rfz.appflotal.data.model.database.AppHCEntity
import com.rfz.appflotal.data.repository.UnidadOdometro
import com.rfz.appflotal.data.repository.UnidadPresion
import com.rfz.appflotal.data.repository.UnidadTemperatura
import kotlin.String

data class UpdateUserUiState(
    val countries: Map<Int, String> = emptyMap(),
    val industries: Map<Int, String> = emptyMap(),
    val userData: UserData = UserData(),
    val newData: UserData = UserData(),
    val isNewData: Boolean = false
)

data class UserData(
    val idUser: Int = -1,
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val country: Pair<Int, String>? = null,
    val industry: Pair<Int, String>? = null,
    val typeVehicle: String = "",
    val plates: String = "",
    val temperatureUnit: UnidadTemperatura = UnidadTemperatura.CELCIUS,
    val pressureUnit: UnidadPresion = UnidadPresion.PSI,
    val odometerUnit: UnidadOdometro = UnidadOdometro.KILOMETROS
)

fun AppHCEntity.toUserData(): UserData {
    return UserData(
        idUser = idUser,
        name = fld_name,
        username = fld_username,
        email = fld_email,
        plates = vehiclePlates,
        typeVehicle = vehicleType
    )
}