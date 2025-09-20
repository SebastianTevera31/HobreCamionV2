package com.rfz.appflotal.data.network.service.defaultparameter

import com.rfz.appflotal.data.model.defaultparameter.dto.UpdateDefaultParameterRequest
import com.rfz.appflotal.data.model.defaultparameter.response.UpdateDefaultParameterResponse
import com.rfz.appflotal.data.network.client.defaultparameter.UpdateDefaultParameterClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class UpdateDefaultParameterService @Inject constructor(
    private val client: UpdateDefaultParameterClient
) {
    suspend fun updateDefaultParameter(
        token: String,
        request: UpdateDefaultParameterRequest
    ): Response<List<UpdateDefaultParameterResponse>> {
        return withContext(Dispatchers.IO) {
            client.updateDefaultParameter(token, request)
        }
    }
}
