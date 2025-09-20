package com.rfz.appflotal.data.repository.defaultparameter

import com.rfz.appflotal.data.model.defaultparameter.dto.UpdateDefaultParameterRequest
import com.rfz.appflotal.data.model.defaultparameter.response.UpdateDefaultParameterResponse
import com.rfz.appflotal.data.network.service.defaultparameter.UpdateDefaultParameterService
import javax.inject.Inject

class UpdateDefaultParameterRepository @Inject constructor(
    private val service: UpdateDefaultParameterService
) {
    suspend fun updateDefaultParameter(
        token: String,
        request: UpdateDefaultParameterRequest
    ): Result<List<UpdateDefaultParameterResponse>> {
        return try {
            val response = service.updateDefaultParameter(token, request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Throwable("Error: Cuerpo de la respuesta nulo"))
            } else {
                when (response.code()) {
                    401 -> Result.failure(Throwable("Error 401: No autorizado"))
                    else -> Result.failure(Throwable("Error en la respuesta del servidor: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
