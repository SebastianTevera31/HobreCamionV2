package com.rfz.appflotal.core.util

import androidx.annotation.StringRes
import com.rfz.appflotal.R

object WeatherMapper {

    @StringRes
    fun getConditionLabel(symbol: String?): Int {
        if (symbol == null) return R.string.clearsky
        
        val base = symbol.substringBefore("_")
        return when (base) {
            "clearsky" -> R.string.clearsky
            "cloudy" -> R.string.cloudy
            "fair" -> R.string.fair
            "fog" -> R.string.fog
            "heavyrain" -> R.string.heavyrain
            "heavyrainandthunder" -> R.string.heavyrainandthunder
            "heavyrainshowers" -> R.string.heavyrainshowers
            "heavyrainshowersandthunder" -> R.string.heavyrainshowersandthunder
            "heavysleet" -> R.string.heavysleet
            "heavysleetandthunder" -> R.string.heavysleetandthunder
            "heavysleetshowers" -> R.string.heavysleetshowers
            "heavysleetshowersandthunder" -> R.string.heavysleetshowersandthunder
            "heavysnow" -> R.string.heavysnow
            "heavysnowandthunder" -> R.string.heavysnowandthunder
            "heavysnowshowers" -> R.string.heavysnowshowers
            "heavysnowshowersandthunder" -> R.string.heavysnowshowersandthunder
            "lightrain" -> R.string.lightrain
            "lightrainandthunder" -> R.string.lightrainandthunder
            "lightrainshowers" -> R.string.lightrainshowers
            "lightrainshowersandthunder" -> R.string.lightrainshowersandthunder
            "lightsleet" -> R.string.lightsleet
            "lightsleetandthunder" -> R.string.lightsleetandthunder
            "lightsleetshowers" -> R.string.lightsleetshowers
            "lightsnow" -> R.string.lightsnow
            "lightsnowandthunder" -> R.string.lightsnowandthunder
            "lightsnowshowers" -> R.string.lightsnowshowers
            "lightssleetshowersandthunder" -> R.string.lightssleetshowersandthunder
            "lightssnowshowersandthunder" -> R.string.lightssnowshowersandthunder
            "partlycloudy" -> R.string.partlycloudy
            "rain" -> R.string.rain
            "rainandthunder" -> R.string.rainandthunder
            "rainshowers" -> R.string.rainshowers
            "rainshowersandthunder" -> R.string.rainshowersandthunder
            "sleet" -> R.string.sleet
            "sleetandthunder" -> R.string.sleetandthunder
            "sleetshowers" -> R.string.sleetshowers
            "sleetshowersandthunder" -> R.string.sleetshowersandthunder
            "snow" -> R.string.snow
            "snowandthunder" -> R.string.snowandthunder
            "snowshowers" -> R.string.snowshowers
            "snowshowersandthunder" -> R.string.snowshowersandthunder
            else -> R.string.weather_clear
        }
    }

    @StringRes
    fun getUvLabel(uv: Double?): Int {
        if (uv == null) return R.string.sin_datos
        val v = uv
        return when {
            v < 3 -> R.string.weather_uv_low
            v < 6 -> R.string.weather_uv_moderate
            v < 8 -> R.string.weather_uv_high
            v < 11 -> R.string.weather_uv_very_high
            else -> R.string.weather_uv_extreme
        }
    }
}
