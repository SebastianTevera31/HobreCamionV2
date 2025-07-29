package com.rfz.appflotal.domain.vehicle

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.vehicle.dto.VehicleCrudDto
import com.rfz.appflotal.data.repository.vehicle.VehicleCrudRepository
import javax.inject.Inject

class VehicleCrudUseCase @Inject constructor(
    private val vehicleCrudRepository: VehicleCrudRepository
) {
    suspend operator fun invoke(requestBody: VehicleCrudDto, token: String): Result<MessageResponse> {
        return vehicleCrudRepository.doCrudVehicle(requestBody, token)
    }
}
