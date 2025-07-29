package com.rfz.appflotal.data.network.client.vehicle


import com.rfz.appflotal.data.model.vehicle.response.TypeVehicleResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface VehicleTypeClient {

    @GET("api/Catalog/TypeVehicle")
    suspend fun doTypeVehicle(@Header("Authorization") token: String): Response<List<TypeVehicleResponse>>
}