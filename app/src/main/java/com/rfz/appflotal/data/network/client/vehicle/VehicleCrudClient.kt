package com.rfz.appflotal.data.network.client.vehicle

import com.rfz.appflotal.data.model.message.response.MessageResponse

import com.rfz.appflotal.data.model.vehicle.dto.VehicleCrudDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface VehicleCrudClient {

    @POST("Vehicle/CrudVehicle")
    suspend fun doCrudVehicle(@Body requestBody: VehicleCrudDto, @Header("Authorization") token: String): Response<MessageResponse>
}