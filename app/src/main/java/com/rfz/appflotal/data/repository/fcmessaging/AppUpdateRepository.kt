package com.rfz.appflotal.data.repository.fcmessaging

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rfz.appflotal.data.model.fcmessaging.AppUpdateMessage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

val Context.appUpdateMessageDataStore by preferencesDataStore("appUpdateMessage")

interface AppUpdateMessageRepository {
    val updateFlow: Flow<AppUpdateMessage?>
    suspend fun saveMessage(msg: AppUpdateMessage)
    suspend fun clear()
}

class AppUpdateMessageRepositoryImpl @Inject constructor(@param:ApplicationContext private val context: Context) :
    AppUpdateMessageRepository {
    private val dataStore = context.appUpdateMessageDataStore

    override val updateFlow = dataStore.data.map { prefs ->
        prefs[APP_UPDATE_KEY]?.let { json ->
            Json.decodeFromString<AppUpdateMessage>(json)
        }
    }

    override suspend fun saveMessage(msg: AppUpdateMessage) {
        dataStore.edit { prefs ->
            prefs[APP_UPDATE_KEY] = Json.encodeToString(msg)
        }
    }

    override suspend fun clear() {
        dataStore.edit { prefs -> prefs.clear() }
    }

    companion object {
        private val APP_UPDATE_KEY = stringPreferencesKey("app_message")
    }
}