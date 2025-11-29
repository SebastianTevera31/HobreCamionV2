package com.rfz.appflotal.data.network.client.vehicle

import com.rfz.appflotal.data.model.lastodometer.LastOdometerResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface VehicleService {
    @GET("api/Vehicle/getLastOdometer")
    suspend fun fetchLastOdometer(@Header("Authorization") token: String): Response<List<LastOdometerResponseDto>?>
}