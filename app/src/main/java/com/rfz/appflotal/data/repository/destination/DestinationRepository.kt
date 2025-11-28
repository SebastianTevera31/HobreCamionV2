package com.rfz.appflotal.data.repository.destination

import com.rfz.appflotal.core.util.AppLocale
import com.rfz.appflotal.data.model.destination.Destination
import com.rfz.appflotal.data.model.destination.toDomain
import com.rfz.appflotal.data.network.service.destination.DestinationService
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class DestinationRepository @Inject constructor(
    private val destinationService: DestinationService, private val getTasksUseCase: GetTasksUseCase
) {

    suspend fun doDestination(): Result<List<Destination>> {
        return try {
            val language = AppLocale.currentLocale.first().language
            val token = getTasksUseCase().first().first().fld_token
            val response = destinationService.doDestination(token)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.map { destinationResponse ->
                        destinationResponse.toDomain().copy(
                            description = if (language.contains("es")) destinationResponse.fldDescription
                            else destinationResponse.fldEsDescription
                        )
                    })
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