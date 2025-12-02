package com.rfz.appflotal.data.repository.database

import com.rfz.appflotal.data.dao.CoordinatesDao
import com.rfz.appflotal.data.model.database.CoordinatesEntity
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorTire
import jakarta.inject.Inject

class CoordinatesTableRepository @Inject constructor(private val coordinatesDao: CoordinatesDao) {
    suspend fun insertCoordinates(monitorId: Int, coordinatesList: List<MonitorTire>) {
        coordinatesList.forEach { coords ->
            coordinatesDao.insertCoordinates(coords.toEntity(monitorId))
        }
    }

    suspend fun getCoordinates(monitorId: Int): List<CoordinatesEntity> {
        return coordinatesDao.getCoordinates(monitorId)
    }

    suspend fun deleteCoordinates(monitorId: Int) = coordinatesDao.deleteCoordinates(monitorId)

    suspend fun updateCoordinates(
        monitorId: Int,
        tire: String,
        isActive: Boolean,
        isAlert: Boolean
    ) = coordinatesDao.updateCoordinates(
        monitorId = monitorId,
        tire = tire,
        isActive = isActive,
        isAlert = isAlert
    )
}

fun MonitorTire.toEntity(monitorId: Int): CoordinatesEntity {
    return CoordinatesEntity(
        monitorId = monitorId,
        idPosition = sensorPosition,
        isAssembled = isAssembled,
        inAlert = inAlert,
        isActive = isActive,
        xPosition = xPosition,
        yPosition = yPosition,
    )
}