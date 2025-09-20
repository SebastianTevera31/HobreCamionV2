package com.rfz.appflotal.data.network.client.depthcolor

import com.rfz.appflotal.data.model.depthcolor.dto.CreateDepthColorRequest
import com.rfz.appflotal.data.model.depthcolor.response.CreateDepthColorResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface CreateDepthColorClient {

    @POST("api/DepthColor/createDepthColor")
    suspend fun createDepthColor(
        @Header("Authorization") token: String,
        @Body request: CreateDepthColorRequest
    ): Response<List<CreateDepthColorResponse>>
}
