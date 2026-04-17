package com.rfz.appflotal.data.repository.tire

import com.rfz.appflotal.data.model.tire.dto.InspectionTireDto
import com.rfz.appflotal.data.model.tire.dto.toEntity
import com.rfz.appflotal.data.network.service.assembly.AssemblySyncScheduler
import com.rfz.appflotal.data.network.service.tire.LocalInspectionDataSource
import javax.inject.Inject

class InspectionTireCrudRepository @Inject constructor(
    private val localInspectionDataSource: LocalInspectionDataSource,
    private val syncScheduler: AssemblySyncScheduler
) {

    suspend fun doInspectionTire(requestBody: InspectionTireDto): Result<Unit> {
        return try {
            // 1. Guardar localmente
            val entity = requestBody.toEntity(updatedAt = System.currentTimeMillis())
            localInspectionDataSource.saveInspectionTire(entity)

            // 2. Encolar sincronización
            syncScheduler.enqueueInspection(requestBody.positionTire)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}