package com.rfz.appflotal.data.repository.base

import com.rfz.appflotal.data.model.base.BaseResponse
import com.rfz.appflotal.data.network.service.base.BaseService
import javax.inject.Inject



class BaseRepository @Inject constructor(private val baseService: BaseService) {

    suspend fun doBase(tok: String): Result<List<BaseResponse>> {
        return try {
            val response = baseService.doBase(tok)
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