package com.rfz.appflotal.domain.tpmsUseCase

import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.core.util.Commons.getDateObject
import com.rfz.appflotal.core.util.Positions.findOutPosition
import com.rfz.appflotal.core.util.tpms.getPressure
import com.rfz.appflotal.core.util.tpms.getTemperature
import com.rfz.appflotal.data.repository.UnidadPresion
import com.rfz.appflotal.data.repository.UnidadTemperatura
import com.rfz.appflotal.data.repository.bluetooth.MonitorDataFrame
import com.rfz.appflotal.data.repository.bluetooth.SensorAlertDataFrame
import com.rfz.appflotal.data.repository.bluetooth.decodeAlertDataFrame
import com.rfz.appflotal.data.repository.bluetooth.decodeDataFrame
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorTire
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.SensorAlerts
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.TireUiState
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.getIsTireInAlert
import javax.inject.Inject

class UpdateSensorDataUseCase @Inject constructor(
    private val monitorUnitConversionUseCase: MonitorUnitConversionUseCase
) {

    data class Result(
        val newTireUiState: TireUiState,
        val updatedTireList: List<MonitorTire>
    )

    operator fun invoke(
        dataFrame: String,
        currentTires: List<MonitorTire>,
        timestamp: String? = null,
        tempUnit: UnidadTemperatura,
        pressureUnit: UnidadPresion
    ): Result {

        val sensorValues = monitorUnitConversionUseCase(
            temp = getTemperature(dataFrame),
            tempUnit = tempUnit,
            pressure = getPressure(dataFrame),
            pressureUnit = pressureUnit
        )

        val pressureStatus = decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.PRESSURE)

        val temperatureStatus =
            decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.HIGH_TEMPERATURE)

        val flatTireStatus = decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.FLAT_TIRE)

        val batteryStatus = decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.LOW_BATTERY)

        val tire = decodeDataFrame(dataFrame, MonitorDataFrame.POSITION_WHEEL).toInt()
        val realTire =
            if (sensorValues.pressure.toInt() != 0 || sensorValues.temperature.toInt() != 0) findOutPosition(
                "P${tire}"
            ) else ""


        val time = if (timestamp != null) {
            val getDate = getDateObject(timestamp)
            getCurrentDate(date = getDate, pattern = "dd/MM/yyyy HH:mm:ss")
        } else getCurrentDate(pattern = "dd/MM/yyyy HH:mm:ss")

        val inAlert =
            getIsTireInAlert(temperatureStatus, pressureStatus, batteryStatus, flatTireStatus)

        val newList = currentTires.map { tireData ->
            if (tireData.sensorPosition == realTire) tireData.copy(
                inAlert = inAlert,
                isActive = true,
            ) else tireData
        }

        val isAssembled = newList.find { it.sensorPosition == realTire }?.isAssembled == true

        val newTireState = TireUiState(
            currentTire = realTire,
            isAssembled = isAssembled,
            pressure = Pair(sensorValues.pressure, pressureStatus),
            temperature = Pair(sensorValues.temperature, temperatureStatus),
            timestamp = time,
            batteryStatus = batteryStatus,
            flatTireStatus = flatTireStatus,
            tireRemovingStatus = if (sensorValues.pressure.toInt() == 0) SensorAlerts.REMOVAL else SensorAlerts.NO_DATA
        )

        return Result(
            newTireUiState = newTireState,
            updatedTireList = newList
        )
    }
}
