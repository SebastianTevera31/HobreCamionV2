package com.rfz.appflotal.presentation.ui.monitor.screen

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.tpms.PositionCoordinatesResponse
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.SensorAlerts

@Composable
fun DiagramaMonitorScreen(
    imageUrl: String,
    imageDimens: Pair<Int, Int>,
    currentWheel: String,
    temperature: Float,
    pressure: Float,
    timestamp: String?,
    temperatureStatus: SensorAlerts,
    pressionStatus: SensorAlerts,
    numWheels: Int,
    alertTires: Map<String, Boolean>,
    updateSelectedTire: (String) -> Unit,
    getSensorData: (String) -> Unit,
    coordinates: List<PositionCoordinatesResponse>?,
    modifier: Modifier = Modifier
) {
    var isLoading by remember { mutableStateOf(true) }
    var tireSelected by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    // Actualizar rueda
    tireSelected = currentWheel

    Column(
        modifier = modifier.verticalScroll(scrollState)
    ) {
        Box {
            val bitmap = loadBitmapFromUrl(imageUrl)

            if (bitmap != null && coordinates != null) {
                DiagramImage(
                    coordinates = coordinates,
                    image = bitmap,
                    alertTires = alertTires,
                    tireSelected = tireSelected,
                    width = imageDimens.first,
                    height = imageDimens.second
                )
                isLoading = false
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                Box(modifier = Modifier.size(520.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // Datos Sensor
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    PanelSensor(
                        wheel = currentWheel,
                        temperature = temperature,
                        pressure = pressure,
                        timestamp = timestamp,
                        temperatureStatus = temperatureStatus,
                        pressureStatus = pressionStatus,
                        modifier = Modifier
                            .height(320.dp)
                            .padding(bottom = dimensionResource(R.dimen.small_dimen))
                            .weight(1f)
                    )
                    PanelLlantas(
                        numWheels = numWheels,
                        wheelsWithAlert = alertTires,
                        tireSelected = tireSelected,
                        updateSelectedTire = { updateSelectedTire(it) },
                        modifier = Modifier
                            .height(320.dp)
                            .padding(bottom = dimensionResource(R.dimen.small_dimen))
                            .weight(1f)
                    ) { sensorId ->
                        tireSelected = sensorId
                        getSensorData(sensorId)
                    }
                }
            }
        }
    }
}

@Composable
fun PanelLlantas(
    numWheels: Int,
    wheelsWithAlert: Map<String, Boolean>,
    tireSelected: String,
    updateSelectedTire: (String) -> Unit,
    modifier: Modifier = Modifier,
    getSensorData: (String) -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(Color.White),
        modifier = modifier,
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = pluralStringResource(R.plurals.llanta_tag, 2),
                color = Color("#2E3192".toColorInt()),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(numWheels) {
                    val colorStatus =
                        if (wheelsWithAlert["P${it + 1}"] == true) Pair(Color.Red, Color.White)
                        else Pair(Color(0x402E3192), Color.Black)

                    val border = if (tireSelected == "P${it + 1}") {
                        BorderStroke(width = 4.dp, color = MaterialTheme.colorScheme.primary)
                    } else null

                    Button(
                        onClick = {
                            val tire = if ("P${it + 1}" == tireSelected) "" else {
                                getSensorData("P${it + 1}")
                                "P${it + 1}"
                            }
                            updateSelectedTire(tire)
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(colorStatus.first),
                        border = border,
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = "P${it + 1}",
                            color = colorStatus.second,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Clip,
                            softWrap = false,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CeldaDatosSensor(
    title: String,
    @DrawableRes img: Int,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x402E3192))
            .padding(4.dp)
            .height(44.dp)
    ) {
        Image(
            painter = painterResource(img),
            colorFilter = ColorFilter.tint(color = Color("#2E3192".toColorInt())),
            contentDescription = null,
            modifier = Modifier
                .weight(1f)
                .size(28.dp)
        )
        Column(
            modifier = Modifier.weight(2f)
        ) {
            Text(title, fontSize = 12.sp, color = Color("#3C3C3C".toColorInt()))
            Text(value, fontSize = 16.sp, color = Color("#3C3C3C".toColorInt()))
        }
    }
}

@Composable
fun CeldaAlerta(wheel: String, alertMessage: String, modifier: Modifier = Modifier) {
    Text(
        text = "$wheel: $alertMessage",
        style = MaterialTheme.typography.bodySmall,
        color = Color.White,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Red)
            .padding(4.dp)
            .fillMaxWidth()
    )
}

@Composable
fun PanelSensor(
    wheel: String,
    temperature: Float,
    pressure: Float,
    timestamp: String?,
    temperatureStatus: SensorAlerts,
    pressureStatus: SensorAlerts,
    modifier: Modifier = Modifier
) {
    val isTempAlert = temperatureStatus == SensorAlerts.HIGH_TEMPERATURE
    val isPressureAlert = pressureStatus != SensorAlerts.NO_DATA

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier
    ) {
        Card(
            colors = CardDefaults.cardColors(Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .weight(3.1f),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = if (wheel.isNotEmpty()) Arrangement.Top else Arrangement.Center
            ) {
                if (wheel.isNotEmpty()) {
                    Text(
                        text = pluralStringResource(R.plurals.llanta_tag, 1, wheel),
                        color = Color("#2E3192".toColorInt()),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = if (!timestamp.isNullOrEmpty()) stringResource(
                            R.string.actualizado,
                            timestamp
                        ) else "N/A",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState())
                    ) {
                        CeldaDatosSensor(
                            title = stringResource(R.string.temperatura),
                            img = R.drawable.temperature__1_,
                            value = "${temperature.toInt()} ℃"
                        )
                        CeldaDatosSensor(
                            title = stringResource(R.string.presion),
                            img = R.drawable.tire_pressure,
                            value = "${pressure.toInt()} PSI"
                        )
//                    CeldaDatosSensor(title = "Profundidad", value = "12mm")
                    }
                } else {
                    Text(
                        text = stringResource(R.string.seleccione_una_llanta),
                        color = Color("#2E3192".toColorInt()),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(
                            dimensionResource(R.dimen.medium_dimen)
                        )
                    )
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isTempAlert || isPressureAlert) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.alertas_activas),
                            color = Color("#2E3192".toColorInt()),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            if (temperatureStatus == SensorAlerts.HIGH_TEMPERATURE) {
                                CeldaAlerta(wheel, stringResource(temperatureStatus.message))
                            }

                            if (pressureStatus != SensorAlerts.NO_DATA) {
                                CeldaAlerta(wheel, stringResource(pressureStatus.message))
                            }
                        }
                    }
                } else {
                    Text(
                        text = stringResource(R.string.sin_alertas),
                        color = Color("#2E3192".toColorInt()),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(
                            dimensionResource(R.dimen.medium_dimen)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun loadBitmapFromUrl(imageUrl: String): Bitmap? {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(imageUrl) {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .build()

        val result = (loader.execute(request) as? SuccessResult)?.drawable
        bitmap = (result as? BitmapDrawable)?.bitmap
    }

    return bitmap
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun DiagramaMonitorScreenPreview() {
    HombreCamionTheme {
        DiagramaMonitorScreen(
            imageUrl = "https://truckdriverapi.azurewebsites.net/Base32.png",
            currentWheel = "7",
            temperature = 54.0f,
            pressure = 39f,
            timestamp = "",
            temperatureStatus = SensorAlerts.HIGH_TEMPERATURE,
            pressionStatus = SensorAlerts.HIGH_PRESSURE,
            numWheels = 7,
            alertTires = emptyMap(),
            getSensorData = {},
            updateSelectedTire = {},
            coordinates = emptyList(),
            modifier = Modifier.safeContentPadding(),
            imageDimens = Pair(1, 1)
        )
    }
}