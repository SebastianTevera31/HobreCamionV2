package com.rfz.appflotal.data.repository.database

import android.util.Log
import com.rfz.appflotal.data.dao.SensorDao
import com.rfz.appflotal.data.model.flotalSoft.SensorTpmsEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SensorTableRepository @Inject constructor(
    private val sensorDao: SensorDao,
) {
    suspend fun insert(sensorTpmsEntity: SensorTpmsEntity) {
        if (sensorTpmsEntity.monitorId != null) {
            sensorDao.insert(sensorTpmsEntity)
        } else {
            Log.d("SensorTable", "Registro no guardado")
        }
    }


    suspend fun deleteOldRecords(vehicleId: Int) = sensorDao.deleteOldRecords(vehicleId)

    suspend fun getUnsentRecords(
        vehicleId: Int,
    ) = sensorDao.getUnsentRecords(vehicleId)

    suspend fun getLastRecord(vehicleId: Int): SensorTpmsEntity? =
        sensorDao.getLastRecord(vehicleId)

    suspend fun exist(sensorId: String): Boolean = sensorDao.exist(sensorId)

    suspend fun setRecordStatus(monitorId: Int, timestamp: String, sendStatus: Boolean) =
        sensorDao.setRecordStatus(monitorId, timestamp, sendStatus)
}