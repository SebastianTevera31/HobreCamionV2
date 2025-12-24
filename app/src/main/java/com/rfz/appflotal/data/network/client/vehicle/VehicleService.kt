package com.rfz.appflotal.data.network.client.vehicle

import com.rfz.appflotal.data.model.lastodometer.LastOdometerResponseDto
import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.vehicle.UpdateVehicleDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface VehicleService {
    @GET("api/Vehicle/getLastOdometer")
    suspend fun fetchLastOdometer(@Header("Authorization") token: String): Response<List<LastOdometerResponseDto>?>

    @POST("api/Vehicle/CrudVehicle")
    suspend fun updateVehicleDate(
        @Header("Authorization") token: String,
        request: UpdateVehicleDto
    ): Response<List<GeneralResponse>>
}