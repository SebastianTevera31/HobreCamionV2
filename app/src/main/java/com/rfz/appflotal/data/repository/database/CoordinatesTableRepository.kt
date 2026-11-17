package com.rfz.appflotal.data.repository.database

import com.rfz.appflotal.data.dao.CoordinatesDao
import com.rfz.appflotal.data.model.database.CoordinatesEntity
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorTire
import jakarta.inject.Inject

class CoordinatesTableRepository @Inject constructor(private val coordinatesDao: CoordinatesDao) {
    /** Reemplaza (atómicamente) las coordenadas guardadas del monitor; una fila por posición. */
    suspend fun replaceCoordinates(monitorId: Int, coordinatesList: List<MonitorTire>) {
        coordinatesDao.replaceCoordinates(
            monitorId,
            coordinatesList.distinctBy { it.sensorPosition }.map { it.toEntity(monitorId) }
        )
    }

    /**
     * Una fila por posición. Instalaciones anteriores pueden tener la tabla duplicada (se
     * corrige en la siguiente carga con red); mientras tanto no debe llegar repetida a la UI.
     */
    suspend fun getCoordinates(monitorId: Int): List<CoordinatesEntity> {
        return coordinatesDao.getCoordinates(monitorId).distinctBy { it.idPosition }
    }

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

    suspend fun updateAssemblyStatus(
        monitorId: Int,
        tire: String,
        isAssembled: Boolean
    ) = coordinatesDao.updateAssemblyStatus(
        monitorId = monitorId,
        tire = tire,
        isAssembled = isAssembled
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