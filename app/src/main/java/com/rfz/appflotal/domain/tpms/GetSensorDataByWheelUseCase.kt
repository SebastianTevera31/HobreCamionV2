package com.rfz.appflotal.domain.tpms

import com.rfz.appflotal.core.util.Commons.isVoidDate
import com.rfz.appflotal.core.util.Commons.convertUtcDate
import com.rfz.appflotal.data.repository.assembly.AssemblyTireRepository
import com.rfz.appflotal.data.repository.database.SensorDataTableRepository
import com.rfz.appflotal.data.model.tpms.SensorAlerts
import javax.inject.Inject

class GetSensorDataByWheelUseCase @Inject constructor(
    private val sensorDataTableRepository: SensorDataTableRepository,
    private val assemblyTireRepository: AssemblyTireRepository
) {

    /**
     * [inAlert] es el delta que el llamador aplica sobre su lista de llantas actual; no se
     * devuelve la lista completa para no pisar cambios concurrentes (p. ej. montajes).
     */
    data class Result(
        val lectura: LecturaLlanta,
        val inAlert: Boolean
    )

    suspend operator fun invoke(monitorId: Int, tireSelected: String): Result? {
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

        val rawTemp = data.temperature.toFloat()
        val rawPressure = data.pressure.toFloat()

        val date = convertUtcDate(data.timestamp)
        val removalStatus = if (rawPressure == 0f && !isVoidDate(date)) SensorAlerts.REMOVAL
        else SensorAlerts.NO_DATA

        val inAlert = estaEnAlerta(
            alertaTemperatura = temperatureStatus,
            alertaPresion = pressureStatus,
            bateria = batteryStatus,
            pinchazo = flatTireStatus,
            extraccion = removalStatus,
        )

        val isAssembled = assemblyTireRepository.confirmTireMounted(tireSelected)

        val lectura = LecturaLlanta(
            posicion = data.tire,
            montada = isAssembled,
            presionPsi = rawPressure,
            alertaPresion = pressureStatus,
            temperaturaC = rawTemp,
            alertaTemperatura = temperatureStatus,
            fecha = date,
            bateria = batteryStatus,
            pinchazo = flatTireStatus,
            extraccion = removalStatus
        )

        return Result(lectura = lectura, inAlert = inAlert)
    }
}
