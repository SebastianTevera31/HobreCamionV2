package com.rfz.appflotal.data.repository.retreadbrand

import com.rfz.appflotal.data.model.retreadbrand.response.RetreadBrandListResponse
import com.rfz.appflotal.data.network.service.retreadbrand.RetreadBrandListService
import javax.inject.Inject


class RetreadBrandListRepository @Inject constructor(private val retreadBrandListService: RetreadBrandListService) {

    suspend fun doBrandCrud(): Result<List<RetreadBrandListResponse>> {
        return try {
            val response = retreadBrandListService.doRetreadBrandList()
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