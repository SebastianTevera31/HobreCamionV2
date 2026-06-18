package com.rfz.appflotal.presentation.ui.weather

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rfz.appflotal.domain.weather.City
import com.rfz.appflotal.domain.weather.HourlyForecast
import com.rfz.appflotal.domain.weather.WeatherCondition
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

private const val TODAY_LABEL = "Hoy, 1 Jun"

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

@Composable
fun HomeScreen(
    onOpenForecast: (cityId: String) -> Unit,
) {
    val cities = SampleCities.all
    val pagerState = rememberPagerState(initialPage = 0) { cities.size }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) { page ->
                val city = cities[page]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 12.dp),
                ) {
                    Header(
                        city = city,
                        pageIndex = pagerState.currentPage,
                        pageCount = cities.size,
                        onSelectPage = { i ->
                            scope.launch {
                                pagerState.animateScrollToPage(i)
                            }
                        },
                    )
                    HeroCard(city)
                    ForecastCta(city) { onOpenForecast(city.id) }
                    MetricsSection(city)
                    SunAndAirRow(city)
                }
            }
            BottomNav(
                active = "home",
                onForecast = { onOpenForecast(cities[pagerState.currentPage].id) })
        }
    }
}

// ─── Header ──────────────────────────────────────────────────────
@Composable
private fun Header(
    city: City,
    pageIndex: Int,
    pageCount: Int,
    onSelectPage: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(top = 14.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = CieloPalette.accent(),
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        city.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = CieloPalette.text()
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        city.tz,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CieloPalette.textFaint(),
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
                Text(
                    "${city.country} · $TODAY_LABEL",
                    fontSize = 12.sp,
                    color = CieloPalette.textDim()
                )
            }
            IconChip {
                Icon(
                    Icons.Outlined.Search,
                    null,
                    tint = CieloPalette.text(),
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(6.dp))
            IconChip {
                Icon(
                    Icons.Outlined.MoreHoriz,
                    null,
                    tint = CieloPalette.text(),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        // Pager dots
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(pageCount) { i ->
                val active = i == pageIndex
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .height(6.dp)
                        .width(if (active) 20.dp else 6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (active) CieloPalette.accent() else CieloPalette.textFaint())
                        .clickable { onSelectPage(i) },
                )
            }
        }
    }
}

@Composable
private fun IconChip(
    onClick: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(CieloPalette.chipBg())
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { content() }
}

// ─── Hero ─────────────────────────────────────────────────────
@Composable
private fun HeroCard(city: City) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 4.dp),
        contentPadding = PaddingValues(18.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Big weather icon (top-right, slightly clipped)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 8.dp, y = (-8).dp)
            ) {
                WeatherIcon(city.cond, size = 150.dp)
            }
            Column(modifier = Modifier.fillMaxWidth()) {
                // "AHORA" pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100))
                        .background(CieloPalette.chipBg())
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(CieloPalette.accent()),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "AHORA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CieloPalette.accent(),
                        letterSpacing = 1.2.sp,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        "${city.temp}",
                        style = MaterialTheme.typography.displayLarge,
                        color = CieloPalette.text(),
                    )
                    Text(
                        "°",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Light,
                        color = CieloPalette.text(),
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                Text(
                    city.condLabel,
                    style = MaterialTheme.typography.titleMedium,
                    color = CieloPalette.text()
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color = CieloPalette.Warm,
                                    fontWeight = FontWeight.Bold
                                )
                            ) { append("▲") }
                            append(" ${city.hi}°")
                        },
                        fontSize = 13.sp,
                        color = CieloPalette.textDim(),
                    )
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color = CieloPalette.Cool,
                                    fontWeight = FontWeight.Bold
                                )
                            ) { append("▼") }
                            append(" ${city.lo}°")
                        },
                        fontSize = 13.sp,
                        color = CieloPalette.textDim(),
                    )
                    Text("·", color = CieloPalette.textFaint())
                    Text("Sensación ${city.feels}°", fontSize = 13.sp, color = CieloPalette.textDim())
                }
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Pill("${city.rainChance}%", leading = {
                        Icon(
                            Icons.Outlined.WaterDrop,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }, strong = true)
                    Pill("${city.wind} km/h ${city.windDir}", leading = {
                        Icon(
                            Icons.Outlined.Air,
                            null,
                            tint = CieloPalette.text(),
                            modifier = Modifier.size(12.dp)
                        )
                    })
                    Pill(city.aqiLabel, leading = {
                        Icon(
                            Icons.Outlined.Speed,
                            null,
                            tint = CieloPalette.text(),
                            modifier = Modifier.size(12.dp)
                        )
                    })
                }
            }
        }
    }
}

// ─── CTA Pronóstico ─────────────────────────────────────────
@Composable
private fun ForecastCta(city: City, onMore: () -> Unit) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(top = 10.dp),
        contentPadding = PaddingValues(14.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "PRONÓSTICO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CieloPalette.accent(),
                        letterSpacing = 1.sp
                    )
                    Text(
                        "Próximas horas y 7 días",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CieloPalette.text()
                    )
                }
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100))
                        .background(CieloPalette.accent())
                        .clickable(onClick = onMore)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Más detalle",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        Icons.Outlined.ChevronRight,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            // 4 preview hours
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                city.hourly.take(4).forEachIndexed { i, h ->
                    val isNow = i == 0
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isNow) CieloPalette.chipBg() else Color.Transparent)
                            .border(
                                width = if (isNow) 1.5.dp else 1.dp,
                                color = if (isNow) CieloPalette.accent() else CieloPalette.divider(),
                                shape = RoundedCornerShape(14.dp),
                            )
                            .clickable(onClick = onMore)
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            if (isNow) "AHORA" else h.hour,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isNow) CieloPalette.accent() else CieloPalette.textDim(),
                            letterSpacing = 0.4.sp,
                        )
                        WeatherIcon(h.cond, size = 28.dp)
                        Text(
                            "${h.temp}°",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = CieloPalette.text()
                        )
                        if (h.pop > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Outlined.WaterDrop,
                                    null,
                                    tint = CieloPalette.Cool,
                                    modifier = Modifier.size(9.dp)
                                )
                                Spacer(Modifier.width(2.dp))
                                Text(
                                    "${h.pop}%",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CieloPalette.Cool
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Métricas 2×3 ────────────────────────────────────────────
@Composable
private fun MetricsSection(city: City) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(top = 10.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "DETALLES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CieloPalette.text(),
                letterSpacing = 1.sp,
                modifier = Modifier.weight(1f),
            )
            Text("actualizado hace 4 min", fontSize = 11.sp, color = CieloPalette.textFaint())
        }
        Spacer(Modifier.height(4.dp))
        val rows = listOf(
            listOf(
                MetricSpec(
                    "Sensación",
                    "${city.feels}",
                    "°",
                    subOf(city.feels, city.temp),
                    Icons.Outlined.Thermostat,
                    CieloPalette.Warm
                ) { },
                MetricSpec(
                    "Humedad",
                    "${city.humidity}",
                    "%",
                    "Rocío ${city.dew}°",
                    Icons.Outlined.WaterDrop,
                    CieloPalette.Cool
                ) {
                    HumidityBar(city.humidity)
                },
                MetricSpec(
                    "Viento",
                    "${city.wind}",
                    "km/h",
                    "${city.windDir} · ráfagas ${city.wind + 8}",
                    Icons.Outlined.Air,
                    CieloPalette.accent()
                ) {
                    WindCompass(city.windDir)
                },
            ),
            listOf(
                MetricSpec(
                    "UV",
                    "${city.uv}",
                    "· ${city.uvLabel}",
                    "Máx. 14:00",
                    Icons.Outlined.WbSunny,
                    CieloPalette.Warm
                ) {
                    UvBar(city.uv)
                },
                MetricSpec(
                    "Presión",
                    "${city.pressure}",
                    "hPa",
                    if (city.pressure > 1013) "Alta · estable" else "Baja · cambio",
                    Icons.Outlined.Speed,
                    CieloPalette.accent()
                ) { },
                MetricSpec(
                    "Visibilidad", "${city.vis.toInt()}", "km",
                    when {
                        city.vis > 10 -> "Excelente"; city.vis > 5 -> "Moderada"; else -> "Reducida"
                    },
                    Icons.Outlined.RemoveRedEye, Color(0xFF9C27B0) // Accent2
                ) { },
            ),
        )
        rows.forEachIndexed { idx, row ->
            if (idx > 0) Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { spec ->
                    MetricCard(spec, Modifier.weight(1f))
                }
            }
        }
    }
}

private fun subOf(feels: Int, real: Int) = when {
    feels < real -> "Más frío"
    feels > real -> "Más cálido"
    else -> "Como real"
}

private data class MetricSpec(
    val label: String,
    val value: String,
    val unit: String,
    val sub: String,
    val icon: ImageVector,
    val accent: Color,
    val below: @Composable () -> Unit,
)

@Composable
private fun MetricCard(s: MetricSpec, modifier: Modifier = Modifier) {
    AppCard(modifier = modifier.heightIn(min = 94.dp), contentPadding = PaddingValues(14.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(s.icon, null, tint = s.accent, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    s.label.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CieloPalette.textDim(),
                    letterSpacing = 0.8.sp
                )
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    s.value,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Light,
                    color = CieloPalette.text()
                )
                if (s.unit.isNotEmpty()) {
                    Spacer(Modifier.width(4.dp))
                    Text(
                        s.unit,
                        fontSize = 12.sp,
                        color = CieloPalette.textDim(),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
            }
            Text(s.sub, fontSize = 11.sp, color = CieloPalette.textFaint())
            s.below()
        }
    }
}

@Composable
private fun HumidityBar(pct: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(CieloPalette.divider()),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(pct / 100f)
                .fillMaxHeight()
                .background(CieloPalette.Cool),
        )
    }
}

@Composable
private fun UvBar(uv: Int) {
    val pct = ((uv.coerceIn(0, 11)) / 11f).coerceIn(0f, 1f)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                Brush.horizontalGradient(
                    0f to CieloPalette.Cool,
                    0.33f to Color(0xFFFFB347),
                    0.66f to CieloPalette.Warm,
                    1f to CieloPalette.Alert,
                )
            ),
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val totalW = maxWidth
            Box(
                modifier = Modifier
                    .padding(start = totalW * pct - 4.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, CieloPalette.accent(), CircleShape),
            )
        }
    }
}

@Composable
private fun WindCompass(dir: String) {
    val angle = when (dir) {
        "N" -> 0f; "NE" -> 45f; "E" -> 90f; "SE" -> 135f
        "S" -> 180f; "SW" -> 225f; "W" -> 270f; "NW" -> 315f
        else -> 0f
    }
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .border(1.dp, CieloPalette.divider(), CircleShape)
            .rotate(angle),
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(width = 6.dp, height = 8.dp)
                .background(CieloPalette.accent()),
        )
    }
}

// ─── Sol + Calidad del aire ─────────────────────────────────
@Composable
private fun SunAndAirRow(city: City) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(top = 10.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        AppCard(modifier = Modifier.weight(1.2f), contentPadding = PaddingValues(14.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "SOL",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CieloPalette.accent(),
                    letterSpacing = 0.8.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SunArc(
                        progress = 0.42f,
                        modifier = Modifier.size(width = 110.dp, height = 60.dp)
                    )
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.height(60.dp)
                    ) {
                        SunRow(label = "AMANECE", value = city.sunrise)
                        SunRow(label = "ANOCHECE", value = city.sunset)
                    }
                }
                Text(
                    buildAnnotatedString {
                        append("Día de ")
                        withStyle(
                            SpanStyle(
                                color = CieloPalette.text(),
                                fontWeight = FontWeight.Bold
                            )
                        ) { append(city.daylight) }
                    },
                    fontSize = 10.sp, color = CieloPalette.textDim(),
                )
            }
        }
        AppCard(modifier = Modifier.weight(1f), contentPadding = PaddingValues(14.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "CALIDAD DEL AIRE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CieloPalette.accent(),
                    letterSpacing = 0.8.sp
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "${city.aqi}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Light,
                        color = CieloPalette.text()
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        city.aqiLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            city.aqi < 50 -> CieloPalette.Aqi1
                            city.aqi < 100 -> CieloPalette.Warm
                            else -> CieloPalette.Alert
                        },
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
                AqiBar(city.aqi)
                Text(
                    buildAnnotatedString {
                        append("Polen ")
                        withStyle(
                            SpanStyle(
                                color = CieloPalette.text(),
                                fontWeight = FontWeight.Bold
                            )
                        ) { append(city.pollen) }
                    },
                    fontSize = 10.sp, color = CieloPalette.textDim(),
                )
            }
        }
    }
}

@Composable
private fun SunRow(label: String, value: String) {
    Column {
        Text(
            label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = CieloPalette.textFaint(),
            letterSpacing = 0.6.sp
        )
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CieloPalette.text())
    }
}

@Composable
private fun SunArc(progress: Float, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val r = h
        val cx = w / 2f
        val cy = h
        // dashed full arc
        val fullPath = Path().apply {
            arcTo(
                rect = Rect(cx - r, cy - r, cx + r, cy + r),
                startAngleDegrees = 180f, sweepAngleDegrees = 180f, forceMoveTo = true,
            )
        }
        drawPath(
            fullPath,
            color = Color.LightGray.copy(alpha = 0.3f), // Use a generic light gray or divider
            style = Stroke(
                width = 1.4.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)),
            )
        )
        // progress arc
        val sweep = 180f * progress
        val progPath = Path().apply {
            arcTo(
                rect = Rect(cx - r, cy - r, cx + r, cy + r),
                startAngleDegrees = 180f, sweepAngleDegrees = sweep, forceMoveTo = true,
            )
        }
        drawPath(
            progPath,
            color = CieloPalette.Sun,
            style = Stroke(width = 2.4.dp.toPx()),
        )
        // sun dot
        val angle = Math.PI * (1 - progress)
        val px = cx + r * cos(angle).toFloat()
        val py = cy - r * sin(angle).toFloat()
        drawCircle(CieloPalette.Sun, 5.dp.toPx(), Offset(px, py))
        // base line
        drawLine(
            color = Color.LightGray.copy(alpha = 0.3f),
            start = Offset(cx - r, cy),
            end = Offset(cx + r, cy),
            strokeWidth = 1.dp.toPx(),
        )
    }
}

@Composable
private fun AqiBar(aqi: Int) {
    val pct = ((aqi.coerceIn(0, 200)) / 200f).coerceIn(0f, 1f)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(
                Brush.horizontalGradient(
                    0f to CieloPalette.Aqi1,
                    0.3f to CieloPalette.Aqi2,
                    0.6f to CieloPalette.Aqi3,
                    1f to CieloPalette.Aqi4,
                )
            ),
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val totalW = maxWidth
            Box(
                modifier = Modifier
                    .padding(start = (totalW * pct - 4.dp).coerceAtLeast(0.dp))
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, CieloPalette.accent(), CircleShape),
            )
        }
    }
}

// ─── Helper Components ──────────────────────────────────────

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable BoxScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}

@Composable
fun WeatherIcon(cond: WeatherCondition, size: Dp) {
    val icon = when (cond) {
        WeatherCondition.Sunny -> Icons.Outlined.WbSunny
        WeatherCondition.Cloudy -> Icons.Outlined.Cloud
        WeatherCondition.Rainy -> Icons.Outlined.WaterDrop
        WeatherCondition.Stormy -> Icons.Outlined.Thunderstorm
        WeatherCondition.PartlyCloudy -> Icons.Outlined.CloudQueue
    }
    val tint = when (cond) {
        WeatherCondition.Sunny -> CieloPalette.Sun
        else -> CieloPalette.accent()
    }
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(size)
    )
}

@Composable
fun Pill(
    text: String,
    leading: @Composable (() -> Unit)? = null,
    strong: Boolean = false
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(100))
            .background(if (strong) CieloPalette.accent() else CieloPalette.chipBg())
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        leading?.invoke()
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (strong) Color.White else CieloPalette.text()
        )
    }
}

// ─── Bottom nav ─────────────────────────────────────────────
@Composable
fun BottomNav(active: String, onHome: () -> Unit = {}, onForecast: () -> Unit = {}) {
    Surface(
        color = CieloPalette.surfaceAlt(),
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NavItem("Hoy", Icons.Outlined.WbSunny, active == "home", Modifier.weight(1f), onHome)
            NavItem(
                "Pronóstico",
                Icons.Outlined.ChevronRight,
                active == "forecast",
                Modifier.weight(1f),
                onForecast
            )
            NavItem("Mapa", Icons.Outlined.LocationOn, false, Modifier.weight(1f))
            NavItem("Ajustes", Icons.Outlined.MoreHoriz, false, Modifier.weight(1f))
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: ImageVector,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Box(
            modifier = Modifier
                .size(width = 52.dp, height = 30.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (active) CieloPalette.chipBg() else Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                null,
                tint = if (active) CieloPalette.accent() else CieloPalette.textDim(),
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            label,
            fontSize = 10.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
            color = if (active) CieloPalette.accent() else CieloPalette.textDim(),
        )
    }
}

// ─── Sample Data ────────────────────────────────────────────

object SampleCities {
    val all = listOf(
        City(
            id = "madrid",
            name = "Madrid",
            country = "España",
            tz = "GMT+2",
            temp = 28,
            cond = WeatherCondition.Sunny,
            condLabel = "Despejado",
            hi = 32,
            lo = 18,
            feels = 30,
            rainChance = 0,
            wind = 12,
            windDir = "NE",
            aqi = 42,
            aqiLabel = "Buena",
            uv = 8,
            uvLabel = "Muy Alto",
            humidity = 35,
            dew = 12,
            pressure = 1015,
            vis = 16.0,
            sunrise = "06:45",
            sunset = "21:32",
            daylight = "14h 47m",
            pollen = "Bajo",
            hourly = listOf(
                HourlyForecast("Ahora", WeatherCondition.Sunny, 28, 0),
                HourlyForecast("11:00", WeatherCondition.Sunny, 29, 0),
                HourlyForecast("12:00", WeatherCondition.Sunny, 31, 0),
                HourlyForecast("13:00", WeatherCondition.Sunny, 32, 0)
            )
        ),
        City(
            id = "london",
            name = "Londres",
            country = "Reino Unido",
            tz = "GMT+1",
            temp = 18,
            cond = WeatherCondition.PartlyCloudy,
            condLabel = "Parcialmente Nublado",
            hi = 22,
            lo = 14,
            feels = 17,
            rainChance = 20,
            wind = 15,
            windDir = "SW",
            aqi = 35,
            aqiLabel = "Excelente",
            uv = 4,
            uvLabel = "Moderado",
            humidity = 65,
            dew = 11,
            pressure = 1008,
            vis = 10.0,
            sunrise = "04:50",
            sunset = "21:15",
            daylight = "16h 25m",
            pollen = "Moderado",
            hourly = listOf(
                HourlyForecast("Ahora", WeatherCondition.PartlyCloudy, 18, 5),
                HourlyForecast("11:00", WeatherCondition.Cloudy, 19, 10),
                HourlyForecast("12:00", WeatherCondition.Rainy, 18, 40),
                HourlyForecast("13:00", WeatherCondition.PartlyCloudy, 20, 10)
            )
        )
    )
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun HomeScreenPreview() {
    HombreCamionTheme {
        HomeScreen(onOpenForecast = {})
    }
}
