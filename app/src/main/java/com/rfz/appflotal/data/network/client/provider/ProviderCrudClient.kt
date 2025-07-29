package com.rfz.appflotal.data.network.client.provider

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.product.dto.ProductCrudDto
import com.rfz.appflotal.data.model.provider.dto.ProviderDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ProviderCrudClient {

    @POST("api/Catalog/CrudProvider")
    suspend fun doCrudProvider(@Body requestBody: ProviderDto, @Header("Authorization") token: String): Response<List<MessageResponse>>
}