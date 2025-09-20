package com.rfz.appflotal.data.network.service.depthcolor

import com.rfz.appflotal.data.model.depthcolor.dto.UpdateDepthColorRequest
import com.rfz.appflotal.data.model.depthcolor.response.UpdateDepthColorResponse
import com.rfz.appflotal.data.network.client.depthcolor.UpdateDepthColorClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class UpdateDepthColorService @Inject constructor(
    private val client: UpdateDepthColorClient
) {
    suspend fun updateDepthColor(
        token: String,
        request: UpdateDepthColorRequest
    ): Response<List<UpdateDepthColorResponse>> {
        return withContext(Dispatchers.IO) {
            client.updateDepthColor(token, request)
        }
    }
}
