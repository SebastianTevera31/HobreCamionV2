package com.rfz.appflotal.data.repository.utilization

import com.rfz.appflotal.data.model.utilization.response.UtilizationResponse
import com.rfz.appflotal.data.network.service.utilization.UtilizationService
import javax.inject.Inject


class UtilizationRepository @Inject constructor(private val utilizationService: UtilizationService) {

    suspend fun doUtilization(tok: String): Result<List<UtilizationResponse>> {
        return try {
            val response = utilizationService.doUtilization(tok)
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