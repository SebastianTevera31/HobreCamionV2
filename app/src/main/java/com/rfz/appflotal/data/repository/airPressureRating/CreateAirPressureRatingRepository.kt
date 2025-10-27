package com.rfz.appflotal.data.repository.airPressureRating

import com.rfz.appflotal.data.model.airPressureRating.dto.CreateAirPressureRatingDto
import com.rfz.appflotal.data.model.airPressureRating.response.CreateAirPressureRatingResponse
import com.rfz.appflotal.data.network.service.airPressureRating.CreateAirPressureRatingService
import javax.inject.Inject



class CreateAirPressureRatingRepository @Inject constructor(private val createAirPressureRatingService: CreateAirPressureRatingService) {

    suspend fun docreateAirPressureRating(requestBody: CreateAirPressureRatingDto, tok: String): Result<List<CreateAirPressureRatingResponse>> {
        return try {
            val response = createAirPressureRatingService.docreateAirPressureRating(requestBody,tok)
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