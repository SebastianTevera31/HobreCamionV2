package com.rfz.appflotal.data.network.service.assembly

import com.rfz.appflotal.data.dao.AssemblyTireDao
import com.rfz.appflotal.data.model.assembly.AssemblyTireEntity
import javax.inject.Inject

class LocalAssemblyDataSource @Inject constructor(
    private val assemblyTireDao: AssemblyTireDao
) {
    suspend fun upsertAssemblyTire(assemblyTire: AssemblyTireEntity) {
        try {
            assemblyTireDao.upsertAssemblyTire(assemblyTire)
        } catch (_: Exception) {

        }
    }
}