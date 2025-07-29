package com.rfz.appflotal.data.repository.provider

import com.rfz.appflotal.data.model.brand.dto.BrandCrudDto
import com.rfz.appflotal.data.model.brand.response.BranListResponse
import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.provider.dto.ProviderDto
import com.rfz.appflotal.data.network.service.brand.BrandCrudService
import com.rfz.appflotal.data.network.service.provider.ProviderCrudService
import javax.inject.Inject


class ProviderCrudRepository @Inject constructor(private val providerCrudService: ProviderCrudService) {

    suspend fun doBrandCrud(requestBody: ProviderDto, tok: String): Result<List<MessageResponse>> {
        return try {
            val response = providerCrudService.doCrudProvider(requestBody,tok)
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