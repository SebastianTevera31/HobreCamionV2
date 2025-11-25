package com.rfz.appflotal.data.network.service.axle

import com.rfz.appflotal.data.dao.AxleDao
import com.rfz.appflotal.data.model.axle.AxleEntity
import javax.inject.Inject

class LocalAxleDataSource @Inject constructor(
    private val axleDao: AxleDao
) {
    suspend fun getAxle(): List<AxleEntity> =
        axleDao.getAxle()

    suspend fun saveAxles(axles: List<AxleEntity>) =
        axleDao.upsertAxles(axles)
}