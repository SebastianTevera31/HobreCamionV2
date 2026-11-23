package com.rfz.appflotal.data.repository.database

import com.rfz.appflotal.data.dao.DataframeDao
import com.rfz.appflotal.data.model.database.DataframeEntity
import com.rfz.appflotal.core.util.Commons.getCurrentUtcDate
import java.time.Instant
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataframeTableRepository @Inject constructor(
    private val dataframeDao: DataframeDao,
) {
    suspend fun insert(sensorTpmsEntity: DataframeEntity): Long =
        dataframeDao.insert(sensorTpmsEntity)

    /**
     * Borra las tramas viejas para que la tabla no crezca sin límite: las ya resueltas al mes y
     * las pendientes a los dos meses. Devuelve cuántas se borraron.
     */
    suspend fun limpiarTramas(ahora: Instant = Instant.now()): Int {
        val hoy = ahora.atZone(ZoneOffset.UTC)
        val corteResueltas = getCurrentUtcDate(hoy.minusMonths(1).toInstant())
        val cortePendientes = getCurrentUtcDate(hoy.minusMonths(2).toInstant())
        return dataframeDao.borrarResueltasAntesDe(corteResueltas) +
            dataframeDao.borrarPendientesAntesDe(cortePendientes)
    }

    suspend fun getUnsentRecords(
        vehicleId: Int,
    ) = dataframeDao.getUnsentRecords(vehicleId)

    suspend fun getLastRecord(userId: Int): List<DataframeEntity?> =
        dataframeDao.getLastRecords(userId)

    suspend fun exist(sensorId: String): Boolean = dataframeDao.exist(sensorId)

    suspend fun setRecordStatus(idRecord: Long, sendStatus: Boolean, active: Boolean) =
        dataframeDao.setRecordStatus(idRecord, sendStatus, active)
}