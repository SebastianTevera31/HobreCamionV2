package com.rfz.appflotal.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.rfz.appflotal.data.repository.fcmessaging.userPreferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


enum class UnidadPresion(val symbol: String) {
    PSI("psi"), BAR("bar")
}

enum class UnidadTemperatura(val symbol: String) {
    CELCIUS("°C"), FAHRENHEIT("°F")
}

enum class UnidadOdometro(val symbol: String) {
    KILOMETROS("km/h"), MILLAS("mph")
}

interface UserPreferencesRepository {
    val temperatureUnitPreference: Flow<String>

    val pressureUnitPreference: Flow<String>

    val odometerUnitPreference: Flow<String>

    suspend fun setTemperatureUnitPreference(unit: UnidadTemperatura)

    suspend fun setPressureUnitPreference(unit: UnidadPresion)

    suspend fun setOdometerUnitPreference(unit: UnidadOdometro)
}

class UserPreferencesRepositoryImpl @Inject constructor(@param:ApplicationContext private val context: Context) :
    UserPreferencesRepository {
    private val userPreferencesDataStore = context.userPreferencesDataStore
    override val temperatureUnitPreference: Flow<String> = userPreferencesDataStore.data.map {
        it[TEMPERATURE_UNIT] ?: UnidadTemperatura.CELCIUS.name
    }
    override val pressureUnitPreference: Flow<String> = userPreferencesDataStore.data.map {
        it[PRESSURE_UNIT] ?: UnidadPresion.PSI.name
    }
    override val odometerUnitPreference: Flow<String> = userPreferencesDataStore.data.map {
        it[ODOMETER_UNIT] ?: UnidadOdometro.KILOMETROS.name
    }

    override suspend fun setTemperatureUnitPreference(unit: UnidadTemperatura) {
        userPreferencesDataStore.edit { preferences ->
            preferences[TEMPERATURE_UNIT] = unit.name
        }
    }

    override suspend fun setPressureUnitPreference(unit: UnidadPresion) {
        userPreferencesDataStore.edit { preferences ->
            preferences[PRESSURE_UNIT] = unit.name
        }
    }

    override suspend fun setOdometerUnitPreference(unit: UnidadOdometro) {
        userPreferencesDataStore.edit { preferences ->
            preferences[ODOMETER_UNIT] = unit.name
        }
    }

    companion object {
        private val PRESSURE_UNIT = stringPreferencesKey("pressure_unit")
        private val TEMPERATURE_UNIT = stringPreferencesKey("temperature_unit")

        private val ODOMETER_UNIT = stringPreferencesKey("odometer_unit")
    }

}
