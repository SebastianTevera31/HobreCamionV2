package com.rfz.appflotal.domain.tpmsUseCase

import com.rfz.appflotal.data.repository.UnidadPresion
import com.rfz.appflotal.data.repository.UnidadTemperatura
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.convertPressureValue
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.convertTemperatureValue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MonitorUnitConversionUseCase @Inject constructor() {
    data class Result(
        val temperature: Float,
        val pressure: Float
    )

    operator fun invoke(
        temp: Float,
        tempUnit: UnidadTemperatura,
        pressure: Float,
        pressureUnit: UnidadPresion
    ): Result {
        // Las funciones de conversión ahora esperan unidades base: Celsius y PSI
        val temperatureValue = convertTemperatureValue(temp, tempUnit)
        val pressureValue = convertPressureValue(pressure, pressureUnit)

        return Result(
            temperature = temperatureValue,
            pressure = pressureValue
        )
    }
}