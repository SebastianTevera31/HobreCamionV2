package com.rfz.appflotal.data.repository.destination

import com.rfz.appflotal.data.model.brand.dto.BrandCrudDto
import com.rfz.appflotal.data.model.brand.response.BranListResponse
import com.rfz.appflotal.data.model.destination.response.DestinationResponse
import com.rfz.appflotal.data.network.service.brand.BrandCrudService
import com.rfz.appflotal.data.network.service.destination.DestinationService
import javax.inject.Inject


class DestinationRepository @Inject constructor(private val destinationService: DestinationService) {

    suspend fun doDestination(tok: String): Result<List<DestinationResponse>> {
        return try {
            val response = destinationService.doDestination(tok)
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