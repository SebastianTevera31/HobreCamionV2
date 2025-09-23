package com.rfz.appflotal.data.network.service.fgservice

import com.rfz.appflotal.data.model.flotalSoft.SensorTpmsEntity
import java.util.Date

fun sensorStatusObserver(
    sensors: List<SensorTpmsEntity>?,
    currentDate: Date,
    onUpdate: (monitorId: Int, timestamp: String) -> Unit
) {
    if (!sensors.isNullOrEmpty()) {
        sensors.forEach {

        }
    }
}