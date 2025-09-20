package com.rfz.appflotal.data.network.service.depthcolor

import com.rfz.appflotal.data.model.depthcolor.dto.CreateDepthColorRequest
import com.rfz.appflotal.data.model.depthcolor.response.CreateDepthColorResponse
import com.rfz.appflotal.data.network.client.depthcolor.CreateDepthColorClient
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class CreateDepthColorService @Inject constructor(
    private val client: CreateDepthColorClient
) {
    suspend fun createDepthColor(
        token: String,
        request: CreateDepthColorRequest
    ): Response<List<CreateDepthColorResponse>> {
        return withContext(Dispatchers.IO) {
            client.createDepthColor(token, request)
        }
    }
}
