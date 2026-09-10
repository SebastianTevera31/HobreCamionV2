package com.rfz.appflotal.data.repository.report

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rfz.appflotal.data.model.report.CO2EmissionsReportResponse
import com.rfz.appflotal.data.model.report.CpkReportResponse
import com.rfz.appflotal.data.model.report.FuelConsumptionReportResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

private val Context.reportCacheDataStore by preferencesDataStore("report_cache")

/**
 * Guarda localmente, por demanda (cada vez que el usuario consulta un reporte con éxito),
 * el último resultado de cada reporte para poder mostrarlo sin conexión a internet.
 */
class ReportLocalCache @Inject constructor(@param:ApplicationContext private val context: Context) {
    private val dataStore = context.reportCacheDataStore

    suspend fun saveCpkReport(reports: List<CpkReportResponse>) {
        dataStore.edit { prefs -> prefs[CPK_KEY] = Json.encodeToString(reports) }
    }

    suspend fun getCpkReport(): List<CpkReportResponse> {
        val json = dataStore.data.first()[CPK_KEY] ?: return emptyList()
        return runCatching { Json.decodeFromString<List<CpkReportResponse>>(json) }.getOrDefault(emptyList())
    }

    suspend fun saveCO2Report(reports: List<CO2EmissionsReportResponse>) {
        dataStore.edit { prefs -> prefs[CO2_KEY] = Json.encodeToString(reports) }
    }

    suspend fun getCO2Report(): List<CO2EmissionsReportResponse> {
        val json = dataStore.data.first()[CO2_KEY] ?: return emptyList()
        return runCatching { Json.decodeFromString<List<CO2EmissionsReportResponse>>(json) }.getOrDefault(emptyList())
    }

    suspend fun saveFuelReport(reports: List<FuelConsumptionReportResponse>) {
        dataStore.edit { prefs -> prefs[FUEL_KEY] = Json.encodeToString(reports) }
    }

    suspend fun getFuelReport(): List<FuelConsumptionReportResponse> {
        val json = dataStore.data.first()[FUEL_KEY] ?: return emptyList()
        return runCatching { Json.decodeFromString<List<FuelConsumptionReportResponse>>(json) }.getOrDefault(emptyList())
    }

    private companion object {
        val CPK_KEY = stringPreferencesKey("cpk_report")
        val CO2_KEY = stringPreferencesKey("co2_report")
        val FUEL_KEY = stringPreferencesKey("fuel_report")
    }
}
