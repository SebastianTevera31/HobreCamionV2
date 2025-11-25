package com.rfz.appflotal.data.network.client.axle

import com.rfz.appflotal.data.model.axle.GetAxleResponseDto
import retrofit2.http.GET
import retrofit2.http.Header

interface AxleService {
    @GET("api/Catalog/Axle")
    suspend fun getAxleList(@Header("Authorization") token: String): List<GetAxleResponseDto>
}