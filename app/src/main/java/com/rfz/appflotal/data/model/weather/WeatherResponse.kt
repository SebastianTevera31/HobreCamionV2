package com.rfz.appflotal.data.model.weather

import com.google.gson.annotations.SerializedName
import com.rfz.appflotal.domain.weather.City
import com.rfz.appflotal.domain.weather.HourlyForecast
import com.rfz.appflotal.domain.weather.WeatherCondition

data class WeatherResponse(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("country") val country: String? = null,
    @SerializedName("tz") val tz: String? = null,
    @SerializedName("temp") val temp: Int? = null,
    @SerializedName("cond") val cond: String? = null,
    @SerializedName("condLabel") val condLabel: String? = null,
    @SerializedName("hi") val hi: Int? = null,
    @SerializedName("lo") val lo: Int? = null,
    @SerializedName("feels") val feels: Int? = null,
    @SerializedName("rainChance") val rainChance: Int? = null,
    @SerializedName("wind") val wind: Int? = null,
    @SerializedName("windDir") val windDir: String? = null,
    @SerializedName("aqi") val aqi: Int? = null,
    @SerializedName("aqiLabel") val aqiLabel: String? = null,
    @SerializedName("uv") val uv: Int? = null,
    @SerializedName("uvLabel") val uvLabel: String? = null,
    @SerializedName("humidity") val humidity: Int? = null,
    @SerializedName("dew") val dew: Int? = null,
    @SerializedName("pressure") val pressure: Int? = null,
    @SerializedName("vis") val vis: Double? = null,
    @SerializedName("sunrise") val sunrise: String? = null,
    @SerializedName("sunset") val sunset: String? = null,
    @SerializedName("daylight") val daylight: String? = null,
    @SerializedName("pollen") val pollen: String? = null,
    @SerializedName("hourly") val hourly: List<HourlyForecastResponse>? = null
)

data class HourlyForecastResponse(
    @SerializedName("hour") val hour: String? = null,
    @SerializedName("cond") val cond: String? = null,
    @SerializedName("temp") val temp: Int? = null,
    @SerializedName("pop") val pop: Int? = null
)

fun WeatherResponse.toDomain(): City {
    return City(
        id = id ?: "",
        name = name ?: "",
        country = country ?: "",
        tz = tz ?: "",
        temp = temp ?: 0,
        cond = mapCondition(cond),
        condLabel = condLabel ?: "",
        hi = hi ?: 0,
        lo = lo ?: 0,
        feels = feels ?: 0,
        rainChance = rainChance ?: 0,
        wind = wind ?: 0,
        windDir = windDir ?: "",
        aqi = aqi ?: 0,
        aqiLabel = aqiLabel ?: "",
        uv = uv ?: 0,
        uvLabel = uvLabel ?: "",
        humidity = humidity ?: 0,
        dew = dew ?: 0,
        pressure = pressure ?: 0,
        vis = vis ?: 0.0,
        sunrise = sunrise ?: "",
        sunset = sunset ?: "",
        daylight = daylight ?: "",
        pollen = pollen ?: "",
        hourly = hourly?.map { it.toDomain() } ?: emptyList()
    )
}

fun HourlyForecastResponse.toDomain(): HourlyForecast {
    return HourlyForecast(
        hour = hour ?: "",
        cond = mapCondition(cond),
        temp = temp ?: 0,
        pop = pop ?: 0
    )
}

private fun mapCondition(cond: String?): WeatherCondition {
    return try {
        WeatherCondition.valueOf(cond ?: "Sunny")
    } catch (e: Exception) {
        WeatherCondition.Sunny
    }
}
