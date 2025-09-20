package com.rfz.appflotal.data.repository.depthcolor

import com.rfz.appflotal.data.model.depthcolor.dto.CreateDepthColorRequest
import com.rfz.appflotal.data.model.depthcolor.response.CreateDepthColorResponse
import com.rfz.appflotal.data.network.service.depthcolor.CreateDepthColorService
import javax.inject.Inject

class CreateDepthColorRepository @Inject constructor(
    private val service: CreateDepthColorService
) {
    suspend fun createDepthColor(
        token: String,
        request: CreateDepthColorRequest
    ): Result<List<CreateDepthColorResponse>> {
        return try {
            val response = service.createDepthColor(token, request)
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
