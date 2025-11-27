package com.rfz.appflotal.data.repository.assembly

import android.util.Log
import com.rfz.appflotal.data.model.assembly.AssemblyTire
import com.rfz.appflotal.data.model.assembly.toDomain
import com.rfz.appflotal.data.model.assembly.toEntity
import com.rfz.appflotal.data.network.service.assembly.AssemblySyncScheduler
import com.rfz.appflotal.data.network.service.assembly.LocalAssemblyDataSource
import com.rfz.appflotal.data.network.service.assembly.RemoteAssemblyDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface AssemblyTireRepository {
    suspend fun observeAssemblyTire(): Flow<List<AssemblyTire>>
    suspend fun addAssemblyTire(assemblyTire: AssemblyTire)
    suspend fun getAssemblyTire(positionTire: String): AssemblyTire?
    suspend fun confirmTireMounted(positionTire: String): Boolean

    suspend fun refreshMountedTires()
}

class AssemblyTireRepositoryImpl @Inject constructor(
    private val localAssemblyDataSource: LocalAssemblyDataSource,
    private val remoteAssemblyDataSource: RemoteAssemblyDataSource,
    private val getTasksUseCase: GetTasksUseCase,
    private val syncScheduler: AssemblySyncScheduler,
) : AssemblyTireRepository {
    override suspend fun observeAssemblyTire(): Flow<List<AssemblyTire>> =
        localAssemblyDataSource.observeAssemblyTire().map { list -> list.map { it.toDomain() } }

    override suspend fun addAssemblyTire(assemblyTire: AssemblyTire) {
        val userData = getTasksUseCase().first()[0]
        localAssemblyDataSource.saveAssemblyTire(
            assemblyTire.toEntity().copy(idMonitor = userData.id_monitor)
        ).also {
            Log.d("AddAssemblyTire", "Llamando a syncScheduler.enqueueCreate")
            syncScheduler.enqueueCreate(assemblyTire, userData.fld_token)
        }
    }

    override suspend fun getAssemblyTire(positionTire: String): AssemblyTire? {
        return localAssemblyDataSource.getAssemblyTire(positionTire)?.toDomain()
    }

    override suspend fun confirmTireMounted(positionTire: String): Boolean {
        return localAssemblyDataSource.getAssemblyTire(positionTire) != null
    }

    override suspend fun refreshMountedTires() {
        val userData = getTasksUseCase().first()[0]
        val result =
            remoteAssemblyDataSource.fetchMountedTire(userData.fld_token, userData.id_monitor)
        if (result.isSuccessful) {
            result.body()?.forEach {
                localAssemblyDataSource.saveAssemblyTire(
                    it.toEntity().copy(idMonitor = userData.id_monitor)
                )
            }
        }
    }
}