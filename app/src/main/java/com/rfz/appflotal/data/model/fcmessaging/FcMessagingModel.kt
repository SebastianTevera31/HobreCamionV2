package com.rfz.appflotal.data.model.fcmessaging

import kotlinx.serialization.Serializable

@Serializable
data class AppUpdateMessage(
    val tipo: String,
    val fecha: String,
    val horaInicio: String,
    val horaFinal: String,
)