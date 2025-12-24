package com.rfz.appflotal.data.network.client.airPressureRating

import com.rfz.appflotal.data.model.airPressureRating.dto.CreateAirPressureRatingDto
import com.rfz.appflotal.data.model.airPressureRating.response.CreateAirPressureRatingResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface CreateAirPressureRatingClient {

    @POST("api/AirPressureRating/createAirPressureRating")
    suspend fun docreateAirPressureRating(@Body requestBody: CreateAirPressureRatingDto, @Header("Authorization") token: String): Response<List<CreateAirPressureRatingResponse>>
}