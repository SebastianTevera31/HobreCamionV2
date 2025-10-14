package com.rfz.appflotal.domain.tpmsUseCase

import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.core.util.Commons.getDateObject
import com.rfz.appflotal.core.util.Positions.findOutPosition
import com.rfz.appflotal.core.util.tpms.getPressure
import com.rfz.appflotal.core.util.tpms.getTemperature
import com.rfz.appflotal.data.repository.bluetooth.MonitorDataFrame
import com.rfz.appflotal.data.repository.bluetooth.SensorAlertDataFrame
import com.rfz.appflotal.data.repository.bluetooth.decodeAlertDataFrame
import com.rfz.appflotal.data.repository.bluetooth.decodeDataFrame
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.SensorAlerts
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.Tire
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.TireUiState
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.getIsTireInAlert
import javax.inject.Inject

class UpdateSensorDataUseCase @Inject constructor() {

    data class Result(
        val newTireUiState: TireUiState,
        val updatedTireList: List<Tire>
    )

    operator fun invoke(
        dataFrame: String,
        currentTires: List<Tire>,
        timestamp: String? = null
    ): Result {
        val pressure = getPressure(dataFrame)
        val pressureStatus = decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.PRESSURE)

        val temperature = getTemperature(dataFrame)
        val temperatureStatus =
            decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.HIGH_TEMPERATURE)

        val flatTireStatus = decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.PERFORACION)

        val batteryStatus = decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.LOW_BATTERY)

        val tire = decodeDataFrame(dataFrame, MonitorDataFrame.POSITION_WHEEL).toInt()
        val realTire =
            if (pressure.toInt() != 0 && temperature.toInt() != 0) findOutPosition("P${tire}") else ""


        val time = if (timestamp != null) {
            val getDate = getDateObject(timestamp)
            getCurrentDate(date = getDate, pattern = "dd/MM/yyyy HH:mm:ss")
        } else getCurrentDate(pattern = "dd/MM/yyyy HH:mm:ss")

        val inAlert =
            getIsTireInAlert(temperatureStatus, pressureStatus, batteryStatus, flatTireStatus)

        val newList = currentTires.map { tireData ->
            if (tireData.sensorPosition == realTire) tireData.copy(
                inAlert = inAlert,
                isActive = true
            ) else tireData
        }

        val newTireState = TireUiState(
            currentTire = realTire,
            pressure = Pair(pressure, pressureStatus),
            temperature = Pair(temperature, temperatureStatus),
            timestamp = time,
            batteryStatus = batteryStatus,
            flatTireStatus = flatTireStatus,
            tireRemovingStatus = if (pressure.toInt() == 0) SensorAlerts.REMOVAL else SensorAlerts.NO_DATA
        )

        return Result(
            newTireUiState = newTireState,
            updatedTireList = newList
        )
    }
}
