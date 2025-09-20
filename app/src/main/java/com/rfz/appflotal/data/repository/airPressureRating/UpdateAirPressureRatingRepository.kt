package com.rfz.appflotal.data.repository.airPressureRating

import com.rfz.appflotal.data.model.airPressureRating.dto.CreateAirPressureRatingDto
import com.rfz.appflotal.data.model.airPressureRating.response.CreateAirPressureRatingResponse
import com.rfz.appflotal.data.network.service.airPressureRating.CreateAirPressureRatingService
import com.rfz.appflotal.data.network.service.airPressureRating.UpdateAirPressureRatingService
import javax.inject.Inject



class UpdateAirPressureRatingRepository @Inject constructor(private val updateAirPressureRatingService: UpdateAirPressureRatingService) {

    suspend fun docupdateAirPressureRating(requestBody: CreateAirPressureRatingDto, tok: String): Result<List<CreateAirPressureRatingResponse>> {
        return try {
            val response = updateAirPressureRatingService.docupdateAirPressureRating(requestBody,tok)
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