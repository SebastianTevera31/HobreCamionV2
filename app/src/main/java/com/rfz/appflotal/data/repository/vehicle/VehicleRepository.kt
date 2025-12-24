package com.rfz.appflotal.data.repository.vehicle

import com.rfz.appflotal.data.model.lastodometer.LastOdometerResponseDto
import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.vehicle.UpdateVehicle
import com.rfz.appflotal.data.model.vehicle.toDto
import com.rfz.appflotal.data.network.service.DataError
import com.rfz.appflotal.data.network.service.vehicle.RemoteVehicleDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface VehicleRepository {
    suspend fun getLastOdometer(token: String): LastOdometerResponseDto?
    suspend fun updateVehicleData(request: UpdateVehicle): Result<List<GeneralResponse>>
}

class VehicleRepositoryImpl @Inject constructor(
    private val remoteVehicleDataSource: RemoteVehicleDataSource,
    private val getTasksUseCase: GetTasksUseCase
) :
    VehicleRepository {
    override suspend fun getLastOdometer(token: String): LastOdometerResponseDto? {
        val result = remoteVehicleDataSource.fetchLastService(token)
        return if (result.isSuccess) {
            if (result.getOrNull()?.isNotEmpty() == true) {
                result.getOrNull()?.first()
            } else null
        } else null
    }

    override suspend fun updateVehicleData(
        request: UpdateVehicle
    ): Result<List<GeneralResponse>> {
        val token = getTasksUseCase().first()[0].fld_token
        val result = remoteVehicleDataSource.updateVehicleDate(token, request.toDto())
        return if (result.isSuccess) {
            Result.success(result.getOrNull()!!)
        } else Result.failure(DataError.Unknown())
    }
}