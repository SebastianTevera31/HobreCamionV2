package com.rfz.appflotal.data.network.service.disassembly

import com.rfz.appflotal.data.dao.DisassemblyTireDao
import com.rfz.appflotal.data.model.disassembly.tire.DisassemblyTireEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalDisassemblyDataSource @Inject constructor(
    private val disassemblyTireDao: DisassemblyTireDao
) {
    fun observeDisassemblyTire(): Flow<List<DisassemblyTireEntity>> =
        disassemblyTireDao.observeDisassemblyTires()

    suspend fun saveDisassemblyTire(disassemblyTire: DisassemblyTireEntity) = withContext(Dispatchers.IO) {
        disassemblyTireDao.upsertDisassemblyTire(disassemblyTire)
    }

    suspend fun getDisassemblyTire(position: String): DisassemblyTireEntity? = withContext(Dispatchers.IO) {
        disassemblyTireDao.getDisassemblyTire(position)
    }

    suspend fun deleteDisassemblyTire(position: String) = withContext(Dispatchers.IO) {
        disassemblyTireDao.deleteDisassemblyTire(position)
    }
}