package com.rfz.appflotal.data.model.alerts

import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import com.rfz.appflotal.R

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

enum class AlertType(val key: String, @StringRes val label: Int) {
    NONE("none", R.string.todas),
    HIGH_TEMPERATURE("temperature", R.string.temperatura),
    LOW_PRESSURE("lowPressure", R.string.presion_baja),
    HIGH_PRESSURE("highPressure", R.string.presion_alta),
    NO_DATA_RECEIVED("noDataReceived", R.string.sin_datos),
    LOW_SENSOR_BATERY("lowSensorBatery", R.string.bateria_baja),
    FAST_LEAK("fastLeak", R.string.fuga_rapida),
    SLOW_LEAK("slowLeak", R.string.fuga_lenta),
    EXTRACTION("extraction", R.string.en_extraccion),
    INFLATE("inflate", R.string.inflate)
}

fun AlertDto.toDomain(): Alert {
    val typeAlert = AlertType.entries.find { it.key == alert } ?: AlertType.NONE
    return Alert(
        idMonitor = idMonitor,
        position = position,
        temperature = temperature,
        psi = psi,
        alert = typeAlert,
        datedata = datedata
    )
}