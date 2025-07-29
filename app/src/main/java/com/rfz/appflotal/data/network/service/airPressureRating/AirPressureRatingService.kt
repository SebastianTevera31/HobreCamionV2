package com.rfz.appflotal.data.network.service.airPressureRating

import com.rfz.appflotal.data.model.acquisitiontype.response.AcquisitionTypeResponse
import com.rfz.appflotal.data.model.airPressureRating.response.AirPressureRating
import com.rfz.appflotal.data.network.client.acquisitiontype.AcquisitionTypeClient
import com.rfz.appflotal.data.network.client.airPressureRating.AirPressureRatingClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class AirPressureRatingService @Inject constructor(private val airPressureRatingClient: AirPressureRatingClient) {
    suspend fun doAirPressureRating(tok:String): Response<List<AirPressureRating>> {
        return withContext(Dispatchers.IO) {
            airPressureRatingClient.doAirPressureRating(tok)
        }
    }
}