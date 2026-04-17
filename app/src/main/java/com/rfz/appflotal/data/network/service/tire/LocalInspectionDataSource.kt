package com.rfz.appflotal.data.network.service.tire

import com.rfz.appflotal.data.dao.InspectionTireDao
import com.rfz.appflotal.data.model.tire.dto.InspectionTireEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalInspectionDataSource @Inject constructor(
    private val inspectionTireDao: InspectionTireDao
) {
    fun observeInspectionTires(): Flow<List<InspectionTireEntity>> =
        inspectionTireDao.observeInspectionTires()

    suspend fun saveInspectionTire(inspectionTire: InspectionTireEntity) = withContext(Dispatchers.IO) {
        inspectionTireDao.upsertInspectionTire(inspectionTire)
    }

    suspend fun getInspectionTire(position: String): InspectionTireEntity? = withContext(Dispatchers.IO) {
        inspectionTireDao.getInspectionTire(position)
    }

    suspend fun deleteInspectionTire(position: String) = withContext(Dispatchers.IO) {
        inspectionTireDao.deleteInspectionTire(position)
    }
}