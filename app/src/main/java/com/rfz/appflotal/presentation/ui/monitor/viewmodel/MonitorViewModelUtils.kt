package com.rfz.appflotal.presentation.ui.monitor.viewmodel

import com.rfz.appflotal.core.network.NetworkConfig.BASE_URL
import com.rfz.appflotal.data.model.tpms.PositionCoordinatesResponse

internal fun getImageDimens(url: String): Pair<Int, Int> {
    val resourcer = url.replace(BASE_URL, "")
    return when (resourcer) {
        "Base6.png" -> Pair(620, 327)
        "Base10.png" -> Pair(628, 327)
        "Base22.png" -> Pair(1280, 425)
        "Base32.png" -> Pair(1780, 327)
        else -> Pair(0, 0)
    }
}

internal fun filterCoordinatesByLabels(
    coordinates: List<PositionCoordinatesResponse>,
    labels: Collection<String>
): List<PositionCoordinatesResponse> {
    val labelSet = labels.map { it.trim() }.toHashSet()
    return coordinates.filter { it.position.trim() in labelSet }
        .sortedBy { it.position.removePrefix("P").trim().toIntOrNull() ?: Int.MAX_VALUE }
}

internal fun getAlertType(highTemperature: Boolean): SensorAlerts {
    return if (highTemperature) SensorAlerts.HIGH_TEMPERATURE
    else SensorAlerts.NO_DATA
}

internal fun getPressureAlert(lowPressure: Boolean, highPressure: Boolean): SensorAlerts {
    return if (lowPressure) SensorAlerts.LOW_PRESSURE
    else if (highPressure) SensorAlerts.HIGH_PRESSURE
    else SensorAlerts.NO_DATA
}

internal fun getBatteryAlert(lowBattery: Boolean): SensorAlerts {
    return if (lowBattery) SensorAlerts.LOW_BATTERY else SensorAlerts.NO_DATA
}

internal fun getIsTireInAlert(tempAlert: SensorAlerts, pressureAlert: SensorAlerts): Boolean {
    return tempAlert != SensorAlerts.NO_DATA || pressureAlert != SensorAlerts.NO_DATA
}