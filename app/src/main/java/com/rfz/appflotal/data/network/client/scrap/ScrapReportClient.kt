package com.rfz.appflotal.data.network.client.scrap


import com.rfz.appflotal.data.model.scrap.response.ScrapReportResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ScrapReportClient {

    @GET("api/Catalog/ScrapReport")
    suspend fun doScrapReport(@Header("Authorization") token: String): Response<List<ScrapReportResponse>>
}