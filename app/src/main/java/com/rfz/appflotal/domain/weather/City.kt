package com.rfz.appflotal.domain.weather

import androidx.annotation.StringRes

data class City(
    val id: String,
    val name: String,
    val country: String,
    val tz: String,
    val temp: Int,
    val cond: WeatherCondition,
    @StringRes val condLabel: Int,
    val hi: Int,
    val lo: Int,
    val feels: Int,
    val rainChance: Int,
    val wind: Int,
    val windDir: String,
    val aqi: Int,
    val aqiLabel: String,
    val uv: Int,
    @StringRes val uvLabel: Int,
    val humidity: Int,
    val dew: Int,
    val pressure: Int,
    val vis: Double,
    val sunrise: String,
    val sunset: String,
    val daylight: String,
    val pollen: String,
    val hourly: List<HourlyForecast>
)

data class HourlyForecast(
    val hour: String,
    val cond: WeatherCondition,
    val temp: Int,
    val pop: Int
)

enum class WeatherCondition {
    Sunny, Cloudy, Rainy, Stormy, PartlyCloudy
}
