package com.rfz.appflotal.data.network.client.depthcolor

import com.rfz.appflotal.data.model.depthcolor.dto.UpdateDepthColorRequest
import com.rfz.appflotal.data.model.depthcolor.response.UpdateDepthColorResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT

interface UpdateDepthColorClient {

    @PUT("api/DepthColor/updateDepthColor")
    suspend fun updateDepthColor(
        @Header("Authorization") token: String,
        @Body request: UpdateDepthColorRequest
    ): Response<List<UpdateDepthColorResponse>>
}
