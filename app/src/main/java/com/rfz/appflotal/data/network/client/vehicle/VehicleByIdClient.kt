package com.rfz.appflotal.data.network.client.vehicle

import com.rfz.appflotal.data.model.provider.response.ProviderListResponse
import com.rfz.appflotal.data.model.vehicle.response.VehicleIdResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface VehicleByIdClient {

    @GET("Vehicle/GetVehicleById")
    suspend fun doGetVehicleById(@Header("Authorization") token: String, @Query("id_vehicle") id_vehicle: Int): Response<List<VehicleIdResponse>>
}