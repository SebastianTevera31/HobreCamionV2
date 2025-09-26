package com.rfz.appflotal.data.repository.database

import com.rfz.appflotal.data.dao.CoordinatesDao
import com.rfz.appflotal.data.model.database.CoordinatesEntity
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.Tire
import jakarta.inject.Inject
import jakarta.inject.Singleton

class CoordinatesTableRepository @Inject constructor(private val coordinatesDao: CoordinatesDao) {
    suspend fun insertCoordinates(monitorId: Int, coordinatesList: List<Tire>) {
        coordinatesList.forEach { coords ->
            coordinatesDao.insertCoordinates(coords.toEntity(monitorId))
        }
    }

    suspend fun getCoordinates(monitorId: Int): List<CoordinatesEntity> {
        return coordinatesDao.getCoordinates(monitorId)
    }

    suspend fun deleteCoordinates(monitorId: Int) = coordinatesDao.deleteCoordinates(monitorId)
}

fun Tire.toEntity(monitorId: Int): CoordinatesEntity {
    return CoordinatesEntity(
        monitorId = monitorId,
        idPosition = sensorPosition,
        inAlert = inAlert,
        isActive = isActive,
        xPosition = xPosition,
        yPosition = yPosition
    )
}