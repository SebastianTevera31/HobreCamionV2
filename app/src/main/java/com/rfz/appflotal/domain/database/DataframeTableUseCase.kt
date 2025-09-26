package com.rfz.appflotal.domain.database

import com.rfz.appflotal.data.model.database.DataframeEntity
import com.rfz.appflotal.data.repository.database.DataframeTableRepository
import javax.inject.Inject

class DataframeTableUseCase @Inject constructor(private val dataframeTableRepository: DataframeTableRepository) {
    suspend fun doInsert(
        sensorTpmsEntity: DataframeEntity
    ) = dataframeTableRepository.insert(sensorTpmsEntity)

    suspend fun doGetUnsentRecords(
        monitorId: Int,
    ) = dataframeTableRepository.getUnsentRecords(monitorId)

    suspend fun doGetLastRecord(userId: Int): List<DataframeEntity?> {
        return dataframeTableRepository.getLastRecord(userId)
    }

    suspend fun doExist(sensorId: String): Boolean {
        return dataframeTableRepository.exist(sensorId)
    }

    suspend fun doSetRecordStatus(monitorId: Int, timestamp: String, sendStatus: Boolean, active: Boolean) {
        dataframeTableRepository.setRecordStatus(monitorId, timestamp, sendStatus, active)
    }

}