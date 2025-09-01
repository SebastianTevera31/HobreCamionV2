package com.rfz.appflotal.presentation.ui.updateuserscreen.viewmodel

import com.rfz.appflotal.data.model.flotalSoft.AppHCEntity
import kotlin.String

data class UpdateUserUiState(
    val countries: Map<Int, String> = emptyMap(),
    val industries: Map<Int, String> = emptyMap(),
    val userData: UserData = UserData(),
    val newData: UserData = UserData(),
    val isNewData: Boolean = false
)

data class UserData(
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val country: Pair<Int, String>? = null,
    val industry: Pair<Int, String>? = null,
    val typeVehicle: String = "",
    val plates: String = ""
)

fun AppHCEntity.toUserData(): UserData {
    return UserData(
        name = fld_name,
        username = fld_username,
        email = fld_email,
        plates = vehiclePlates
    )
}