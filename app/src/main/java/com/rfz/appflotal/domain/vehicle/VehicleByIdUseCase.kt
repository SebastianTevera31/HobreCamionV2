package com.rfz.appflotal.domain.vehicle

import com.rfz.appflotal.data.model.vehicle.response.VehicleIdResponse
import com.rfz.appflotal.data.repository.vehicle.VehicleByIdRepository
import javax.inject.Inject

class VehicleByIdUseCase @Inject constructor(
    private val vehicleByIdRepository: VehicleByIdRepository
) {
    suspend operator fun invoke(token: String, vehicleId: Int): Result<List<VehicleIdResponse>> {
        return vehicleByIdRepository.doGetVehicleById(token, vehicleId)
    }
}
