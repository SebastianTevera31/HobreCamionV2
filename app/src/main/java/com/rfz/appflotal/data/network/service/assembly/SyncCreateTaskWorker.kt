package com.rfz.appflotal.data.network.service.assembly

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rfz.appflotal.data.model.assembly.toDto
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncCreateAssemblyTireWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val remote: RemoteAssemblyDataSource,
    private val local: LocalAssemblyDataSource,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        Log.d("SyncWorker", "Worker Iniciado.")

        val position = inputData.getString("assemblyTirePosition")
            ?: run {
                Log.e("SyncWorker", "FAILURE: Posición del neumático no encontrada en InputData.")
                return Result.failure()
            }
        val token = inputData.getString("token")
            ?: run {
                Log.e("SyncWorker", "FAILURE: Token no encontrado en InputData.")
                return Result.failure()
            }

        return try {
            val record = local.getAssemblyTire(position).getOrNull()
            Log.d("SyncWorker", "Registro local obtenido para posición: $position")

            if (record == null) return Result.failure()

            val result = remote.pushAssemblyTire(
                token = token,
                assemblyTire = record.toDto().copy(idMonitor = record.idMonitor)
            ).getOrNull()

            if (result != null) {
                if (result[0].id == 200) return Result.success()
            }
            return Result.failure()

        } catch (e: Exception) {
            // Capturar cualquier excepción de red o base de datos que no esté cubierta por 'Result'
            Log.e(
                "SyncWorker",
                "FAILURE (Exception): Error inesperado durante la sincronización.",
                e
            )
            Result.failure() // Puedes usar Result.retry() si el error es recuperable (ej: red)
        }
    }
}