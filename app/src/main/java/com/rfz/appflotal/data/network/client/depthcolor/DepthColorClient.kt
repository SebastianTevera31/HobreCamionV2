package com.rfz.appflotal.data.network.client.depthcolor

import com.rfz.appflotal.data.model.depthcolor.response.DepthColorResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface DepthColorClient {

    @GET("api/DepthColor/DepthColor")
    suspend fun getDepthColors(
        @Header("Authorization") token: String
    ): Response<List<DepthColorResponse>>
}
