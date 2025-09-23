package com.rfz.appflotal.presentation.ui.monitor.viewmodel

import com.rfz.appflotal.core.network.NetworkConfig.BASE_URL

fun getImageDimens(url: String): Pair<Int, Int> {
    val resourcer = url.replace(BASE_URL, "")
    return when (resourcer) {
        "Base6.png" -> Pair(620, 327)
        "Base10.png" -> Pair(628, 327)
        "Base22.png" -> Pair(1280, 425)
        "Base32.png" -> Pair(1780, 327)
        else -> Pair(0, 0)
    }
}

fun getAlertType(highTemperature: Boolean): SensorAlerts {
    return if (highTemperature) SensorAlerts.HIGH_TEMPERATURE
    else SensorAlerts.NO_DATA
}

fun getPressureAlert(lowPressure: Boolean, highPressure: Boolean): SensorAlerts {
    return if (lowPressure) SensorAlerts.LOW_PRESSURE
    else if (highPressure) SensorAlerts.HIGH_PRESSURE
    else SensorAlerts.NO_DATA
}

fun getBatteryAlert(lowBattery: Boolean): SensorAlerts {
    return if (!lowBattery) SensorAlerts.LOW_BATTERY else SensorAlerts.NO_DATA
}

fun getIsTireInAlert(
    tempAlert: SensorAlerts,
    pressureAlert: SensorAlerts,
    batteryAlert: SensorAlerts
): Boolean {
    return tempAlert != SensorAlerts.NO_DATA
            || pressureAlert != SensorAlerts.NO_DATA
            || batteryAlert != SensorAlerts.NO_DATA
}