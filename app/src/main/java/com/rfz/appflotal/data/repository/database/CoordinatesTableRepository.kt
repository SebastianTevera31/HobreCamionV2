package com.rfz.appflotal.data.repository.database

import com.rfz.appflotal.data.dao.CoordinatesDao
import com.rfz.appflotal.data.model.database.CoordinatesEntity
import jakarta.inject.Inject

/** Caché de coordenadas del monitor; la llena DiagramaMonitorRepository al cargar con red. */
class CoordinatesTableRepository @Inject constructor(private val coordinatesDao: CoordinatesDao) {
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
}
