package com.rfz.appflotal.data.network.service.provider

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.provider.dto.ProviderDto
import com.rfz.appflotal.data.model.provider.response.ProviderListResponse
import com.rfz.appflotal.data.network.client.provider.ProviderCrudClient
import com.rfz.appflotal.data.network.client.provider.ProviderListClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject



class ProviderListService @Inject constructor(private val providerListClient: ProviderListClient) {
    suspend fun doProviderList(tok:String,id_typeProvider:Int): Response<List<ProviderListResponse>> {
        return withContext(Dispatchers.IO) {
            providerListClient.doProviderList( tok,id_typeProvider)
        }
    }
}