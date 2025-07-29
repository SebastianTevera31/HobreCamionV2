package com.rfz.appflotal.data.network.client.retreaband

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.provider.dto.ProviderDto
import com.rfz.appflotal.data.model.retreadbrand.dto.RetreadBrandDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface RetreadBrandCrudClient {

    @POST("api/Catalog/CrudRetreadBrand")
    suspend fun doRetreadBrand(@Body requestBody: RetreadBrandDto, @Header("Authorization") token: String): Response<List<MessageResponse>>
}