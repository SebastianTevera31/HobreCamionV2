package com.rfz.appflotal.data.repository.tire

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.tire.dto.TireSizeDto
import com.rfz.appflotal.data.network.service.tire.TireSizeCrudService
import javax.inject.Inject



class TireSizeCrudRepository @Inject constructor(private val tireSizeCrudService: TireSizeCrudService) {

    suspend fun doCrudTireSize(requestBody: TireSizeDto, tok: String): Result<List<GeneralResponse>> {
        return try {
            val response = tireSizeCrudService.doCrudTireSize(requestBody,tok)
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