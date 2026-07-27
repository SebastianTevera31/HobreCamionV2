package com.rfz.appflotal.presentation.ui.weather.view

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.CloudQueue
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.outlined.WbTwilight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.AppLocale
import com.rfz.appflotal.domain.weather.City
import com.rfz.appflotal.domain.weather.HourlyForecast
import com.rfz.appflotal.domain.weather.WeatherCondition
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.utils.LoadState
import com.rfz.appflotal.presentation.ui.vialstatus.view.CancellableLoadingDialog
import com.rfz.appflotal.presentation.ui.weather.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.Date

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
}

@Composable
fun WeatherRoute(
    onBack: () -> Unit,
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
                    WeatherScreen(city, onBack)
                }
            }

            LoadState.Loading -> CancellableLoadingDialog(onCancel = onBack)
            is LoadState.Error -> EmptyWeatherContent(onBack = onBack)
            else -> Unit
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmptyWeatherContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            TopAppBar(
                title = {
                    Column(verticalArrangement = Arrangement.Center) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = CieloPalette.accent()
                            )

                            Spacer(Modifier.width(4.dp))

                            Text(
                                text = "Clima",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = getTodayLabel(),
                            style = MaterialTheme.typography.labelMedium,
                            color = CieloPalette.textFaint()
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBackIosNew,
                            contentDescription = stringResource(R.string.regresar),
                            modifier = Modifier.size(Dimens.PaddingLarge),
                            tint = CieloPalette.accent()
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF8FAFC),
                    scrolledContainerColor = Color(0xFFF8FAFC),
                    navigationIconContentColor = CieloPalette.accent(),
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.PaddingLarge),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.weather_no_data),
                color = CieloPalette.textDim(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun WeatherScreen(
    city: City,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            Header(city, onBack)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                // Primero respeta el espacio del Scaffold: TopAppBar, status bar, etc.
                .padding(innerPadding)
                // Después haces scroll sobre el área útil
                .verticalScroll(rememberScrollState())
                // Y al final agregas el margen visual interno
                .padding(horizontal = Dimens.PaddingLarge, vertical = Dimens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
        ) {
            HeroSection(city)

            WeatherSection(title = stringResource(R.string.weather_forecast_today)) {
                HourlySection(city)
            }

            DetailsGrid(city)

            SunAndAirSection(city)
        }
    }
}

@Composable
fun WeatherSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = CieloPalette.textFaint(),
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(city: City, onBack: () -> Unit) {
    TopAppBar(
        title = {
            Column(verticalArrangement = Arrangement.Center) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = CieloPalette.accent()
                    )

                    Spacer(Modifier.width(4.dp))

                    Text(
                        text = city.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = getTodayLabel(),
                    style = MaterialTheme.typography.labelMedium,
                    color = CieloPalette.textFaint()
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBackIosNew,
                    contentDescription = stringResource(R.string.regresar),
                    modifier = Modifier.size(20.dp),
                    tint = CieloPalette.accent()
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFF8FAFC),
            scrolledContainerColor = Color(0xFFF8FAFC),
            navigationIconContentColor = CieloPalette.accent(),
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
private fun HeroSection(city: City) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(32.dp),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(24.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${city.temp}°",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 80.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(MaterialTheme.colorScheme.primary.value) // O un azul oscuro
                        )
                    )
                    Text(
                        text = stringResource(city.condLabel),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                WeatherIcon(city.cond, size = 100.dp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LabelValue(stringResource(R.string.weather_feels_like), "${city.feels}°")
                LabelValue(stringResource(R.string.weather_humidity), "${city.humidity}%")
            }
        }
    }
}

@Composable
private fun HourlySection(city: City) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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

@Composable
private fun DetailsGrid(city: City) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.05f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val metrics = listOf(
                MetricData(
                    stringResource(R.string.weather_wind),
                    "${city.wind} km/h",
                    Icons.Outlined.Air
                ),
                MetricData(
                    stringResource(R.string.weather_uv),
                    stringResource(city.uvLabel),
                    Icons.Outlined.WbSunny
                ),
                MetricData(
                    stringResource(R.string.weather_visibility),
                    "${city.vis.toInt()} km",
                    Icons.Outlined.Visibility
                ),
                MetricData(
                    stringResource(R.string.presion),
                    "${city.pressure} hPa",
                    Icons.Outlined.Speed
                )
            )

            metrics.chunked(2).forEachIndexed { index, pair ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    pair.forEach { metric ->
                        Box(modifier = Modifier.weight(1f)) {
                            MetricTile(metric)
                        }
                    }
                }
                if (index == 0) HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = Color.Black.copy(alpha = 0.05f)
                )
            }
        }
    }
}

@Composable
private fun MetricTile(metric: MetricData) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            metric.icon,
            null,
            Modifier.size(20.dp),
            tint = CieloPalette.accent().copy(alpha = 0.8f)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                metric.label,
                style = MaterialTheme.typography.labelSmall,
                color = CieloPalette.textFaint()
            )
            Text(
                metric.value.ifBlank { "--" },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
private fun SunAndAirSection(city: City) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Calidad del Aire mejorada
        InfoCard(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.weather_air_quality),
            value = city.aqiLabel.ifBlank { stringResource(R.string.weather_normal) },
            icon = Icons.Outlined.Cloud
        ) {
            // Barra de progreso de AQI
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(Color(0xFFE2E8F0), CircleShape)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth(0.4f) // Dinámico basado en city.aqi
                        .fillMaxHeight()
                        .background(CieloPalette.accent(), CircleShape)
                )
            }
        }

        // Amanecer/Ocaso
        InfoCard(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.weather_sunset),
            value = city.sunset.ifBlank { "--:--" },
            icon = Icons.Outlined.WbTwilight
        ) {
            Text(
                text = if (city.sunrise.isNotBlank()) "${stringResource(R.string.weather_sunrise_prefix)}${city.sunrise}" else stringResource(
                    R.string.sin_datos
                ),
                style = MaterialTheme.typography.labelSmall,
                color = CieloPalette.textDim()
            )
        }
    }
}

@Composable
private fun InfoCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    footer: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        color = Color.White,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.05f))
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, Modifier.size(14.dp), CieloPalette.textFaint())
                Spacer(Modifier.width(6.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.labelSmall,
                    color = CieloPalette.textFaint(),
                    letterSpacing = 1.sp
                )
            }
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            footer()
        }
    }
}

@Composable
fun LabelValue(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label ",
            style = MaterialTheme.typography.labelSmall,
            color = CieloPalette.textFaint()
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
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
    val locale = AppLocale.currentLocale
    val formatter = SimpleDateFormat("EEEE, d MMMM", locale.value)
    return formatter.format(Date()).replaceFirstChar { it.uppercase() }
}

data class MetricData(val label: String, val value: String, val icon: ImageVector)

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
        condLabel = R.string.clearsky,
        hi = 32,
        lo = 22,
        feels = 30,
        rainChance = 10,
        wind = 15,
        windDir = "NE",
        aqi = 45,
        aqiLabel = "Buena",
        uv = 8,
        uvLabel = R.string.weather_uv_very_high,
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

    HombreCamionTheme {
        WeatherScreen(
            city = sampleCity,
            onBack = {}
        )
    }
}
