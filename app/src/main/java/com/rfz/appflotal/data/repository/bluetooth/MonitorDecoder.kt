package com.rfz.appflotal.data.repository.bluetooth

import com.rfz.appflotal.presentation.ui.monitor.viewmodel.SensorAlerts
import java.math.MathContext
import java.util.Locale

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
        val length = dataFrame.length
        val byteLen = length / 2
        // El protocolo estándar es de 14 bytes. Si es de 15, hay un desplazamiento de 1 byte.
        val shift = if (byteLen >= 15) 1 else 0

        when (typeData) {
            MonitorDataFrame.POSITION_WHEEL -> {
                val posIndex = 10 + (shift * 2)
                if (length < posIndex + 2) return "N/A"

                val binaryString = dataFrame
                    .substring(posIndex, posIndex + 2)
                    .toIntOrNull(16)
                    ?.toString(2)
                    ?.padStart(8, '0')

                if (binaryString == null) return "00"

                val vehicleId = binaryString.substring(0, 3)
                val position = try {
                    binaryString.substring(3).toInt(2)
                } catch (e: Exception) {
                    0
                }

                val result = when (vehicleId) {
                    "000" -> position
                    "001" -> position + 10
                    "010" -> position + 22
                    else -> 0
                }

                return String.format(Locale.getDefault(), "%02d", result)
            }

            MonitorDataFrame.SENSOR_ID -> {
                val start = 12 + (shift * 2)
                val end = start + 6
                if (length < end) return "N/A"
                return dataFrame.substring(start, end)
            }

            MonitorDataFrame.PRESSION -> {
                val start = 18 + (shift * 2)
                if (length < start + 4) return "N/A"
                val highBits = dataFrame.substring(start, start + 2)
                val lowBits = dataFrame.substring(start + 2, start + 4)
                return try {
                    val refValue = Integer.parseInt(highBits + lowBits, 16)
                    val pressure = refValue * 0.025f * 14.5038f // PSI
                    pressure.toBigDecimal().round(MathContext.DECIMAL32)
                        .toDouble()
                        .toString()
                } catch (e: Exception) {
                    "N/A"
                }
            }

            MonitorDataFrame.TEMPERATURE -> {
                val start = 22 + (shift * 2)
                if (length < start + 2) return "N/A"
                return try {
                    val temperatureHex = dataFrame.substring(start, start + 2)
                    val temperature = temperatureHex.toInt(16) - 50
                    temperature.toString()
                } catch (e: Exception) {
                    "N/A"
                }
            }
        }
    }
    return "N/A"
}

fun decodeAlertDataFrame(dataFrame: String?, alertType: SensorAlertDataFrame): SensorAlerts {
    if (dataFrame != null) {
        val length = dataFrame.length
        val byteLen = length / 2
        val shift = if (byteLen >= 15) 1 else 0
        val statusStart = 24 + (shift * 2)

        if (length < statusStart + 2) return SensorAlerts.NO_DATA

        when (alertType) {
            SensorAlertDataFrame.LOW_BATTERY -> {
                return try {
                    val status = dataFrame.substring(statusStart, statusStart + 1)
                    val binary = status.toInt(16).toString(2).padStart(4, '0')
                    if (binary.substring(0, 1) != "0") SensorAlerts.LOW_BATTERY
                    else SensorAlerts.NO_DATA
                } catch (e: Exception) {
                    SensorAlerts.NO_DATA
                }
            }

            SensorAlertDataFrame.PRESSURE -> {
                return try {
                    val status1 = dataFrame.substring(statusStart, statusStart + 1)
                    val binary1 = status1.toInt(16).toString(2).padStart(4, '0')

                    val status2 = dataFrame.substring(statusStart + 1, statusStart + 2)
                    val binary2 = status2.toInt(16).toString(2).padStart(4, '0')

                    val highPressureSignal = binary1.substring(3, 4) != "0"
                    val lowPressureSignal = binary2.substring(0, 1) != "0"

                    if (lowPressureSignal && !highPressureSignal) SensorAlerts.LOW_PRESSURE
                    else if (!lowPressureSignal && highPressureSignal) SensorAlerts.HIGH_PRESSURE
                    else SensorAlerts.NO_DATA
                } catch (e: Exception) {
                    SensorAlerts.NO_DATA
                }
            }

            SensorAlertDataFrame.HIGH_TEMPERATURE -> {
                return try {
                    val status = dataFrame.substring(statusStart + 1, statusStart + 2)
                    val binary = status.toInt(16).toString(2).padStart(4, '0')
                    // Si es diferente de 0 es alta
                    if (binary.substring(1, 2) != "0") SensorAlerts.HIGH_TEMPERATURE
                    else SensorAlerts.NO_DATA
                } catch (e: Exception) {
                    SensorAlerts.NO_DATA
                }
            }

            SensorAlertDataFrame.FLAT_TIRE -> {
                return try {
                    val status = dataFrame.substring(statusStart + 1, statusStart + 2)
                    val binary = status.toInt(16).toString(2).padStart(4, '0')
                    val bits = binary.substring(2, 4) // 01 = Fuga rapida, 10 = Fuga lenta
                    when (bits) {
                        "01" -> SensorAlerts.FAST_LEAKAGE
                        "10" -> SensorAlerts.SLOW_LEAKAGE
                        else -> SensorAlerts.NO_DATA
                    }
                } catch (e: Exception) {
                    SensorAlerts.NO_DATA
                }
            }
        }
    }
    return SensorAlerts.NO_DATA
}

fun verifyTemperature(dataFrame: String?): Boolean {
    if (dataFrame != null) {
        val length = dataFrame.length
        val byteLen = length / 2
        val shift = if (byteLen >= 15) 1 else 0
        val tempStart = 22 + (shift * 2)

        if (length >= tempStart + 2) {
            return try {
                val temperatureHex = dataFrame.substring(tempStart, tempStart + 2)
                val temperature = temperatureHex.toInt(16) - 50
                temperature in -40..85
            } catch (e: Exception) {
                false
            }
        }
    }
    return false
}