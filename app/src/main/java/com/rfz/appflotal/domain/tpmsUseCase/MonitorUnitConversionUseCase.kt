package com.rfz.appflotal.domain.tpmsUseCase

import com.rfz.appflotal.data.repository.UnidadPresion
import com.rfz.appflotal.data.repository.UnidadTemperatura
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
        val temperatureValue = if (tempUnit == UnidadTemperatura.FAHRENHEIT) {
            (temp * 1.8f) + 32
        } else {
            temp
        }

        val pressureInBar = pressure * 0.025f
        val pressureValue = if (pressureUnit == UnidadPresion.BAR) {
            pressureInBar
        } else {
            pressureInBar * 14.5038f
        }

        return Result(
            temperature = temperatureValue,
            pressure = pressureValue
        )
    }
}