package com.rfz.appflotal.data.repository.brand

import com.rfz.appflotal.data.model.brand.response.BranListResponse
import com.rfz.appflotal.data.network.service.brand.BrandListService
import javax.inject.Inject


class BrandListRepository @Inject constructor(private val brandListService: BrandListService) {

    suspend fun doBrandList(tok: String, iduser:Int): Result<List<BranListResponse>> {
        return try {
            val response = brandListService.doBrandList(tok,iduser)
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