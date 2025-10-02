package com.rfz.appflotal.presentation.ui.monitor.viewmodel

import com.rfz.appflotal.core.network.NetworkConfig.BASE_URL
import com.rfz.appflotal.data.model.database.SensorDataEntity
import com.rfz.appflotal.data.model.tpms.DiagramMonitorResponse
import com.rfz.appflotal.data.model.tpms.MonitorTireByDateResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


enum class BaseConfig(val base: Int) {
    BASE6(6), BASE10(10), BASE22(22), BASE38(38)
}

data class ImageConfig(val dimen: Pair<Int, Int>, val image: Int)

fun getImageDimens(resourcer: BaseConfig?): Pair<Int, Int> {
    return when (resourcer) {
        BaseConfig.BASE6 -> Pair(620, 327)
        BaseConfig.BASE10 -> Pair(628, 327)
        BaseConfig.BASE22 -> Pair(1280, 425)
        BaseConfig.BASE38 -> Pair(1780, 327)
        null -> Pair(0, 0)
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
    return if (lowBattery) SensorAlerts.LOW_BATTERY else SensorAlerts.NO_DATA
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

fun getBaseConfigImage(baseConfig: Int): BaseConfig {
    return when (baseConfig) {
        6 -> BaseConfig.BASE6
        10 -> BaseConfig.BASE10
        22 -> BaseConfig.BASE22
        else -> BaseConfig.BASE38
    }
}

suspend fun updateTireStatus(
    listTires: List<Tire>,
    onGetData: suspend () -> List<SensorDataEntity>,
): List<Tire> = withContext(Dispatchers.IO) {
    val data = onGetData()
    val activeTire = data.associate { it.tire to it.active }

    listTires.toMutableList().map { tire ->
        val activeStatus = activeTire[tire.sensorPosition]
        if (activeStatus == false)
            tire.copy(inAlert = false)
        else tire
    }
}
