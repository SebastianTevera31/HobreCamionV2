package com.rfz.appflotal.data.repository


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rfz.appflotal.data.dao.AppFlotalDao
import com.rfz.appflotal.data.model.flotalSoft.AppFlotalEntity
import com.rfz.appflotal.data.model.login.response.LoginResponse
import dagger.hilt.android.qualifiers.ApplicationContext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class FscSoftRepository @Inject constructor(private val flotalDao: AppFlotalDao, @ApplicationContext private val context: Context) {

     suspend fun clearUserData() {
         flotalDao.deleteAllFlotalSoft()
    }


     suspend fun getUserData(): AppFlotalEntity? {
        return flotalDao.getData().firstOrNull()?.firstOrNull()
    }
        val tasks: Flow<List<AppFlotalEntity>> = flotalDao.getData().map { items ->
        items.map {
            AppFlotalEntity(
                it.id,
                it.id_user,
                it.fld_name,

                it.fld_email,
                it.fld_token,
                it.fecha,

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

    suspend fun getTasks(): Flow<List<AppFlotalEntity>> {
        return flotalDao.getData()
    }

    suspend  fun addTask(task: AppFlotalEntity) {
        flotalDao.addFlotalSoft(task)
    }

    suspend  fun deleteAllTasks() {
        flotalDao.deleteAllFlotalSoft()
    }







}