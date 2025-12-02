package com.rfz.appflotal.data.network.client.repair

import com.rfz.appflotal.data.model.repair.RepairCauseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface RepairService {
    @GET("api/Catalog/Repair")
    suspend fun getRepairCatalog(@Header("Authorization") token: String): Response<List<RepairCauseDto>>
}