package com.rfz.appflotal.data.network.service.assembly

import com.rfz.appflotal.data.dao.AssemblyTireDao
import com.rfz.appflotal.data.model.assembly.AssemblyTireEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalAssemblyDataSource @Inject constructor(
    private val assemblyTireDao: AssemblyTireDao
) {
    suspend fun saveAssemblyTire(assemblyTire: AssemblyTireEntity) {
        try {
            assemblyTireDao.upsertAssemblyTire(assemblyTire)
        } catch (_: Exception) {

        }
    }

    suspend fun getAssemblyTire(position: String): AssemblyTireEntity? {
        return try {
            assemblyTireDao.getAssemblyTire(position = position)
        } catch (_: Exception) {
            null
        }
    }
}