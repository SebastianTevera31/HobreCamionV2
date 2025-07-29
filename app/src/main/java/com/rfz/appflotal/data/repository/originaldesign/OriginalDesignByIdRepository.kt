package com.rfz.appflotal.data.repository.originaldesign

import com.rfz.appflotal.data.model.originaldesign.response.OriginalDesignByIdResponse
import com.rfz.appflotal.data.model.product.response.ProductByIdResponse
import com.rfz.appflotal.data.network.service.originaldesign.OriginalDesignByIdService
import com.rfz.appflotal.data.network.service.product.ProductByIdService
import javax.inject.Inject



class OriginalDesignByIdRepository @Inject constructor(private val originalDesignByIdService: OriginalDesignByIdService) {

    suspend fun doOriginalDesignById(id: Int, tok: String): Result<List<OriginalDesignByIdResponse>> {
        return try {
            val response = originalDesignByIdService.doOriginalDesignById(id,tok)
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