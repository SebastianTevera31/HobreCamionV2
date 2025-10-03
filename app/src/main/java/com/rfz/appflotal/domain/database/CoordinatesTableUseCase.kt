package com.rfz.appflotal.domain.database

import com.rfz.appflotal.data.model.database.CoordinatesEntity
import com.rfz.appflotal.data.repository.database.CoordinatesTableRepository
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.Tire
import jakarta.inject.Inject

class CoordinatesTableUseCase @Inject constructor(private val coordinatesTableRepository: CoordinatesTableRepository) {
    suspend fun insertCoordinates(monitorId: Int, listCoordinates: List<Tire>) {
        coordinatesTableRepository.insertCoordinates(monitorId, listCoordinates)
    }

    suspend fun getCoordinates(monitorId: Int): List<CoordinatesEntity> {
        return coordinatesTableRepository.getCoordinates(monitorId)
    }

    suspend fun deleteCoordinates(monitorId: Int) =
        coordinatesTableRepository.deleteCoordinates(monitorId)

    suspend fun updateCoordinates(
        monitorId: Int,
        tire: String,
        isActive: Boolean,
        isAlert: Boolean
    ) =
        coordinatesTableRepository.updateCoordinates(
            monitorId = monitorId,
            tire = tire,
            isActive = isActive,
            isAlert = isAlert
        )
}