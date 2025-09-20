package com.rfz.appflotal.data.repository.depthcolor

import com.rfz.appflotal.data.model.depthcolor.dto.UpdateDepthColorRequest
import com.rfz.appflotal.data.model.depthcolor.response.UpdateDepthColorResponse
import com.rfz.appflotal.data.network.service.depthcolor.UpdateDepthColorService
import javax.inject.Inject

class UpdateDepthColorRepository @Inject constructor(
    private val service: UpdateDepthColorService
) {
    suspend fun updateDepthColor(
        token: String,
        request: UpdateDepthColorRequest
    ): Result<List<UpdateDepthColorResponse>> {
        return try {
            val response = service.updateDepthColor(token, request)
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
