package com.rfz.appflotal.data.repository.database


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rfz.appflotal.data.dao.AppHCDao
import com.rfz.appflotal.data.model.flotalSoft.AppHCEntity
import dagger.hilt.android.qualifiers.ApplicationContext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class HombreCamionRepository @Inject constructor(
    private val flotalDao: AppHCDao,
    @ApplicationContext private val context: Context
) {
    suspend fun clearUserData() {
        flotalDao.deleteAllFlotalSoft()
    }

    suspend fun getUserData(): AppHCEntity? {
        return flotalDao.getData().firstOrNull()?.firstOrNull()
    }

    suspend fun updateIdMonitor(
        idMonitor: Int,
        mac: String,
        baseConfiguration: String,
        idUser: Int
    ) {
        flotalDao.updateMonitorId(idMonitor, mac, baseConfiguration, idUser)
    }

    val tasks: Flow<List<AppHCEntity>> = flotalDao.getData().map { items ->
        items.map {
            AppHCEntity(
                id = it.id,
                id_user = it.id_user,
                fld_name = it.fld_name,
                fld_email = it.fld_email,
                fld_username = it.fld_username,
                fld_token = it.fld_token,
                id_monitor = it.id_monitor,
                monitorMac = it.monitorMac,
                baseConfiguration = it.baseConfiguration,
                idVehicle = it.idVehicle,
                vehiclePlates = it.vehiclePlates,
                paymentPlan = it.paymentPlan,
                fecha = it.fecha
            )
        }
    }

    suspend fun saveSelectedLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE] = language
        }
    }

    suspend fun getSavedLanguage(): String? {
        return try {
            context.dataStore.data
                .map { preferences -> preferences[PreferencesKeys.LANGUAGE] }
                .first()
        } catch (e: Exception) {
            null
        }
    }

    private object PreferencesKeys {
        val LANGUAGE = stringPreferencesKey("app_language")
    }

    suspend fun getTasks(): Flow<List<AppHCEntity>> {
        return flotalDao.getData()
    }

    suspend fun addTask(task: AppHCEntity) {
        flotalDao.addFlotalSoft(task)
    }

    suspend fun deleteAllTasks() {
        flotalDao.deleteAllFlotalSoft()
    }
}