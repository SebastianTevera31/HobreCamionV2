package com.rfz.appflotal.data.repository.assembly

import android.util.Log
import com.rfz.appflotal.data.model.assembly.AssemblyTire
import com.rfz.appflotal.data.model.assembly.AssemblyTireResponse
import com.rfz.appflotal.data.model.assembly.toDomain
import com.rfz.appflotal.data.model.assembly.toDto
import com.rfz.appflotal.data.model.assembly.toEntity
import com.rfz.appflotal.data.network.service.assembly.AssemblySyncScheduler
import com.rfz.appflotal.data.network.service.assembly.LocalAssemblyDataSource
import com.rfz.appflotal.data.network.service.assembly.RemoteAssemblyDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** id que devuelve api/AssemblyTire/CrudAssemblyTire en el cuerpo cuando guardó el montaje. */
const val ASSEMBLY_SUCCESS_ID = 200

private const val TAG = "AssemblyTireRepository"

/** El servidor respondió, pero no aceptó el montaje; [message] es el motivo que envió. */
class AssemblyRejectedException(message: String?) : Exception(message)

interface AssemblyTireRepository {
    suspend fun observeAssemblyTire(): Flow<List<AssemblyTire>>
    suspend fun addAssemblyTire(assemblyTire: AssemblyTire)
    suspend fun getAssemblyTire(positionTire: String): AssemblyTire?
    suspend fun confirmTireMounted(positionTire: String): Boolean
    suspend fun refreshMountedTires(): Result<List<AssemblyTireResponse>>
}

class AssemblyTireRepositoryImpl @Inject constructor(
    private val localAssemblyDataSource: LocalAssemblyDataSource,
    private val remoteAssemblyDataSource: RemoteAssemblyDataSource,
    private val getTasksUseCase: GetTasksUseCase,
    private val syncScheduler: AssemblySyncScheduler,
) : AssemblyTireRepository {
    override suspend fun observeAssemblyTire(): Flow<List<AssemblyTire>> =
        localAssemblyDataSource.observeAssemblyTire().map { list -> list.map { it.toDomain() } }

    /**
     * Envía el montaje en el momento. El API responde HTTP 200 aunque lo rechace; el resultado
     * real viene en el cuerpo (id = 200 si se guardó):
     * - Aceptado: se guarda en Room.
     * - Rechazado: no se guarda y se lanza [AssemblyRejectedException] con el mensaje del servidor.
     * - Sin red / error de transporte: se guarda en Room y lo envía después el worker.
     */
    override suspend fun addAssemblyTire(assemblyTire: AssemblyTire) {
        val userData = getTasksUseCase().first()[0]
        val entity = assemblyTire.toEntity().copy(idMonitor = userData.id_monitor)

        val pushResult = remoteAssemblyDataSource.pushAssemblyTire(userData.fld_token, entity.toDto())
        val response = pushResult.getOrNull()?.firstOrNull()

        when {
            response?.id == ASSEMBLY_SUCCESS_ID ->
                localAssemblyDataSource.saveAssemblyTire(entity).getOrThrow()

            pushResult.isSuccess -> {
                Log.w(TAG, "Montaje en ${entity.positionTire} rechazado: ${response?.message}")
                throw AssemblyRejectedException(response?.message)
            }

            else -> {
                Log.d(TAG, "Sin respuesta del servidor, se envía después: ${pushResult.exceptionOrNull()}")
                localAssemblyDataSource.saveAssemblyTire(entity).getOrThrow()
                syncScheduler.enqueueCreate(assemblyTire, userData.fld_token)
            }
        }
    }

    override suspend fun getAssemblyTire(positionTire: String): AssemblyTire? {
        val result = localAssemblyDataSource.getAssemblyTire(positionTire)
        return if (result.isSuccess) result.getOrNull()?.toDomain() else null
    }

    override suspend fun confirmTireMounted(positionTire: String): Boolean {
        val result = localAssemblyDataSource.getAssemblyTire(positionTire)
        return if (result.isSuccess) result.getOrNull()?.positionTire != null else false
    }

    override suspend fun refreshMountedTires(): Result<List<AssemblyTireResponse>> {
        val userData = getTasksUseCase().first()[0]
        val result =
            remoteAssemblyDataSource.fetchMountedTire(userData.fld_token, userData.id_monitor)
        return if (result.isSuccess) {
            result.onSuccess {
                it.forEach { assemblyTire ->
                    localAssemblyDataSource.saveAssemblyTire(
                        assemblyTire.toEntity().copy(idMonitor = userData.id_monitor)
                    )
                }
            }
        } else {
            Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
        }
    }
}