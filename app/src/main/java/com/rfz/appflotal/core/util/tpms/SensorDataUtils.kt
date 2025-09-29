package com.rfz.appflotal.core.util.tpms

import androidx.core.text.isDigitsOnly
import com.rfz.appflotal.core.util.Positions.findOutPosition
import com.rfz.appflotal.data.repository.bluetooth.MonitorDataFrame
import com.rfz.appflotal.data.repository.bluetooth.SensorAlertDataFrame
import com.rfz.appflotal.data.repository.bluetooth.decodeAlertDataFrame
import com.rfz.appflotal.data.repository.bluetooth.decodeDataFrame
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.SensorAlerts
import kotlin.math.roundToInt

fun getTire(dataFrame: String): String {
    val tire = decodeDataFrame(dataFrame, MonitorDataFrame.POSITION_WHEEL).toInt()
    return findOutPosition("P${tire}")
}

fun getPressure(dataFrame: String): Float {
    val data = decodeDataFrame(dataFrame, MonitorDataFrame.PRESSION)
    return  (data.toFloat() * 100).roundToInt() / 100f
}

fun getTemperature(dataFrame: String): Float {
    val data = decodeDataFrame(dataFrame, MonitorDataFrame.TEMPERATURE)
    return if (data.isDigitsOnly()) data.toFloat() else 0f
}

fun getHighTemperatureStatus(dataFrame: String): Boolean {
    val data = decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.HIGH_TEMPERATURE)
    return data == SensorAlerts.HIGH_TEMPERATURE
}

fun getLowPressureStatus(dataFrame: String): Boolean {
    val data = decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.PRESSURE)
    return data == SensorAlerts.LOW_PRESSURE
}

fun getHighPressureStatus(dataFrame: String): Boolean {
    val data = decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.PRESSURE)
    return data == SensorAlerts.HIGH_PRESSURE
}

fun getBatteryStatus(dataFrame: String): Boolean {
    val data = decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.LOW_BATTERY)
    return data == SensorAlerts.LOW_BATTERY
}
