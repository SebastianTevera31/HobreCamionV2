package com.rfz.appflotal.data.repository.weather

import com.rfz.appflotal.data.model.weather.toDomain
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.network.service.weather.WeatherService
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.weather.City
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface WeatherRepository {
    suspend fun getLatest(lat: Double, lon: Double): ApiResult<City>
    suspend fun getWeatherApi(lat: Double, lon: Double, nombreUbicacion: String): ApiResult<City>
}

class WeatherRepositoryImpl @Inject constructor(
    private val weatherService: WeatherService,
    private val getTasksUseCase: GetTasksUseCase
) : WeatherRepository {

    override suspend fun getLatest(lat: Double, lon: Double): ApiResult<City> {
        return try {
            val user = getTasksUseCase().first().firstOrNull()
            val token = user?.fld_token ?: ""
            val response = weatherService.getLatest(token, lat, lon)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    ApiResult.Success(body.toDomain())
                } else {
                    ApiResult.Error(message = "Response body is null")
                }
            } else {
                ApiResult.Error(message = response.message())
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    override suspend fun getWeatherApi(
        lat: Double,
        lon: Double,
        nombreUbicacion: String
    ): ApiResult<City> {
        return try {
            val user = getTasksUseCase().first().firstOrNull()
            val token = user?.fld_token ?: ""
            val response = weatherService.getWeatherApi(token, lat, lon, nombreUbicacion)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    ApiResult.Success(body.toDomain())
                } else {
                    ApiResult.Error(message = "Response body is null")
                }
            } else {
                ApiResult.Error(message = response.message())
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}
