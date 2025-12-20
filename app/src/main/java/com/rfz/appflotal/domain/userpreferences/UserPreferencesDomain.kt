package com.rfz.appflotal.domain.userpreferences

import com.rfz.appflotal.data.repository.UnidadPresion
import com.rfz.appflotal.data.repository.UnidadTemperatura
import com.rfz.appflotal.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObservePressureUnitUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<UnidadPresion> = repository.pressureUnitPreference.map {
        if (it == UnidadPresion.PSI.name) UnidadPresion.PSI else UnidadPresion.BAR
    }
}

class ObserveTemperatureUnitUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<UnidadTemperatura> = repository.temperatureUnitPreference.map {
        if (it == UnidadTemperatura.CELCIUS.name) UnidadTemperatura.CELCIUS else UnidadTemperatura.FAHRENHEIT
    }
}


class SwitchPressureUnitUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke() {
        val currentUnit = repository.pressureUnitPreference.first()
        val newUnit =
            if (currentUnit == UnidadPresion.PSI.name) UnidadPresion.BAR else UnidadPresion.PSI
        repository.setPressureUnitPreference(newUnit)
    }
}

class SwitchTemperatureUnitUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke() {
        val currentUnit = repository.temperatureUnitPreference.first()
        val newUnit =
            if (currentUnit == UnidadTemperatura.CELCIUS.name) UnidadTemperatura.FAHRENHEIT
            else UnidadTemperatura.CELCIUS
        repository.setTemperatureUnitPreference(newUnit)
    }
}