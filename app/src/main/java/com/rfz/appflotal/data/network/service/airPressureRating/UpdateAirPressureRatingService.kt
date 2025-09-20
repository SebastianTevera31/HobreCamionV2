package com.rfz.appflotal.data.network.service.airPressureRating

import com.rfz.appflotal.data.model.airPressureRating.dto.CreateAirPressureRatingDto
import com.rfz.appflotal.data.model.airPressureRating.dto.UpdateAirPressureRatingDto
import com.rfz.appflotal.data.model.airPressureRating.response.CreateAirPressureRatingResponse
import com.rfz.appflotal.data.network.client.airPressureRating.CreateAirPressureRatingClient
import com.rfz.appflotal.data.network.client.airPressureRating.UpdateAirPressureRatingClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class UpdateAirPressureRatingService @Inject constructor(private val updateAirPressureRatingClient: UpdateAirPressureRatingClient) {
    suspend fun docupdateAirPressureRating(datos: UpdateAirPressureRatingDto, tok:String): Response<List<CreateAirPressureRatingResponse>> {
        return withContext(Dispatchers.IO) {
            updateAirPressureRatingClient.doUpdateAirPressureRating(datos,tok)
        }
    }
}