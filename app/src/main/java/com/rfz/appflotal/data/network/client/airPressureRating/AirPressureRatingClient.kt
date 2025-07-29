package com.rfz.appflotal.data.network.client.airPressureRating

import com.rfz.appflotal.data.model.acquisitiontype.response.AcquisitionTypeResponse
import com.rfz.appflotal.data.model.airPressureRating.response.AirPressureRating
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header


interface AirPressureRatingClient {
    @GET("api/AirPressureRating/AirPressureRating")
    suspend fun doAirPressureRating(@Header("Authorization") token: String): Response<List<AirPressureRating>>
}
