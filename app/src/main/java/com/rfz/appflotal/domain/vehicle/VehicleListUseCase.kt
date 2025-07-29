package com.rfz.appflotal.domain.vehicle

import com.rfz.appflotal.data.model.vehicle.response.VehicleListResponse
import com.rfz.appflotal.data.repository.vehicle.VehicleListRepository
import javax.inject.Inject

class VehicleListUseCase @Inject constructor(
    private val vehicleListRepository: VehicleListRepository
) {
    suspend operator fun invoke(token: String): Result<List<VehicleListResponse>> {
        return vehicleListRepository.doVehicleList(token)
    }
}
