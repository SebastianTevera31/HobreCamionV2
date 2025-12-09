package com.rfz.appflotal.data.model.fcmessaging

import kotlinx.serialization.Serializable

@Serializable
data class AppUpdateMessage(
    val newAppVersion: String,
    val versionLogs: String,
    val tipo: Int,
    val prioridad: Boolean,
    val fechaImplementacion: String,
    val horaInicial: String,
    val horaFinal: String
)