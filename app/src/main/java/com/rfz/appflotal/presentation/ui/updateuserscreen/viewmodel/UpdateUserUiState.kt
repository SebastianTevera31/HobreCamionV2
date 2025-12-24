package com.rfz.appflotal.presentation.ui.updateuserscreen.viewmodel

import com.rfz.appflotal.data.model.database.AppHCEntity
import com.rfz.appflotal.data.repository.UnidadOdometro
import com.rfz.appflotal.data.repository.UnidadPresion
import com.rfz.appflotal.data.repository.UnidadTemperatura
import com.rfz.appflotal.data.repository.UnitProvider

data class UpdateUserUiState(
    val idVehicle: Int = 0,
    val countries: Map<Int, String> = emptyMap(),
    val industries: Map<Int, String> = emptyMap(),
    val userData: UserData = UserData(),
    val newUserData: UserData = UserData(),
    val vehicleData: VehicleData = VehicleData(),
    val newVehicleData: VehicleData = VehicleData(),
    val isNewUserData: Boolean = false,
    val isNewVehicleData: Boolean = false
)

data class VehicleData(
    val typeVehicle: String = "",
    val plates: String = "",
    val temperatureUnit: UnitProvider = UnidadTemperatura.CELCIUS,
    val pressureUnit: UnitProvider = UnidadPresion.PSI,
    val odometerUnit: UnitProvider = UnidadOdometro.KILOMETROS
)


data class UserData(
    val idUser: Int = -1,
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val country: Pair<Int, String>? = null,
    val industry: Pair<Int, String>? = null
)

fun AppHCEntity.toUserData(): UserData {
    return UserData(
        idUser = idUser,
        name = fld_name,
        username = fld_username,
        email = fld_email,
    )
}

fun AppHCEntity.toVehicleData(): VehicleData {
    return VehicleData(
        typeVehicle = vehicleType,
        plates = vehiclePlates
    )
}