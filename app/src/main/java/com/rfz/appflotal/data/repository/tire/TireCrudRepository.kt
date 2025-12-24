package com.rfz.appflotal.data.repository.tire

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.tire.dto.TireCrudDto
import com.rfz.appflotal.data.network.service.tire.TireCrudService
import javax.inject.Inject


class TireCrudRepository @Inject constructor(private val tireCrudService: TireCrudService) {

    suspend fun doTireCrud(requestBody: TireCrudDto, tok: String): Result<GeneralResponse> {
        return try {
            val response = tireCrudService.doTireCrud(requestBody,tok)
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