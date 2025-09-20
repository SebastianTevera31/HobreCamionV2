package com.rfz.appflotal.data.network.client.defaultparameter

import com.rfz.appflotal.data.model.defaultparameter.dto.UpdateDefaultParameterRequest
import com.rfz.appflotal.data.model.defaultparameter.response.UpdateDefaultParameterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT

interface UpdateDefaultParameterClient {

    @PUT("api/DefaultParameter/UpdateDefaultParameters")
    suspend fun updateDefaultParameter(
        @Header("Authorization") token: String,
        @Body request: UpdateDefaultParameterRequest
    ): Response<List<UpdateDefaultParameterResponse>>
}
