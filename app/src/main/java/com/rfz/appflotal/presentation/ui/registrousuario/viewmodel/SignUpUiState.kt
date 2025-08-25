package com.rfz.appflotal.presentation.ui.registrousuario.viewmodel

data class SignUpUiState(
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val country: Pair<Int, String>? = null,
    val sector: Pair<Int, String>? = null,
    val vehicleType: String = "",
    val plates: String = "",
    val countries: Map<Int, String> = emptyMap(),
    val sectors: Map<Int, String> = emptyMap()
)
