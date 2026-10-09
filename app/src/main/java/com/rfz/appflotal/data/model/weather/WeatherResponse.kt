package com.rfz.appflotal.data.model.weather

import com.google.gson.annotations.SerializedName
import com.rfz.appflotal.core.util.AppLocale
import com.rfz.appflotal.core.util.WeatherMapper
import com.rfz.appflotal.domain.weather.City
import com.rfz.appflotal.domain.weather.HourlyForecast
import com.rfz.appflotal.domain.weather.WeatherCondition

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class WeatherResponse(
    @SerializedName("properties") val properties: PropertiesResponse? = null
)

data class PropertiesResponse(
    @SerializedName("timeseries") val timeseries: List<TimeSeriesResponse>? = null
)

data class TimeSeriesResponse(
    @SerializedName("time") val time: String? = null,
    @SerializedName("data") val data: DataResponse? = null
)

data class DataResponse(
    @SerializedName("instant") val instant: InstantResponse? = null,
    @SerializedName("next_1_hours") val next1Hours: NextHoursResponse? = null,
    @SerializedName("next_6_hours") val next6Hours: NextHoursResponse? = null,
    @SerializedName("next_12_hours") val next12Hours: NextHoursResponse? = null
)

data class InstantResponse(
    @SerializedName("details") val details: DetailsResponse? = null
)

data class DetailsResponse(
    @SerializedName("air_pressure_at_sea_level") val airPressure: Double? = null,
    @SerializedName("air_temperature") val airTemperature: Double? = null,
    @SerializedName("cloud_area_fraction") val cloudAreaFraction: Double? = null,
    @SerializedName("relative_humidity") val relativeHumidity: Double? = null,
    @SerializedName("wind_from_direction") val windFromDirection: Double? = null,
    @SerializedName("wind_speed") val windSpeed: Double? = null,
    @SerializedName("ultraviolet_index_clear_sky") val uvIndex: Double? = null,
    @SerializedName("dew_point_temperature") val dewPoint: Double? = null
)

data class NextHoursResponse(
    @SerializedName("summary") val summary: SummaryResponse? = null,
    @SerializedName("details") val details: PrecipitationDetailsResponse? = null
)

data class SummaryResponse(
    @SerializedName("symbol_code") val symbolCode: String? = null
)

data class PrecipitationDetailsResponse(
    @SerializedName("precipitation_amount") val precipitationAmount: Double? = null
)

fun WeatherResponse.toDomain(name: String): City {
    val latest = properties?.timeseries?.firstOrNull()
    val instant = latest?.data?.instant?.details
    val next1h = latest?.data?.next1Hours

    val temp = instant?.airTemperature?.toInt() ?: 0
    val symbol = next1h?.summary?.symbolCode ?: ""

    // Mapping Hourly Forecasts
    val hourlyList = properties?.timeseries?.take(24)?.map { ts ->
        // El API entrega UTC: se convierte a la zona horaria del dispositivo.
        val hourLabel = formatHourLabel(ts.time)
        val tsData = ts.data
        HourlyForecast(
            hour = hourLabel,
            cond = mapSymbolToCondition(
                tsData?.next1Hours?.summary?.symbolCode ?: tsData?.next6Hours?.summary?.symbolCode
            ),
            temp = tsData?.instant?.details?.airTemperature?.toInt() ?: 0,
            pop = (tsData?.next1Hours?.details?.precipitationAmount
                ?: tsData?.next6Hours?.details?.precipitationAmount ?: 0.0).toInt()
        )
    } ?: emptyList()

    return City(
        id = "latest",
        name = name,
        country = "",
        tz = "",
        temp = temp,
        cond = mapSymbolToCondition(symbol),
        condLabel = WeatherMapper.getConditionLabel(symbol),
        hi = properties?.timeseries?.take(24)
            ?.maxOfOrNull { it.data?.instant?.details?.airTemperature ?: -999.0 }?.toInt() ?: temp,
        lo = properties?.timeseries?.take(24)
            ?.minOfOrNull { it.data?.instant?.details?.airTemperature ?: 999.0 }?.toInt() ?: temp,
        feels = temp, // Met Norway doesn't provide "feels like" directly in compact
        rainChance = (next1h?.details?.precipitationAmount ?: 0.0).toInt(),
        wind = (instant?.windSpeed ?: 0.0).toInt(),
        windDir = mapDegreesToDirection(instant?.windFromDirection),
        aqi = 0,
        aqiLabel = "",
        uv = instant?.uvIndex?.toInt() ?: 0,
        uvLabel = WeatherMapper.getUvLabel(instant?.uvIndex),
        humidity = (instant?.relativeHumidity ?: 0.0).toInt(),
        dew = (instant?.dewPoint ?: 0.0).toInt(),
        pressure = (instant?.airPressure ?: 0.0).toInt(),
        vis = 10.0, // Not in this response
        sunrise = "",
        sunset = "",
        daylight = "",
        pollen = "",
        hourly = hourlyList
    )
}

private fun mapSymbolToCondition(symbol: String?): WeatherCondition {
    if (symbol == null) return WeatherCondition.Sunny
    // El orden importa: los simbolos compuestos ("rainandthunder", "lightsnowshowers") deben
    // resolverse por el fenomeno mas severo antes que por uno mas general.
    return when {
        symbol.contains("thunder") || symbol.contains("storm") -> WeatherCondition.Stormy
        symbol.contains("snow") -> WeatherCondition.Snow
        symbol.contains("rain") || symbol.contains("sleet") -> WeatherCondition.Rainy
        symbol.contains("fog") -> WeatherCondition.Cloudy
        symbol.contains("partlycloudy") -> WeatherCondition.PartlyCloudy
        symbol.contains("cloud") -> WeatherCondition.Cloudy
        else -> WeatherCondition.Sunny // clearsky, fair, sun
    }
}

private fun formatHourLabel(isoTime: String?): String {
    if (isoTime.isNullOrBlank()) return ""
    return try {
        val local = Instant.parse(isoTime).atZone(ZoneId.systemDefault())
        val formatter = if (AppLocale.currentLocale.value.language == "es") {
            DateTimeFormatter.ofPattern("HH':00'", Locale("es"))
        } else {
            DateTimeFormatter.ofPattern("h a", Locale.US)
        }
        local.format(formatter)
    } catch (e: Exception) {
        ""
    }
}

private fun mapDegreesToDirection(degrees: Double?): String {
    if (degrees == null) return "N"
    val lang = Locale.getDefault().language
    val directions = if (lang == "en") {
        listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW", "N")
    } else {
        listOf("N", "NE", "E", "SE", "S", "SO", "O", "NO", "N")
    }
    return directions[((degrees % 360) / 45).toInt()]
}
