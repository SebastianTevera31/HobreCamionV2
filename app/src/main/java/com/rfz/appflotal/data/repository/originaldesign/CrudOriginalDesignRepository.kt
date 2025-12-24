package com.rfz.appflotal.data.repository.originaldesign

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.originaldesign.dto.CrudOriginalDesignDto
import com.rfz.appflotal.data.network.service.originaldesign.CrudOriginalDesignService
import javax.inject.Inject



class CrudOriginalDesignRepository @Inject constructor(private val crudOriginalDesignService: CrudOriginalDesignService) {

    suspend fun doCrudOriginalDesign(requestBody: CrudOriginalDesignDto, tok: String): Result<List<GeneralResponse>> {
        return try {
            val response = crudOriginalDesignService.doCrudOriginalDesign(requestBody,tok)
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