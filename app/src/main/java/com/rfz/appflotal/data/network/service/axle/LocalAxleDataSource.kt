package com.rfz.appflotal.data.network.service.axle

import androidx.sqlite.SQLiteException
import com.rfz.appflotal.data.dao.AxleDao
import com.rfz.appflotal.data.model.axle.AxleEntity
import com.rfz.appflotal.data.network.service.DataError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalAxleDataSource @Inject constructor(
    private val axleDao: AxleDao
) {
    suspend fun getAxle() = withContext(Dispatchers.IO) {
        try {
            Result.success(axleDao.getAxle())
        } catch (e: SQLiteException) {
            Result.failure(DataError.Local(e))
        } catch (_: Exception) {
            Result.failure(DataError.Unknown())
        }
    }

    suspend fun saveAxles(axles: List<AxleEntity>) = withContext(Dispatchers.IO) {
        try {
            Result.success(axleDao.upsertAxles(axles))
        } catch (e: SQLiteException) {
            Result.failure(DataError.Local(e))
        } catch (_: Exception) {
            Result.failure(DataError.Unknown())
        }

    }
}