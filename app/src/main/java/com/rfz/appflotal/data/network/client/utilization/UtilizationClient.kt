package com.rfz.appflotal.data.network.client.utilization

import com.rfz.appflotal.data.model.scrap.response.ScrapReportResponse
import com.rfz.appflotal.data.model.utilization.response.UtilizationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header



interface UtilizationClient {

    @GET("api/Catalog/Utilization")
    suspend fun doUtilization(@Header("Authorization") token: String): Response<List<UtilizationResponse>>
}