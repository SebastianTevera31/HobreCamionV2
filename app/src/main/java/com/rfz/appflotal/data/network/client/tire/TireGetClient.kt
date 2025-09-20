package com.rfz.appflotal.data.network.client.tire

import com.rfz.appflotal.data.model.provider.response.ProviderListResponse
import com.rfz.appflotal.data.model.tire.response.TireInspectionReportResponse
import com.rfz.appflotal.data.model.tire.response.TirexIdResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface TireGetClient {


    @GET("api/Tire/GetTireById")
    suspend fun doTireGet(@Header("Authorization") token: String, @Query("id_tire") id_tire: Int): Response<List<TirexIdResponse>>
}