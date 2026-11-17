package com.rfz.appflotal.domain.tpms

import com.rfz.appflotal.core.util.Commons.isVoidDate
import com.rfz.appflotal.core.util.Commons.convertUtcDate
import com.rfz.appflotal.core.util.Commons.displayDateTimePattern
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.core.util.Positions.findOutPosition
import com.rfz.appflotal.core.util.tpms.getPressure
import com.rfz.appflotal.core.util.tpms.getTemperature
import com.rfz.appflotal.data.repository.UnidadPresion
import com.rfz.appflotal.data.repository.UnidadTemperatura
import com.rfz.appflotal.data.repository.assembly.AssemblyTireRepository
import com.rfz.appflotal.data.repository.bluetooth.MonitorDataFrame
import com.rfz.appflotal.data.repository.bluetooth.SensorAlertDataFrame
import com.rfz.appflotal.data.repository.bluetooth.decodeAlertDataFrame
import com.rfz.appflotal.data.repository.bluetooth.decodeDataFrame
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.SensorAlerts
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.TireUiState
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.getIsTireInAlert
import javax.inject.Inject

class UpdateSensorDataUseCase @Inject constructor(
    private val assemblyTireRepository: AssemblyTireRepository
) {

    /**
     * [position] (vacía si la lectura no se asignó a ninguna llanta) e [inAlert] son el delta
     * que el llamador aplica sobre su lista de llantas actual; no se devuelve la lista completa
     * para no pisar cambios concurrentes (p. ej. montajes).
     */
    data class Result(
        val newTireUiState: TireUiState,
        val position: String,
        val inAlert: Boolean
    )

    suspend operator fun invoke(
        dataFrame: String,
        timestamp: String? = null,
        tempUnit: UnidadTemperatura,
        pressureUnit: UnidadPresion
    ): Result {
        val rawTemp = getTemperature(dataFrame)
        val rawPressure = getPressure(dataFrame)

        val pressureStatus = decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.PRESSURE)
        val temperatureStatus =
            decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.HIGH_TEMPERATURE)
        val flatTireStatus = decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.FLAT_TIRE)
        val batteryStatus = decodeAlertDataFrame(dataFrame, SensorAlertDataFrame.LOW_BATTERY)

        val tire = decodeDataFrame(dataFrame, MonitorDataFrame.POSITION_WHEEL).toInt()
        val realTire = if (rawPressure.toInt() != 0 || rawTemp.toInt() != 0) {
            findOutPosition("P${tire}")
        } else ""

        val time = if (timestamp != null) convertUtcDate(timestamp)
        else getCurrentDate(pattern = displayDateTimePattern())

        val inAlert =
            getIsTireInAlert(temperatureStatus, pressureStatus, batteryStatus, flatTireStatus)

        val isAssembled =
            realTire.isNotEmpty() && assemblyTireRepository.confirmTireMounted(realTire)

        val newTireState = TireUiState(
            currentTire = realTire,
            isAssembled = isAssembled,
            pressure = Pair(rawPressure, pressureStatus),
            rawPressure = rawPressure,
            temperature = Pair(rawTemp, temperatureStatus),
            rawTemperature = rawTemp,
            timestamp = time,
            batteryStatus = batteryStatus,
            flatTireStatus = flatTireStatus,
            tireRemovingStatus = if (rawPressure.toInt() == 0 && !isVoidDate(time)) SensorAlerts.REMOVAL
            else SensorAlerts.NO_DATA
            // isInspectionAvailable lo deriva MonitorViewModel.tireUiState de sensor_data.
        )

        return Result(
            newTireUiState = newTireState,
            position = realTire,
            inAlert = inAlert
        )
    }
}