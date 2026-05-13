package com.rfz.appflotal.data.repository.vialstatus

import com.rfz.appflotal.data.model.catalog.StateDto
import com.rfz.appflotal.data.network.client.vialstatus.RoadMapDto
import com.rfz.appflotal.data.network.service.catalog.RemoteCatalogDataSource
import com.rfz.appflotal.data.network.service.vialstatus.RemoteVialStatusDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class VialStatusRepository @Inject constructor(
    private val remoteVialStateRepository: RemoteVialStatusDataSource,
    private val remoteCatalogClient: RemoteCatalogDataSource,
    private val userData: GetTasksUseCase
) {
    suspend fun getStates(countryId: Int): Result<List<StateDto>> {
        val user = userData().first().firstOrNull() ?: return Result.success(emptyList())
        return remoteCatalogClient.getStates(user.fld_token, countryId)
    }

    suspend fun getMapByState(stateId: Int): Result<List<RoadMapDto>> {
        val user = userData().first().firstOrNull() ?: return Result.success(emptyList())
        return remoteVialStateRepository.getMapByState(user.fld_token, stateId)
    }
}