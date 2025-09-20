package com.rfz.appflotal.data.repository.depthcolor

import com.rfz.appflotal.data.model.depthcolor.response.DepthColorResponse
import com.rfz.appflotal.data.network.service.depthcolor.DepthColorService
import javax.inject.Inject

class DepthColorRepository @Inject constructor(
    private val service: DepthColorService
) {
    suspend fun getDepthColors(token: String): Result<List<DepthColorResponse>> {
        return try {
            val response = service.getDepthColors(token)
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
