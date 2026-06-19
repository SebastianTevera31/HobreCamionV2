package com.rfz.appflotal.presentation.ui.weather.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.CloudQueue
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rfz.appflotal.domain.weather.City
import com.rfz.appflotal.domain.weather.HourlyForecast
import com.rfz.appflotal.domain.weather.WeatherCondition
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.components.LoadingDialog
import com.rfz.appflotal.presentation.ui.utils.LoadState
import com.rfz.appflotal.presentation.ui.weather.viewmodel.WeatherUiState
import com.rfz.appflotal.presentation.ui.weather.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CieloPalette {
    @Composable
    @ReadOnlyComposable
    fun accent() = MaterialTheme.colorScheme.primary

    @Composable
    @ReadOnlyComposable
    fun text() = MaterialTheme.colorScheme.onSurface

    @Composable
    @ReadOnlyComposable
    fun textDim() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    @ReadOnlyComposable
    fun textFaint() = MaterialTheme.colorScheme.outline

    @Composable
    @ReadOnlyComposable
    fun chipBg() = MaterialTheme.colorScheme.surfaceVariant

    @Composable
    @ReadOnlyComposable
    fun divider() = MaterialTheme.colorScheme.outlineVariant

    @Composable
    @ReadOnlyComposable
    fun surfaceAlt() = MaterialTheme.colorScheme.surfaceContainer

    val Warm = Color(0xFFFF5252)
    val Cool = Color(0xFF448AFF)
    val Sun = Color(0xFFFFD600)
    val Alert = Color(0xFFFF6D00)

    val Aqi1 = Color(0xFF4CAF50)
    val Aqi2 = Color(0xFFFFEB3B)
    val Aqi3 = Color(0xFFFF9800)
    val Aqi4 = Color(0xFFF44336)
}

// ─── PANTALLA PRINCIPAL ──────────────────────────────────────

@Composable
fun WeatherRoute(
    onOpenForecast: (cityId: String) -> Unit,
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.weatherState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getCurrentWeather()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFF8FAFC), Color(0xFFF1F5F9)) // Fondo muy tenue
                )
            )
    ) {
        when (uiState.screenState) {
            is LoadState.Success -> {
                uiState.city?.let { city ->
                    WeatherScreen(city, onOpenForecast)
                }
            }

            LoadState.Loading -> LoadingDialog()
            else -> EmptyWeatherContent()
        }
    }
}

@Composable
private fun EmptyWeatherContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Sin datos disponibles.",
            color = CieloPalette.textDim(),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun WeatherScreen(
    city: City,
    onOpenForecast: (cityId: String) -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = { Header(city) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp) // Mucho aire entre secciones
        ) {
            HeroSection(city)
            HourlySection(city, onOpenForecast)
            DetailsGrid(city)
            SunAndAirSection(city)
        }
    }
}

// ─── SECCIÓN 1: HEADER (Minimalista) ─────────────────────────

@Composable
private fun Header(city: City) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.LocationOn, null, Modifier.size(16.dp), CieloPalette.accent())
                Spacer(Modifier.width(4.dp))
                Text(
                    city.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                getTodayLabel(),
                style = MaterialTheme.typography.labelMedium,
                color = CieloPalette.textFaint()
            )
        }

        IconButton(
            onClick = { /* Search */ },
            modifier = Modifier
                .background(Color.White, CircleShape)
                .size(40.dp)
        ) {
            Icon(Icons.Outlined.Search, null, modifier = Modifier.size(20.dp))
        }
    }
}

// ─── SECCIÓN 2: HERO (Sin caja, tipografía potente) ──────────

@Composable
private fun HeroSection(city: City) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "${city.temp}°",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 88.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = (-4).sp
                )
            )
            Text(
                text = city.condLabel,
                style = MaterialTheme.typography.headlineSmall,
                color = CieloPalette.textDim()
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LabelValue("Sensación", "${city.feels}°")
                LabelValue("Humedad", "${city.humidity}%")
            }
        }

        WeatherIcon(city.cond, size = 130.dp)
    }
}

// ─── SECCIÓN 3: PRONÓSTICO (LazyRow sin bordes) ──────────────

@Composable
private fun HourlySection(city: City, onMore: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "PRONÓSTICO HOY",
                style = MaterialTheme.typography.labelLarge,
                color = CieloPalette.textFaint()
            )
            TextButton(onClick = { onMore(city.id) }) {
                Text("7 días", fontWeight = FontWeight.Bold, color = CieloPalette.accent())
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(city.hourly.take(8)) { hour ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        hour.hour,
                        style = MaterialTheme.typography.labelMedium,
                        color = CieloPalette.textDim()
                    )
                    Spacer(Modifier.height(8.dp))
                    WeatherIcon(hour.cond, size = 32.dp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${hour.temp}°",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ─── SECCIÓN 4: DETALLES (Agrupados en una sola zona) ────────

@Composable
private fun DetailsGrid(city: City) {
    // Usamos una sola superficie sutil para agrupar todo, evitando "muchas cajas"
    Surface(
        modifier = Modifier.padding(horizontal = 24.dp),
        color = Color.White.copy(alpha = 0.5f),
        shape = RoundedCornerShape(32.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            val metrics = listOf(
                Triple("Viento", "${city.wind} km/h", Icons.Outlined.Air),
                Triple("Índice UV", city.uvLabel, Icons.Outlined.WbSunny),
                Triple("Visibilidad", "${city.vis.toInt()} km", Icons.Outlined.RemoveRedEye),
                Triple("Presión", "${city.pressure} hPa", Icons.Outlined.Speed)
            )

            metrics.chunked(2).forEachIndexed { index, pair ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    pair.forEach { (label, value, icon) ->
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(icon, null, Modifier.size(18.dp), CieloPalette.accent())
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CieloPalette.textFaint()
                                )
                                Text(
                                    value,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
                if (index == 0) Divider(color = Color.Black.copy(alpha = 0.05f))
            }
        }
    }
}

// ─── SECCIÓN 5: SOL Y AIRE (Simplificado) ────────────────────

@Composable
private fun SunAndAirSection(city: City) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Calidad del Aire (Caja sutil)
        Surface(
            Modifier.weight(1f),
            color = Color.White.copy(alpha = 0.5f),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "AIRE",
                    style = MaterialTheme.typography.labelSmall,
                    color = CieloPalette.textFaint()
                )
                Text(
                    city.aqiLabel,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                // Barra de AQI muy fina
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(Color.LightGray.copy(0.3f), CircleShape)
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(0.4f)
                            .fillMaxHeight()
                            .background(CieloPalette.accent(), CircleShape)
                    )
                }
            }
        }

        // Sol (Caja sutil)
        Surface(
            Modifier.weight(1f),
            color = Color.White.copy(alpha = 0.5f),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "OCASO",
                    style = MaterialTheme.typography.labelSmall,
                    color = CieloPalette.textFaint()
                )
                Text(
                    city.sunset,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Amanece ${city.sunrise}",
                    style = MaterialTheme.typography.labelSmall,
                    color = CieloPalette.textDim()
                )
            }
        }
    }
}

// ─── COMPONENTES ATÓMICOS ────────────────────────────────────

@Composable
fun LabelValue(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label ",
            style = MaterialTheme.typography.labelMedium,
            color = CieloPalette.textFaint()
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun WeatherIcon(cond: WeatherCondition, size: Dp) {
    val icon = when (cond) {
        WeatherCondition.Sunny -> Icons.Outlined.WbSunny
        WeatherCondition.Cloudy -> Icons.Outlined.Cloud
        WeatherCondition.Rainy -> Icons.Outlined.WaterDrop
        else -> Icons.Outlined.CloudQueue
    }
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (cond == WeatherCondition.Sunny) Color(0xFFFFB300) else CieloPalette.accent(),
        modifier = Modifier.size(size)
    )
}

private fun getTodayLabel(): String {
    val formatter = SimpleDateFormat("EEEE, d MMMM", Locale("es", "MX"))
    return formatter.format(Date()).replaceFirstChar { it.uppercase() }
}

@Preview(showBackground = true)
@Composable
fun WeatherScreenPreview() {
    val sampleCity = City(
        id = "1",
        name = "Monterrey",
        country = "México",
        tz = "CST",
        temp = 28,
        cond = WeatherCondition.Sunny,
        condLabel = "Despejado",
        hi = 32,
        lo = 22,
        feels = 30,
        rainChance = 10,
        wind = 15,
        windDir = "NE",
        aqi = 45,
        aqiLabel = "Buena",
        uv = 8,
        uvLabel = "Muy Alto",
        humidity = 60,
        dew = 18,
        pressure = 1015,
        vis = 16.0,
        sunrise = "06:15 AM",
        sunset = "07:45 PM",
        daylight = "13h 30m",
        pollen = "Bajo",
        hourly = listOf(
            HourlyForecast("08:00", WeatherCondition.Sunny, 24, 0),
            HourlyForecast("09:00", WeatherCondition.Sunny, 26, 0),
            HourlyForecast("10:00", WeatherCondition.Sunny, 28, 0),
            HourlyForecast("11:00", WeatherCondition.Sunny, 30, 0)
        )
    )

    val sampleUiState = WeatherUiState(
        city = sampleCity
    )

    HombreCamionTheme {
        WeatherScreen(
            onOpenForecast = {},
            city = sampleCity
        )
    }
}
