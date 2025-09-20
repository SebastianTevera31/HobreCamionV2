package com.rfz.appflotal.data.repository.defaultparameter

import com.rfz.appflotal.data.model.defaultparameter.dto.CreateDefaultParameterRequest
import com.rfz.appflotal.data.model.defaultparameter.response.CreateDefaultParameterResponse
import com.rfz.appflotal.data.network.service.defaultparameter.CreateDefaultParameterService
import jakarta.inject.Inject

class CreateDefaultParameterRepository @Inject constructor(
    private val service: CreateDefaultParameterService
) {
    suspend fun createDefaultParameter(
        token: String,
        request: CreateDefaultParameterRequest
    ): Result<List<CreateDefaultParameterResponse>> {
        return try {
            val response = service.createDefaultParameter(token, request)
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
