package com.rfz.appflotal.data.network.service.fueling

import com.rfz.appflotal.data.model.fueling.FuelingHistoryResponse
import com.rfz.appflotal.data.model.fueling.InsertFuelingDto
import com.rfz.appflotal.data.network.client.fueling.FuelingService
import com.rfz.appflotal.data.network.networkRequestHelper
import javax.inject.Inject

class RemoteFuelingDataSource @Inject constructor(
    private val fuelingService: FuelingService
) {
    suspend fun insertFueling(token: String, request: InsertFuelingDto): Result<Unit> =
        networkRequestHelper {
            fuelingService.insertFueling("Bearer $token", request)
        }

    suspend fun getFuelingHistory(
        token: String,
        startDate: String?,
        endDate: String?,
        fuelType: Int?,
        page: Int,
        pageSize: Int
    ): Result<FuelingHistoryResponse> = networkRequestHelper {
        fuelingService.getFuelingHistory(
            token = "Bearer $token",
            startDate = startDate,
            endDate = endDate,
            fuelType = fuelType,
            start = page,
            pageSize = pageSize
        )
    }
}
