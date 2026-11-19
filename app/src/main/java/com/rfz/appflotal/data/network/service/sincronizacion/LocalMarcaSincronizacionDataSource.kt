package com.rfz.appflotal.data.network.service.sincronizacion

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.rfz.appflotal.data.repository.fcmessaging.userPreferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Hora de la última vez que la cola envió algo al servidor; sobrevive a reinicios de la app. */
class LocalMarcaSincronizacionDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    fun observar(): Flow<Long?> = context.userPreferencesDataStore.data.map { it[ULTIMA] }

    suspend fun registrar(momento: Long) {
        context.userPreferencesDataStore.edit { it[ULTIMA] = momento }
    }

    private companion object {
        val ULTIMA = longPreferencesKey("ultima_sincronizacion_operaciones")
    }
}
