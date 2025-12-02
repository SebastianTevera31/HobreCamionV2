package com.rfz.appflotal.data.network.service.repair

import com.rfz.appflotal.data.model.repair.RepairCauseDto
import com.rfz.appflotal.data.network.client.repair.RepairService
import com.rfz.appflotal.data.network.networkRequestHelper
import javax.inject.Inject

class RemoteRepairDataSource @Inject constructor(private val repairService: RepairService) {
    suspend fun getRepairCatalog(token: String) = networkRequestHelper {
        repairService.getRepairCatalog("Bearer $token")
    }
}