package com.rfz.appflotal.data.network.service.provider

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.provider.dto.ProviderDto
import com.rfz.appflotal.data.network.client.provider.ProviderCrudClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject



class ProviderCrudService @Inject constructor(private val providerCrudClient: ProviderCrudClient) {
    suspend fun doCrudProvider(requestBody: ProviderDto, tok:String): Response<List<GeneralResponse>> {
        return withContext(Dispatchers.IO) {
            providerCrudClient.doCrudProvider(requestBody, tok)
        }
    }
}