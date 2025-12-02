package com.rfz.appflotal.data.repository.repair

import com.rfz.appflotal.data.model.repair.RepairCauseDto
import com.rfz.appflotal.data.network.service.repair.RemoteRepairDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface RepairRepository {
    suspend fun getRepairCatalog(): Result<List<RepairCauseDto>>
}

class RepairRepositoryImpl @Inject constructor(
    private val remoteRepairDataSource: RemoteRepairDataSource,
    private val getTasksUseCase: GetTasksUseCase
) : RepairRepository {
    override suspend fun getRepairCatalog(): Result<List<RepairCauseDto>> {
        val token = getTasksUseCase().first().first().fld_token
        return remoteRepairDataSource.getRepairCatalog(token)
    }
}
