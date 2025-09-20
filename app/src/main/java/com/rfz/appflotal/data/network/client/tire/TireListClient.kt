package com.rfz.appflotal.data.network.client.tire

import com.rfz.appflotal.data.model.tire.response.TireInspectionReportResponse
import com.rfz.appflotal.data.model.tire.response.TireListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface TireListClient {


    @GET("api/Tire/ListTire")
    suspend fun TireList(@Header("Authorization") token: String): Response<List<TireListResponse>>
}
