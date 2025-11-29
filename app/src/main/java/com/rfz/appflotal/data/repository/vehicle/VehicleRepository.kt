package com.rfz.appflotal.data.repository.vehicle

import com.rfz.appflotal.data.model.lastodometer.LastOdometerResponseDto
import com.rfz.appflotal.data.network.service.vehicle.RemoteVehicleDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface VehicleRepository {
    suspend fun getLastOdometer(): LastOdometerResponseDto?
}

class VehicleRepositoryImpl @Inject constructor(
    private val remoteVehicleDataSource: RemoteVehicleDataSource,
    private val getTasksUseCase: GetTasksUseCase,
) :
    VehicleRepository {
    override suspend fun getLastOdometer(): LastOdometerResponseDto? {
        val token = getTasksUseCase().first().first().fld_token
        val result = remoteVehicleDataSource.fetchLastService(token)
        return if (result.isSuccess) {
            result.getOrNull()?.ifEmpty { emptyList() }[0]
        } else null
    }
}