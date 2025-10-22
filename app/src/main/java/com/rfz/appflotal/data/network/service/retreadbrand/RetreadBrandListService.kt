package com.rfz.appflotal.data.network.service.retreadbrand

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.retreadbrand.response.RetreadBrandListResponse
import com.rfz.appflotal.data.network.client.retreaband.RetreadBrandListClient
import com.rfz.appflotal.data.network.client.retreaddesign.RetreadDesignListClient
import com.rfz.appflotal.data.network.requestHelper
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class RetreadBrandListService @Inject constructor(
    private val retreadBrandListClient: RetreadBrandListClient,
    private val getTasksUseCase: GetTasksUseCase
) {
    suspend fun doRetreadBrandList(): Response<List<RetreadBrandListResponse>> {
        return withContext(Dispatchers.IO) {
            val token = getTasksUseCase().first()[0].fld_token
            retreadBrandListClient.doRetreadBrandList("bearer $token")
        }
    }
}