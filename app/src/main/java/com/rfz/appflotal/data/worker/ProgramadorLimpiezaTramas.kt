package com.rfz.appflotal.data.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

private const val TRABAJO_LIMPIAR_TRAMAS = "limpiar-tramas"

/** Una vez al día; KEEP para no reiniciar el ciclo cada vez que se abre la app. */
fun programarLimpiezaTramas(context: Context) {
    val trabajo = PeriodicWorkRequestBuilder<LimpiarTramasWorker>(1, TimeUnit.DAYS).build()
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        TRABAJO_LIMPIAR_TRAMAS,
        ExistingPeriodicWorkPolicy.KEEP,
        trabajo
    )
}
