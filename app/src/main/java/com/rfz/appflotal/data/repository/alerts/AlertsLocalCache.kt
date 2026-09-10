package com.rfz.appflotal.data.repository.alerts

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rfz.appflotal.data.model.alerts.Alert
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

private val Context.alertsCacheDataStore by preferencesDataStore("alerts_cache")

/**
 * Guarda localmente el último lote de alertas más recientes para poder mostrarlas
 * cuando no hay conexión a internet.
 */
class AlertsLocalCache @Inject constructor(@param:ApplicationContext private val context: Context) {
    private val dataStore = context.alertsCacheDataStore

    suspend fun saveRecent(alerts: List<Alert>) {
        dataStore.edit { prefs ->
            prefs[ALERTS_KEY] = Json.encodeToString(alerts.take(MAX_CACHED))
        }
    }

    suspend fun getCached(): List<Alert> {
        val json = dataStore.data.first()[ALERTS_KEY] ?: return emptyList()
        return runCatching { Json.decodeFromString<List<Alert>>(json) }.getOrDefault(emptyList())
    }

    private companion object {
        val ALERTS_KEY = stringPreferencesKey("recent_alerts")
        const val MAX_CACHED = 30
    }
}
