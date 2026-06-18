package com.rfz.appflotal.data.network.service.weather

import com.rfz.appflotal.data.model.weather.WeatherResponse
import com.rfz.appflotal.data.network.client.weather.WeatherClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class WeatherService @Inject constructor(private val weatherClient: WeatherClient) {
    suspend fun getLatest(token: String, lat: Double, lon: Double): Response<WeatherResponse> {
        return withContext(Dispatchers.IO) {
            weatherClient.getLatest("bearer $token", lat, lon)
        }
    }

    suspend fun getWeatherApi(
        token: String,
        lat: Double,
        lon: Double,
        nombreUbicacion: String
    ): Response<WeatherResponse> {
        return withContext(Dispatchers.IO) {
            weatherClient.getWeatherApi("bearer $token", lat, lon, nombreUbicacion)
        }
    }
}
