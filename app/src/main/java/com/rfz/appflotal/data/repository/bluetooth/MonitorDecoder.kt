package com.rfz.appflotal.data.repository.bluetooth

import com.rfz.appflotal.presentation.ui.monitor.viewmodel.SensorAlerts
import java.math.MathContext
import java.math.RoundingMode

enum class MonitorDataFrame {
    SENSOR_ID,
    PRESSION,
    TEMPERATURE,
    POSITION_WHEEL,
}

enum class SensorAlertDataFrame {
    LOW_BATTERY,
    HIGH_TEMPERATURE,
    PRESSURE,
    FLAT_TIRE
}

fun decodeDataFrame(dataFrame: String?, typeData: MonitorDataFrame): String {
    if (dataFrame != null) {
        when (typeData) {
            MonitorDataFrame.POSITION_WHEEL -> return dataFrame.substring(10, 12)

            MonitorDataFrame.SENSOR_ID -> return dataFrame.substring(12, 18)

            MonitorDataFrame.PRESSION -> {
                val lowBits = dataFrame.substring(20, 22)
                val highBits = dataFrame.substring(18, 20)
                val refValue = Integer.parseInt(highBits + lowBits, 16)
                val pression = refValue * 0.025 * 14.5038
                return pression.toBigDecimal().round(MathContext.DECIMAL32)
                    .toDouble()
                    .toString()
            }

            MonitorDataFrame.TEMPERATURE -> {
                val temperatureHex = dataFrame.substring(22, 24)       // 53
                val temperature = temperatureHex.toInt(16) - 50    // 83 - 50 = 33
                return temperature.toString()
            }
        }
    }
    return "N/A"
}

fun decodeAlertDataFrame(dataFrame: String?, alertType: SensorAlertDataFrame): SensorAlerts {
    if (dataFrame != null) {
        when (alertType) {
            SensorAlertDataFrame.LOW_BATTERY -> {
                val status = dataFrame.substring(24, 25)
                val binary = status.toInt(16).toString(2).padStart(4, '0')
                return if (binary.substring(0, 1) != "0") SensorAlerts.LOW_BATTERY
                else SensorAlerts.NO_DATA
            }

            SensorAlertDataFrame.PRESSURE -> {
                val status1 = dataFrame.substring(24, 25)
                val binary1 = status1.toInt(16).toString(2).padStart(4, '0')

                val status2 = dataFrame.substring(25, 26)
                val binary2 = status2.toInt(16).toString(2).padStart(4, '0')

                val highPressureSignal = binary1.substring(3, 4) != "0"
                val lowPressureSignal = binary2.substring(0, 1) != "0"

                return if (lowPressureSignal && !highPressureSignal) SensorAlerts.LOW_PRESSURE
                else if (!lowPressureSignal && highPressureSignal) SensorAlerts.HIGH_PRESSURE
                else SensorAlerts.NO_DATA
            }

            SensorAlertDataFrame.HIGH_TEMPERATURE -> {
                val status = dataFrame.substring(25, 26)
                val binary = status.toInt(16).toString(2).padStart(4, '0')
                // Si es diferente de 0 es alta
                return if (binary.substring(1, 2) != "0") SensorAlerts.HIGH_TEMPERATURE
                else SensorAlerts.NO_DATA
            }

            SensorAlertDataFrame.FLAT_TIRE -> {
                val status = dataFrame.substring(25, 26)
                val binary = status.toInt(16).toString(2).padStart(4, '0')
                val bits = binary.substring(2, 4) // 01 = Fuga rapida, 10 = Fuga lenta
                when (bits) {
                    "01" -> SensorAlerts.FAST_LEAKAGE
                    "10" -> SensorAlerts.SLOW_LEAKAGE
                    else -> SensorAlerts.NO_DATA
                }
            }
        }
    }
    return SensorAlerts.NO_DATA
}

fun verifyTemperature(dataFrame: String?): Boolean {
    if (dataFrame != null) {
        val temperatureHex = dataFrame.substring(22, 24)       // 53
        val temperature = temperatureHex.toInt(16) - 50    // 83 - 50 = 33
        if (temperature in -40..85) return true
    }
    return false
}