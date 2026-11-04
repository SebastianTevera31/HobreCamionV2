package com.rfz.appflotal.data.repository.fueling

import com.rfz.appflotal.data.model.fueling.FuelingPage
import com.rfz.appflotal.data.model.fueling.NewFueling
import com.rfz.appflotal.data.model.fueling.toDomain
import com.rfz.appflotal.data.model.fueling.toDto
import com.rfz.appflotal.data.network.service.fueling.RemoteFuelingDataSource
import com.rfz.appflotal.data.repository.location.LocationRepository
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface FuelingRepository {
    suspend fun insertFueling(fueling: NewFueling): Result<Unit>

    /**
     * [startDate] / [endDate] en ISO 8601 (o null para no filtrar);
     * [fuelTypeId] null = todos los combustibles.
     */
    suspend fun getFuelingHistory(
        startDate: String?,
        endDate: String?,
        fuelTypeId: Int?,
        page: Int,
        pageSize: Int
    ): Result<FuelingPage>
}

class FuelingRepositoryImpl @Inject constructor(
    private val remoteFuelingDataSource: RemoteFuelingDataSource,
    private val getTasksUseCase: GetTasksUseCase,
    private val locationRepository: LocationRepository
) : FuelingRepository {

    private suspend fun token(): String? =
        getTasksUseCase().first().firstOrNull()?.fld_token?.takeIf { it.isNotBlank() }

    override suspend fun insertFueling(fueling: NewFueling): Result<Unit> {
        val token = token() ?: return Result.failure(Exception("No hay usuario logueado"))
        // Sin permiso o sin ubicación disponible se envía (0, 0): el historial la trata como "sin ubicación".
        val (lat, lon) = locationRepository.getLastCoordinates() ?: (0.0 to 0.0)
        return remoteFuelingDataSource.insertFueling(token, fueling.toDto(lat, lon))
    }

    override suspend fun getFuelingHistory(
        startDate: String?,
        endDate: String?,
        fuelTypeId: Int?,
        page: Int,
        pageSize: Int
    ): Result<FuelingPage> {
        val token = token() ?: return Result.failure(Exception("No hay usuario logueado"))
        return remoteFuelingDataSource.getFuelingHistory(
            token = token,
            startDate = startDate,
            endDate = endDate,
            fuelType = fuelTypeId,
            page = page,
            pageSize = pageSize
        ).map { it.toDomain(requestedPage = page, requestedPageSize = pageSize) }
    }
}
