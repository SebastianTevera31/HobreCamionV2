package com.rfz.appflotal.data.repository.performance

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rfz.appflotal.domain.performance.PerformanceData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

private val Context.performanceCacheDataStore by preferencesDataStore("performance_cache")

class PerformanceCacheRepository @Inject constructor(@param:ApplicationContext private val context: Context) {
    private val dataStore = context.performanceCacheDataStore

    suspend fun save(data: PerformanceData) {
        dataStore.edit { prefs ->
            prefs[PERFORMANCE_KEY] = Json.encodeToString(data)
        }
    }

    suspend fun get(): PerformanceData? {
        val json = dataStore.data.first()[PERFORMANCE_KEY] ?: return null
        return runCatching { Json.decodeFromString<PerformanceData>(json) }.getOrNull()
    }

    private companion object {
        val PERFORMANCE_KEY = stringPreferencesKey("performance_data")
    }
}
