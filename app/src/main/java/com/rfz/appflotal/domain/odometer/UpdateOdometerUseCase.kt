package com.rfz.appflotal.domain.odometer

import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.data.repository.database.HombreCamionRepository
import javax.inject.Inject

/**
 * Guarda [odometerKm] como el odómetro vigente del vehículo (tabla user) para el resto de
 * operaciones (llantas, mantenimiento, siguientes cargas y servicios).
 *
 * Solo lo sube: un valor menor o igual al actual no lo pisa. Devuelve true si lo actualizó.
 * Se llama después de que el backend confirmó el registro que lo capturó.
 */
class UpdateOdometerUseCase @Inject constructor(
    private val hombreCamionRepository: HombreCamionRepository
) {
    suspend operator fun invoke(odometerKm: Int): Boolean {
        if (odometerKm <= hombreCamionRepository.getOdometer().odometer) return false
        hombreCamionRepository.updateOdometer(odometerKm, getCurrentDate())
        return true
    }
}
