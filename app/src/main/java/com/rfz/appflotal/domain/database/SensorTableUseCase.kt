package com.rfz.appflotal.domain.database

import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.data.model.flotalSoft.SensorTpmsEntity
import com.rfz.appflotal.data.repository.database.SensorTableRepository
import java.util.Calendar
import javax.inject.Inject

class SensorTableUseCase @Inject constructor(private val sensorTableRepository: SensorTableRepository) {
    suspend fun doInsert(
        sensorTpmsEntity: SensorTpmsEntity
    ) = sensorTableRepository.insert(sensorTpmsEntity)

    suspend fun doGetUnsentRecords(
        monitorId: Int,
    ) = sensorTableRepository.getUnsentRecords(monitorId)

    suspend fun doGetLastRecord(userId: Int): List<SensorTpmsEntity?> {
        return sensorTableRepository.getLastRecord(userId)
    }

    suspend fun doExist(sensorId: String): Boolean {
        return sensorTableRepository.exist(sensorId)
    }

    suspend fun doSetRecordStatus(monitorId: Int, timestamp: String, sendStatus: Boolean) {
        sensorTableRepository.setRecordStatus(monitorId, timestamp, sendStatus)
    }

}