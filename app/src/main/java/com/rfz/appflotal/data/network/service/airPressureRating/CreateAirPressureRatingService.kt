package com.rfz.appflotal.data.network.service.airPressureRating

import com.rfz.appflotal.data.model.airPressureRating.dto.CreateAirPressureRatingDto
import com.rfz.appflotal.data.model.airPressureRating.response.AirPressureRating
import com.rfz.appflotal.data.model.airPressureRating.response.CreateAirPressureRatingResponse
import com.rfz.appflotal.data.network.client.airPressureRating.AirPressureRatingClient
import com.rfz.appflotal.data.network.client.airPressureRating.CreateAirPressureRatingClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject



class CreateAirPressureRatingService @Inject constructor(private val createAirPressureRatingClient: CreateAirPressureRatingClient) {
    suspend fun docreateAirPressureRating(datos:CreateAirPressureRatingDto, tok:String): Response<List<CreateAirPressureRatingResponse>> {
        return withContext(Dispatchers.IO) {
            createAirPressureRatingClient.docreateAirPressureRating(datos,tok)
        }
    }
}