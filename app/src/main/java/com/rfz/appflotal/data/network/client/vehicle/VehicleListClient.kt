package com.rfz.appflotal.data.network.client.vehicle

import com.rfz.appflotal.data.model.vehicle.response.VehicleListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface VehicleListClient {

    @GET("api/Catalog/VehicleList")
    suspend fun doVehicleList(@Header("Authorization") token: String): Response<List<VehicleListResponse>>
}