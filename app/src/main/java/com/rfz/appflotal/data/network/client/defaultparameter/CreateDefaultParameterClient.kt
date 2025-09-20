package com.rfz.appflotal.data.network.client.defaultparameter

import com.rfz.appflotal.data.model.defaultparameter.dto.CreateDefaultParameterRequest
import com.rfz.appflotal.data.model.defaultparameter.response.CreateDefaultParameterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface CreateDefaultParameterClient {

    @POST("api/DefaultParameter/createDefaultParameter")
    suspend fun createDefaultParameter(
        @Header("Authorization") token: String,
        @Body request: CreateDefaultParameterRequest
    ): Response<List<CreateDefaultParameterResponse>>
}
