package com.rfz.appflotal.data.network.client.waster

import com.rfz.appflotal.data.model.vehicle.response.VehicleListResponse
import com.rfz.appflotal.data.model.waster.response.WasteReportListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface WasteReportListClient {

    @GET("api/Catalog/WasteReportList")
    suspend fun doWasteReportList(@Header("Authorization") token: String): Response<List<WasteReportListResponse>>

}