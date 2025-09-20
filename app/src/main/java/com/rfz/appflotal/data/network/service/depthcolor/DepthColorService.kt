package com.rfz.appflotal.data.network.service.depthcolor

import com.rfz.appflotal.data.model.depthcolor.response.DepthColorResponse
import com.rfz.appflotal.data.network.client.depthcolor.DepthColorClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class DepthColorService @Inject constructor(
    private val client: DepthColorClient
) {
    suspend fun getDepthColors(token: String): Response<List<DepthColorResponse>> {
        return withContext(Dispatchers.IO) {
            client.getDepthColors(token)
        }
    }
}
