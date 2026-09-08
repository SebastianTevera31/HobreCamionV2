package com.rfz.appflotal

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.rfz.appflotal.core.util.AppLocale
import com.rfz.appflotal.data.worker.schedulePromotionsWorker
import com.rfz.appflotal.data.worker.triggerPromotionsSync
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ProyectoFscApp() : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory


    override fun onCreate() {
        super.onCreate()
        AppLocale.loadSavedLocale(this)
        schedulePromotionsWorker(this)
        // Cubre al usuario que ya tenía sesión iniciada y solo reabre la app
        // (no pasa por LoginViewModel.handleLoginSuccess); el worker se auto-descarta
        // si no hay sesión activa.
        triggerPromotionsSync(this)
    }


    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}