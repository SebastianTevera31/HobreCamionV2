package com.rfz.appflotal.data.network.service.defaultparameter

import com.rfz.appflotal.data.model.defaultparameter.dto.CreateDefaultParameterRequest
import com.rfz.appflotal.data.model.defaultparameter.response.CreateDefaultParameterResponse
import com.rfz.appflotal.data.network.client.defaultparameter.CreateDefaultParameterClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class CreateDefaultParameterService @Inject constructor(
    private val client: CreateDefaultParameterClient
) {
    suspend fun createDefaultParameter(
        token: String,
        request: CreateDefaultParameterRequest
    ): Response<List<CreateDefaultParameterResponse>> {
        return withContext(Dispatchers.IO) {
            client.createDefaultParameter(token, request)
        }
    }
}
