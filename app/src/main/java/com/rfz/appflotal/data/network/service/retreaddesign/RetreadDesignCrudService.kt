package com.rfz.appflotal.data.network.service.retreaddesign

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.provider.dto.ProviderDto
import com.rfz.appflotal.data.model.retreaddesing.dto.RetreadDesignCrudDto
import com.rfz.appflotal.data.network.client.provider.ProviderCrudClient
import com.rfz.appflotal.data.network.client.retreaddesign.RetreadDesignCrudClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class RetreadDesignCrudService @Inject constructor(private val retreadDesignCrudClient: RetreadDesignCrudClient) {
    suspend fun doCrudRetreadDesign(requestBody: RetreadDesignCrudDto, tok:String): Response<List<MessageResponse>> {
        return withContext(Dispatchers.IO) {
            retreadDesignCrudClient.doCrudRetreadDesign(requestBody, tok)
        }
    }
}