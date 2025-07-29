package com.rfz.appflotal.domain.vehicle

import com.rfz.appflotal.data.model.vehicle.response.TypeVehicleResponse
import com.rfz.appflotal.data.repository.vehicle.VehicleTypeRepository
import javax.inject.Inject

class VehicleTypeUseCase @Inject constructor(
    private val vehicleTypeRepository: VehicleTypeRepository
) {
    suspend operator fun invoke(token: String): Result<List<TypeVehicleResponse>> {
        return vehicleTypeRepository.doTypeVehicle(token)
    }
}
