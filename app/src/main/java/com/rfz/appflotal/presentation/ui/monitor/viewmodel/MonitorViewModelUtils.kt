package com.rfz.appflotal.presentation.ui.monitor.viewmodel

import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.database.SensorDataEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ImageConfig(val dimen: Pair<Int, Int>, val image: Int)

enum class BaseConfig(val base: Int) {
    BASE6(6), BASE10(10), BASE22(22), BASE38(38)
}

fun getImageDimens(resourcer: BaseConfig?): Pair<Int, Int> {
    return when (resourcer) {
        BaseConfig.BASE6 -> Pair(620, 327)
        BaseConfig.BASE10 -> Pair(628, 327)
        BaseConfig.BASE22 -> Pair(1280, 425)
        BaseConfig.BASE38 -> Pair(1780, 327)
        null -> Pair(0, 0)
    }
}

fun getImageConfig(baseConfig: BaseConfig): ImageConfig {
    return when (baseConfig) {
        BaseConfig.BASE6 -> ImageConfig(Pair(620, 327), R.drawable.base6)
        BaseConfig.BASE10 -> ImageConfig(Pair(628, 327), R.drawable.base22)
        BaseConfig.BASE22 -> ImageConfig(Pair(1280, 425), R.drawable.base22)
        BaseConfig.BASE38 -> ImageConfig(Pair(1780, 327), R.drawable.base32)
    }
}

fun getIsTireInAlert(
    temperatureStatus: SensorAlerts,
    pressureStatus: SensorAlerts,
    batteryStatus: SensorAlerts,
    flatTireStatus: SensorAlerts
): Boolean {
    return temperatureStatus != SensorAlerts.NO_DATA
            || pressureStatus != SensorAlerts.NO_DATA
            || batteryStatus != SensorAlerts.NO_DATA
            || flatTireStatus != SensorAlerts.NO_DATA
}

fun getIsTireInAlertByApi(
    highTemperatureStatus: Boolean?,
    highPressureStatus: Boolean?,
    lowPressureStatus: Boolean?,
    batteryStatus: Boolean?,
    flatTireStatus: Boolean?
): Boolean {
    return highTemperatureStatus == true
            || highPressureStatus == true
            || lowPressureStatus == true
            || batteryStatus == true
            || flatTireStatus == true
}

fun getBaseConfigImage(baseConfig: Int): BaseConfig {
    return when (baseConfig) {
        6 -> BaseConfig.BASE6
        10 -> BaseConfig.BASE10
        22 -> BaseConfig.BASE22
        else -> BaseConfig.BASE38
    }
}

suspend fun updateTiresStatus(
    listTires: List<MonitorTire>,
    onGetSensorData: suspend () -> List<SensorDataEntity>,
): List<MonitorTire> = withContext(Dispatchers.IO) {
    val sensorData = onGetSensorData()

    val activeTire = sensorData.associate { it.tire to it.active }

    listTires.toMutableList().filter { tire -> activeTire[tire.sensorPosition] == true }
}

fun updateTireState(currentTire: String, tires: List<MonitorTire>, onUpdate: (tire: MonitorTire) -> Unit) {
    if (currentTire.isEmpty()) return
    tires.find { it.sensorPosition == currentTire }.let { tire ->
        if (tire?.isActive == false) {
            onUpdate(tire)
        }
    }
}
