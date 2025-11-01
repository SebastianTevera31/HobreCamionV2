package com.rfz.appflotal.data.repository.airPressureRating

import com.rfz.appflotal.data.model.airPressureRating.response.AirPressureRating
import com.rfz.appflotal.data.network.service.airPressureRating.AirPressureRatingService
import javax.inject.Inject



class AirPressureRatingRepository @Inject constructor(private val airPressureRatingService: AirPressureRatingService) {

    suspend fun doAirPressureRating(tok: String): Result<List<AirPressureRating>> {
        return try {
            val response = airPressureRatingService.doAirPressureRating(tok)
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