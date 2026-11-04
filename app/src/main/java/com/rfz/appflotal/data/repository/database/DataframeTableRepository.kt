package com.rfz.appflotal.data.repository.database

import com.rfz.appflotal.data.dao.DataframeDao
import com.rfz.appflotal.data.model.database.DataframeEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataframeTableRepository @Inject constructor(
    private val dataframeDao: DataframeDao,
) {
    suspend fun insert(sensorTpmsEntity: DataframeEntity): Long =
        dataframeDao.insert(sensorTpmsEntity)

    suspend fun deleteOldRecords(vehicleId: Int) = dataframeDao.deleteOldRecords(vehicleId)

    suspend fun getUnsentRecords(
        vehicleId: Int,
    ) = dataframeDao.getUnsentRecords(vehicleId)

    suspend fun getLastRecord(userId: Int): List<DataframeEntity?> =
        dataframeDao.getLastRecords(userId)

    suspend fun exist(sensorId: String): Boolean = dataframeDao.exist(sensorId)

    suspend fun setRecordStatus(idRecord: Long, sendStatus: Boolean, active: Boolean) =
        dataframeDao.setRecordStatus(idRecord, sendStatus, active)
}