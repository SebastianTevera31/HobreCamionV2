package com.rfz.appflotal.data.network.client.airPressureRating

import com.rfz.appflotal.data.model.airPressureRating.dto.CreateAirPressureRatingDto
import com.rfz.appflotal.data.model.airPressureRating.dto.UpdateAirPressureRatingDto
import com.rfz.appflotal.data.model.airPressureRating.response.CreateAirPressureRatingResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT


interface UpdateAirPressureRatingClient {

    @PUT("api/AirPressureRating/UpdateAirPressureRating")
    suspend fun doUpdateAirPressureRating(@Body requestBody: UpdateAirPressureRatingDto, @Header("Authorization") token: String): Response<List<CreateAirPressureRatingResponse>>
}