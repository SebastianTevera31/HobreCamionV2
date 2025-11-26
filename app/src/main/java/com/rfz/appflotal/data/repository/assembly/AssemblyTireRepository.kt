package com.rfz.appflotal.data.repository.assembly

import android.util.Log
import com.rfz.appflotal.data.model.assembly.AssemblyTire
import com.rfz.appflotal.data.model.assembly.toEntity
import com.rfz.appflotal.data.network.service.assembly.AssemblySyncScheduler
import com.rfz.appflotal.data.network.service.assembly.LocalAssemblyDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface AssemblyTireRepository {
    suspend fun addAssemblyTire(assemblyTire: AssemblyTire)
}

class AssemblyTireRepositoryImpl @Inject constructor(
    private val localAssemblyDataSource: LocalAssemblyDataSource,
    private val getTasksUseCase: GetTasksUseCase,
    private val syncScheduler: AssemblySyncScheduler,
) : AssemblyTireRepository {
    override suspend fun addAssemblyTire(assemblyTire: AssemblyTire) {
        val userData = getTasksUseCase().first()[0]
        localAssemblyDataSource.saveAssemblyTire(
            assemblyTire.toEntity()
        ).also {
            Log.d("AddAssemblyTire", "Llamando a syncScheduler.enqueueCreate")
            syncScheduler.enqueueCreate(assemblyTire, userData.fld_token)
        }
    }
}