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
    val pendingMessagesFlow: Flow<List<AppUpdateMessage>>

    suspend fun enqueueMessage(msg: AppUpdateMessage)

    suspend fun dequeueMessage(): AppUpdateMessage?

    suspend fun clearAll()
}

class AppUpdateMessageRepositoryImpl @Inject constructor(@param:ApplicationContext private val context: Context) :
    AppUpdateMessageRepository {
    private val dataStore = context.appUpdateMessageDataStore

    override val pendingMessagesFlow = dataStore.data.map { prefs ->
        prefs[APP_UPDATE_KEY]?.let { json ->
            Json.decodeFromString<List<AppUpdateMessage>>(json)
        } ?: emptyList()
    }

    override suspend fun enqueueMessage(msg: AppUpdateMessage) {
        dataStore.edit { prefs ->
            val currentMessagesJson = prefs[APP_UPDATE_KEY]
            val currentMessages = if (currentMessagesJson != null) {
                Json.decodeFromString<MutableList<AppUpdateMessage>>(currentMessagesJson)
            } else {
                mutableListOf()
            }
            currentMessages.add(msg)
            prefs[APP_UPDATE_KEY] = Json.encodeToString(currentMessages)
        }
    }

    override suspend fun dequeueMessage(): AppUpdateMessage? {
        var dequeuedMessage: AppUpdateMessage? = null
        dataStore.edit { prefs ->
            val currentMessagesJson = prefs[APP_UPDATE_KEY] ?: return@edit
            val currentMessages = Json.decodeFromString<MutableList<AppUpdateMessage>>(currentMessagesJson)

            if (currentMessages.isNotEmpty()) {
                dequeuedMessage = currentMessages.removeAt(0)
                prefs[APP_UPDATE_KEY] = Json.encodeToString(currentMessages)
            }
        }
        return dequeuedMessage
    }

    override suspend fun clearAll() {
        dataStore.edit { prefs -> prefs.clear() }
    }

    companion object {
        // La clave no necesita cambiar. Seguirá guardando un string (JSON).
        private val APP_UPDATE_KEY = stringPreferencesKey("app_message")
    }
}