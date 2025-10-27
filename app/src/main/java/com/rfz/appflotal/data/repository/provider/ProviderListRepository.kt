package com.rfz.appflotal.data.repository.provider

import com.rfz.appflotal.data.model.provider.response.ProviderListResponse
import com.rfz.appflotal.data.network.service.provider.ProviderListService
import javax.inject.Inject



class ProviderListRepository @Inject constructor(private val providerListService: ProviderListService) {

    suspend fun doProviderList( tok: String,id_typeProvider:Int): Result<List<ProviderListResponse>> {
        return try {
            val response = providerListService.doProviderList(tok,id_typeProvider)
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