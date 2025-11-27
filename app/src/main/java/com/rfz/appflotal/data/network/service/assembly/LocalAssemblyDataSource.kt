package com.rfz.appflotal.data.network.service.assembly

import android.database.sqlite.SQLiteException
import com.rfz.appflotal.data.dao.AssemblyTireDao
import com.rfz.appflotal.data.model.assembly.AssemblyTireEntity
import com.rfz.appflotal.data.network.service.DataError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalAssemblyDataSource @Inject constructor(
    private val assemblyTireDao: AssemblyTireDao
) {

    suspend fun observeAssemblyTire(): Flow<List<AssemblyTireEntity>> =
        withContext(Dispatchers.IO) {
            assemblyTireDao.observeAssemblyTires()
        }

    suspend fun saveAssemblyTire(assemblyTire: AssemblyTireEntity) = withContext(Dispatchers.IO) {
        try {
            Result.success(assemblyTireDao.upsertAssemblyTire(assemblyTire))
        } catch (e: SQLiteException) {
            Result.failure(DataError.Local(e))
        } catch (_: Exception) {
            Result.failure(DataError.Unknown())
        }
    }

    suspend fun getAssemblyTire(position: String) = withContext(Dispatchers.IO) {
        try {
            Result.success(assemblyTireDao.getAssemblyTire(position = position))
        } catch (e: SQLiteException) {
            Result.failure(DataError.Local(e))
        } catch (_: Exception) {
            Result.failure(DataError.Unknown())
        }
    }
}