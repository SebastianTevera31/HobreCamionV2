package com.rfz.appflotal.presentation.ui.monitor.screen

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sensors
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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
    wheel: String,
    temperature: Float,
    pressure: Float,
    timestamp: String?,
    temperatureStatus: SensorAlerts,
    pressionStatus: SensorAlerts,
    numWheels: Int,
    alertTires: Map<String, Boolean>,
    getSensorData: (String) -> Unit,
    coordinates: List<PositionCoordinatesResponse>?,
    modifier: Modifier = Modifier
) {
    var isLoading by remember { mutableStateOf(true) }
    var tireSelected by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    // Actualizar rueda
    tireSelected = wheel

    Column(modifier = modifier) {
        Box {
            val bitmap = loadBitmapFromUrl(imageUrl)

            if (bitmap != null && coordinates != null) {
                DiagramImage(
                    coordinates = coordinates,
                    image = bitmap,
                    alertTires = alertTires,
                    tireSelected = tireSelected
                )
                isLoading = false
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                Box(modifier = Modifier.size(520.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // Datos Sensor
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    PanelSensor(
                        wheel = wheel,
                        temperature = temperature,
                        pressure = pressure,
                        timestamp = timestamp,
                        temperatureStatus = temperatureStatus,
                        pressureStatus = pressionStatus,
                        modifier = Modifier
                            .weight(1f)
                            .height(400.dp)
                    )
                    PanelLlantas(
                        numWheels = numWheels, wheelsWithAlert = alertTires, Modifier.weight(1f)
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
    modifier: Modifier = Modifier,
    getSensorData: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(Color.White),
        modifier = modifier.height(200.dp),
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

                    Button(
                        onClick = { getSensorData("P${it + 1}") },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(colorStatus.first),
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
fun CeldaDatosSensor(title: String, value: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x402E3192))
            .padding(4.dp)
    ) {
        Image(
            Icons.Filled.Sensors,
            colorFilter = ColorFilter.tint(color = Color("#2E3192".toColorInt())),
            contentDescription = null,
            modifier = Modifier.weight(1f)
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
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier
    ) {
        Card(
            colors = CardDefaults.cardColors(Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .weight(3f),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            ) {
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
                        .fillMaxHeight()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    CeldaDatosSensor(
                        title = stringResource(R.string.temperatura),
                        value = "$temperature ℃"
                    )
                    CeldaDatosSensor(
                        title = stringResource(R.string.presion),
                        value = "$pressure PSI"
                    )
//                    CeldaDatosSensor(title = "Profundidad", value = "12mm")
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
            wheel = "7",
            temperature = 54.0f,
            pressure = 39f,
            timestamp = "",
            temperatureStatus = SensorAlerts.HIGH_TEMPERATURE,
            pressionStatus = SensorAlerts.HIGH_PRESSURE,
            numWheels = 7,
            alertTires = emptyMap(),
            getSensorData = { },
            coordinates = emptyList(),
            modifier = Modifier.safeContentPadding()
        )
    }
}