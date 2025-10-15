package com.rfz.appflotal.domain.tpmsUseCase

import com.rfz.appflotal.core.util.Commons.convertDate
import com.rfz.appflotal.data.repository.database.SensorDataTableRepository
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.SensorAlerts
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.Tire
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.TireUiState
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.getIsTireInAlert
import javax.inject.Inject

class GetSensorDataByWheelUseCase @Inject constructor(
    private val sensorDataTableRepository: SensorDataTableRepository
) {

    data class Result(
        val newTireUiState: TireUiState,
        val updatedTireList: List<Tire>
    )

    suspend operator fun invoke(
        monitorId: Int,
        tireSelected: String,
        currentTires: List<Tire>
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

        val inAlert = getIsTireInAlert(
            temperatureStatus = temperatureStatus,
            pressureStatus = pressureStatus,
            batteryStatus = batteryStatus,
            flatTireStatus = SensorAlerts.NO_DATA,
        )

        val updatedTireList = currentTires.map { tire ->
            if (tire.sensorPosition == tireSelected) tire.copy(inAlert = inAlert) else tire
        }

        val newTireUiState = TireUiState(
            currentTire = data.tire,
            pressure = Pair(data.pressure.toFloat(), pressureStatus),
            temperature = Pair(data.temperature.toFloat(), temperatureStatus),
            timestamp = convertDate(data.timestamp, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
            batteryStatus = batteryStatus,
            tireRemovingStatus = if (data.pressure == 0) SensorAlerts.REMOVAL else SensorAlerts.NO_DATA
        )

        return Result(
            newTireUiState = newTireUiState,
            updatedTireList = updatedTireList
        )
    }
}