package com.rfz.appflotal.data.repository.brand

import com.rfz.appflotal.data.model.brand.dto.BrandCrudDto
import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.network.service.brand.BrandCrudService
import javax.inject.Inject


class BrandCrudRepository @Inject constructor(private val brandCrudService: BrandCrudService) {

    suspend fun doBrandCrud(requestBody: BrandCrudDto, tok: String): Result<List<MessageResponse>> {
        return try {
            val response = brandCrudService.doBrandCrud(requestBody,tok)
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