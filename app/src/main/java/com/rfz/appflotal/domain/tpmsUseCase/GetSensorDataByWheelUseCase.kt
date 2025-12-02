package com.rfz.appflotal.domain.tpmsUseCase

import com.rfz.appflotal.core.util.Commons.convertDate
import com.rfz.appflotal.data.repository.assembly.AssemblyTireRepository
import com.rfz.appflotal.data.repository.database.SensorDataTableRepository
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.SensorAlerts
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorTire
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.TireUiState
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.getIsTireInAlert
import java.time.Instant
import java.time.format.DateTimeParseException
import javax.inject.Inject

class GetSensorDataByWheelUseCase @Inject constructor(
    private val sensorDataTableRepository: SensorDataTableRepository,
    private val assemblyTireRepository: AssemblyTireRepository
) {

    data class Result(
        val newTireUiState: TireUiState,
        val updatedTireList: List<MonitorTire>
    )

    suspend operator fun invoke(
        monitorId: Int,
        tireSelected: String,
        currentTires: List<MonitorTire>
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
        val isInspectionAvailable = if (lastInspection != null) {
            try {
                val inspectionDate = Instant.parse(lastInspection)
                Instant.now().isAfter(inspectionDate)
            } catch (_: DateTimeParseException) {
                // Log.e("GetSensorDataUseCase", "Formato de fecha inválido para lastInspection: $lastInspection")
                false
            }
        } else {
            false
        }


        val isAssembled = assemblyTireRepository.confirmTireMounted(tireSelected)

        val newTireUiState = TireUiState(
            currentTire = data.tire,
            pressure = Pair(data.pressure.toFloat(), pressureStatus),
            isAssembled = isAssembled,
            temperature = Pair(data.temperature.toFloat(), temperatureStatus),
            timestamp = convertDate(data.timestamp, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
            batteryStatus = batteryStatus,
            tireRemovingStatus = if (data.pressure == 0) SensorAlerts.REMOVAL else SensorAlerts.NO_DATA,
            flatTireStatus = flatTireStatus,
            isInspectionAvailable = isInspectionAvailable
        )

        return Result(
            newTireUiState = newTireUiState,
            updatedTireList = updatedTireList
        )
    }
}