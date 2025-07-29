package com.rfz.appflotal.data.network.service.retreadbrand

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.retreadbrand.response.RetreadBrandListResponse
import com.rfz.appflotal.data.network.client.retreaband.RetreadBrandListClient
import com.rfz.appflotal.data.network.client.retreaddesign.RetreadDesignListClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class RetreadBrandListService @Inject constructor(private val retreadBrandListClient: RetreadBrandListClient) {
    suspend fun doRetreadBrandList(tok:String): Response<List<RetreadBrandListResponse>> {
        return withContext(Dispatchers.IO) {
            retreadBrandListClient.doRetreadBrandList(tok)
        }
    }
}