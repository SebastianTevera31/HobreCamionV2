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
import com.rfz.appflotal.data.repository.database.SensorDataTableRepository
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorTire
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.SensorAlerts
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.TireUiState
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.VOID_DATE
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.getIsTireInAlert
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class UpdateSensorDataUseCase @Inject constructor(private val sensorDataTableRepository: SensorDataTableRepository) {

    data class Result(
        val newTireUiState: TireUiState,
        val updatedTireList: List<MonitorTire>
    )

    suspend operator fun invoke(
        monitorId: Int,
        dataFrame: String,
        currentTires: List<MonitorTire>,
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

        val data =
            sensorDataTableRepository.getLastDataByTire(monitorId, realTire)

        val lastInspection = data?.lastInspection
        val isInspectionAvailable = if (lastInspection.isNullOrEmpty()) true else {
            try {
                val lastInspection = Instant.parse(lastInspection)
                val oneDayAgo = Instant.now().minus(1, ChronoUnit.DAYS)
                lastInspection.isBefore(oneDayAgo)
            } catch (_: DateTimeParseException) {
                try {
                    val lastInspection = LocalDateTime.parse(lastInspection)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                    val oneDayAgo = Instant.now().minus(1, ChronoUnit.DAYS)
                    lastInspection.isBefore(oneDayAgo)
                } catch (_: Exception) {
                    true
                }
            }
        }

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
            pressure = Pair(rawPressure, pressureStatus),
            rawPressure = rawPressure,
            temperature = Pair(rawTemp, temperatureStatus),
            rawTemperature = rawTemp,
            timestamp = time,
            batteryStatus = batteryStatus,
            flatTireStatus = flatTireStatus,
            tireRemovingStatus = if (rawPressure.toInt() == 0 && time != VOID_DATE) SensorAlerts.REMOVAL
            else SensorAlerts.NO_DATA,
            isInspectionAvailable = isInspectionAvailable
        )

        return Result(
            newTireUiState = newTireState,
            updatedTireList = newList
        )
    }
}