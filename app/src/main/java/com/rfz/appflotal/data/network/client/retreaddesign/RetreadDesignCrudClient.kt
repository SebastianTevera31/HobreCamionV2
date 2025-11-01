package com.rfz.appflotal.data.network.client.retreaddesign

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.provider.dto.ProviderDto
import com.rfz.appflotal.data.model.retreaddesing.dto.RetreadDesignCrudDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface RetreadDesignCrudClient {

    @POST("api/Catalog/CrudRetreadDesign")
    suspend fun doCrudRetreadDesign(
        @Header("Authorization") token: String,
        @Body requestBody: RetreadDesignCrudDto
    ): Response<List<MessageResponse>>
}