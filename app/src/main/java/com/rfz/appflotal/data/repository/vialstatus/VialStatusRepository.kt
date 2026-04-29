package com.rfz.appflotal.data.repository.vialstatus

import com.rfz.appflotal.data.model.catalog.StateDto
import com.rfz.appflotal.data.network.client.vialstatus.RoadMapDto
import com.rfz.appflotal.data.network.service.catalog.RemoteCatalogDataSource
import com.rfz.appflotal.data.network.service.vialstatus.RemoteVialStatusDataSource
import javax.inject.Inject

class VialStatusRepository @Inject constructor(
    private val remoteVialStateRepository: RemoteVialStatusDataSource,
    private val remoteCatalogClient: RemoteCatalogDataSource
) {
    suspend fun getStates(countryId: Int): Result<List<StateDto>> {
        return remoteCatalogClient.getStates(countryId)
    }

    suspend fun getMapByState(stateId: Int): Result<List<RoadMapDto>> {
        return remoteVialStateRepository.getMapByState(stateId)
    }
}