package com.rfz.appflotal.data.model.alerts

import com.google.gson.annotations.SerializedName

data class AlertDto(
    @SerializedName("id_monitor") val idMonitor: Int,
    @SerializedName("fld_position") val position: Int,
    @SerializedName("fld_temperature") val temperature: Int,
    @SerializedName("fld_psi") val psi: Int,
    @SerializedName("alert") val alert: Int,
    @SerializedName("fld_datedate") val datedata: Int,
)

data class Alert(
    val idMonitor: Int,
    val position: Int,
    val temperature: Int,
    val psi: Int,
    val alert: Int,
    val datedata: Int
)

fun AlertDto.toDomain() = Alert(
    idMonitor = idMonitor,
    position = position,
    temperature = temperature,
    psi = psi,
    alert = alert,
    datedata = datedata
)