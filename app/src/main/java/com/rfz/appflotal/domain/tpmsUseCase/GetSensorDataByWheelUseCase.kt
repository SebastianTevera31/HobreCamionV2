package com.rfz.appflotal.domain.tpmsUseCase

import com.rfz.appflotal.core.util.Commons.convertDate
import com.rfz.appflotal.data.repository.UnidadPresion
import com.rfz.appflotal.data.repository.UnidadTemperatura
import com.rfz.appflotal.data.repository.assembly.AssemblyTireRepository
import com.rfz.appflotal.data.repository.database.SensorDataTableRepository
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorTire
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.SensorAlerts
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.TireUiState
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.getIsTireInAlert
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class GetSensorDataByWheelUseCase @Inject constructor(
    private val sensorDataTableRepository: SensorDataTableRepository,
    private val assemblyTireRepository: AssemblyTireRepository,
    private val monitorUnitConversionUseCase: MonitorUnitConversionUseCase
) {

    data class Result(
        val newTireUiState: TireUiState,
        val updatedTireList: List<MonitorTire>
    )

    suspend operator fun invoke(
        monitorId: Int,
        tireSelected: String,
        currentTires: List<MonitorTire>,
        tempUnit: UnidadTemperatura,
        pressureUnit: UnidadPresion
    ): Result? {
        val data =
            sensorDataTableRepository.getLastDataByTire(monitorId, tireSelected) ?: return null

        val pressureStatus = if (data.lowPressureAlert) SensorAlerts.LOW_PRESSURE
        else if (data.highPressureAlert) SensorAlerts.HIGH_PRESSURE
        else SensorAlerts.NO_DATA

        val temperatureStatus = if (data.highTemperatureAlert) SensorAlerts.HIGH_TEMPERATURE
        else SensorAlerts.NO_DATA

        val batteryStatus = if (data.lowBatteryAlert) SensorAlerts.LOW_BATTERY
        else SensorAlerts.NO_DATA

        val flatTireStatus =
            if (data.punctureAlert) SensorAlerts.FAST_LEAKAGE else SensorAlerts.NO_DATA

        val sensorValues = monitorUnitConversionUseCase(
            temp = data.temperature.toFloat(),
            tempUnit = tempUnit,
            pressure = data.pressure.toFloat(),
            pressureUnit = pressureUnit
        )

        val inAlert = getIsTireInAlert(
            temperatureStatus = temperatureStatus,
            pressureStatus = pressureStatus,
            batteryStatus = batteryStatus,
            flatTireStatus = flatTireStatus,
        )

        val updatedTireList = currentTires.map { tire ->
            if (tire.sensorPosition == tireSelected) tire.copy(inAlert = inAlert) else tire
        }

        val lastInspection = data.lastInspection
        val isInspectionAvailable = if (lastInspection.isNullOrEmpty()) true else {
            try {
                val lastInspection = Instant.parse(lastInspection)
                val oneDayAgo = Instant.now().minus(1, ChronoUnit.DAYS)
                lastInspection.isBefore(oneDayAgo)
            } catch (_: DateTimeParseException) {
                try {
                    val lastInspection = LocalDateTime.parse(lastInspection)
                        .atZone(ZoneId.systemDefault()) // Asume la zona horaria del dispositivo
                        .toInstant()
                    val oneDayAgo = Instant.now().minus(1, ChronoUnit.DAYS)
                    lastInspection.isBefore(oneDayAgo)
                } catch (_: Exception) {
                    // Si el formato es completamente inesperado, es más seguro permitir la inspección.
                    true
                }
            }
        }


        val isAssembled = assemblyTireRepository.confirmTireMounted(tireSelected)

        val newTireUiState = TireUiState(
            currentTire = data.tire,
            pressure = Pair(sensorValues.pressure, pressureStatus),
            isAssembled = isAssembled,
            temperature = Pair(sensorValues.temperature, temperatureStatus),
            timestamp = convertDate(data.timestamp, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
            batteryStatus = batteryStatus,
            tireRemovingStatus = if (sensorValues.pressure == 0f) SensorAlerts.REMOVAL else SensorAlerts.NO_DATA,
            flatTireStatus = flatTireStatus,
            isInspectionAvailable = isInspectionAvailable
        )

        return Result(
            newTireUiState = newTireUiState,
            updatedTireList = updatedTireList
        )
    }
}