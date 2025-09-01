package com.rfz.appflotal.data.model.forms

data class ProfileFormModel(
    val name: String = "",
    val password: String = "",
    val email: String = "",
    val country: Pair<Int, String>? = null,
    val industry: Pair<Int, String>? = null,
)

data class VehicleFormModel(
    val vehicleType: String = "",
    val plates: String = ""
)