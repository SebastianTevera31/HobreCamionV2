package com.rfz.appflotal.data.model.alerts

import com.google.gson.annotations.SerializedName

data class AlertDto(
    @SerializedName("id_monitor") val idMonitor: Int,
    @SerializedName("fld_position") val position: String,
    @SerializedName("fld_temperature") val temperature: Int,
    @SerializedName("fld_psi") val psi: Double,
    @SerializedName("alert") val alert: String,
    @SerializedName("fld_datedata") val datedata: String,
)

data class Alert(
    val idMonitor: Int,
    val position: String,
    val temperature: Int,
    val psi: Double,
    val alert: AlertType,
    val datedata: String
)

enum class AlertType(val label: String) {
    NONE("none"),
    TEMPERATURE("temperature"),
    PRESSURE("pressure"),
    INFLATE("inflate")
}

fun AlertDto.toDomain(): Alert {
    val typeAlert = AlertType.entries.find { it.label == alert } ?: AlertType.NONE
    return Alert(
        idMonitor = idMonitor,
        position = position,
        temperature = temperature,
        psi = psi,
        alert = typeAlert,
        datedata = datedata
    )
}