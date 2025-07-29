package com.rfz.appflotal.data.network.service.retreadbrand

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.retreadbrand.dto.RetreadBrandDto
import com.rfz.appflotal.data.model.retreaddesing.dto.RetreadDesignCrudDto
import com.rfz.appflotal.data.network.client.retreaband.RetreadBrandCrudClient
import com.rfz.appflotal.data.network.client.retreaddesign.RetreadDesignCrudClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject



class RetreadBrandCrudService @Inject constructor(private val retreadBrandCrudClient: RetreadBrandCrudClient) {
    suspend fun doRetreadBrand(requestBody: RetreadBrandDto, tok:String): Response<List<MessageResponse>> {
        return withContext(Dispatchers.IO) {
            retreadBrandCrudClient.doRetreadBrand(requestBody, tok)
        }
    }
}