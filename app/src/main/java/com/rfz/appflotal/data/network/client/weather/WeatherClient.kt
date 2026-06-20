package com.rfz.appflotal.data.network.client.weather

import com.rfz.appflotal.data.model.weather.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface WeatherClient {
    @GET("api/Weather/GetLatest")
    suspend fun getLatest(
        @Header("Authorization") token: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("nombreUbicacion") locationName: String,
    ): Response<WeatherResponse>

    @GET("api/Weather/WeatherApi")
    suspend fun getWeatherApi(
        @Header("Authorization") token: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("nombreUbicacion") nombreUbicacion: String
    ): Response<WeatherResponse>
}
