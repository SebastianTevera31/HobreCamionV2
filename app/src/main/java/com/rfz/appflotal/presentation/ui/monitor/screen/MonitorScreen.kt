package com.rfz.appflotal.presentation.ui.monitor.screen

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import coil.compose.AsyncImage
import com.rfz.appflotal.data.repository.bluetooth.BluetoothSignalQuality
import com.rfz.appflotal.data.repository.bluetooth.MonitorDataFrame
import com.rfz.appflotal.data.repository.bluetooth.SensorAlertDataFrame
import com.rfz.appflotal.data.repository.bluetooth.decodeAlertDataFrame
import com.rfz.appflotal.data.repository.bluetooth.decodeDataFrame
import com.rfz.appflotal.presentation.theme.ProyectoFscSoftTheme
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorViewModel
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.SensorAlerts

@Composable
fun MonitorScreen(
    monitorViewModel: MonitorViewModel, modifier: Modifier = Modifier
) {
    val monitorUiState = monitorViewModel.monitorUiState.collectAsState()

    Scaffold(topBar = {}) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            MonitorScreenView(
                wheel = monitorUiState.value.wheel,
                id = monitorUiState.value.monitorId,
                pressure = monitorUiState.value.pression.first,
                pressionStatus = monitorUiState.value.pression.second,
                temperature = monitorUiState.value.temperature.first,
                temperatureStatus = monitorUiState.value.temperature.second,
                timestamp = monitorViewModel.getCurrentRecordDate(),
                imageUrl = monitorUiState.value.chassisImageUrl,
                numWheels = monitorUiState.value.numWheels,
                getSensorData = { sensorId -> monitorViewModel.getSensorDataByWheel(sensorId) },
                modifier = modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun MonitorScreenView(
    wheel: String,
    id: Int,
    pressure: Float,
    pressionStatus: SensorAlerts,
    temperature: Float,
    temperatureStatus: SensorAlerts,
    timestamp: String?,
    imageUrl: String,
    numWheels: Int,
    getSensorData: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color("#EDF0F8".toColorInt()))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Cabecera
        Text(
            "Unidad 001",
            color = Color.Black,
            style = MaterialTheme.typography.displayLarge,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Box {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            )
        }

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
                modifier = Modifier.weight(1f)
            )
            PanelLlantas(numWheels = numWheels, Modifier.weight(1f)) { sensorId ->
                getSensorData(sensorId)
            }
        }
    }
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
                    text = "Llanta $wheel",
                    color = Color("#2E3192".toColorInt()),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = if (timestamp != null) "Actualizado: \n$timestamp" else "N/A",
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
                    CeldaDatosSensor(title = "Temperatura", value = "$temperature C")
                    CeldaDatosSensor(title = "Presion", value = "$pressure PSI")
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
                    text = "Alertas activas",
                    color = Color("#2E3192".toColorInt()),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    if (temperatureStatus == SensorAlerts.HighTemperature) {
                        CeldaAlerta(wheel, temperatureStatus.message)
                    }

                    if (pressureStatus != SensorAlerts.NoData) {
                        CeldaAlerta(wheel, pressureStatus.message)
                    }
                }
            }
        }
    }
}

@Composable
fun PanelLlantas(numWheels: Int, modifier: Modifier = Modifier, getSensorData: (String) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(Color.White), modifier = modifier.fillMaxSize(),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "Llantas",
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
                    Button(
                        onClick = { getSensorData("P${it + 1}") },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(Color(0x402E3192)),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = "P${it + 1}",
                            color = Color("#3C3C3C".toColorInt()),
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
        color = Color("#fbcbcb".toColorInt()),
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Red)
            .padding(4.dp)
            .fillMaxWidth()
    )
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MonitorScreenPreview() {
    val data = "aaa1410e630147e85e00124f08f4"
    val wheel = decodeDataFrame(data, MonitorDataFrame.POSITION_WHEEL)
    val battery = decodeAlertDataFrame(data, SensorAlertDataFrame.LOW_BATTERY)
    val pressionValue = decodeDataFrame(data, MonitorDataFrame.PRESSION)
    val pressionStatus = decodeAlertDataFrame(data, SensorAlertDataFrame.PRESSURE)
    val temperatureValue = decodeDataFrame(data, MonitorDataFrame.TEMPERATURE)
    val temperatureStatus = decodeAlertDataFrame(data, SensorAlertDataFrame.HIGH_TEMPERATURE)
    val calidadBluetooth = BluetoothSignalQuality.Pobre
    val measuredBluetooth = "-70 dBm"
    ProyectoFscSoftTheme {
        MonitorScreenView(
            wheel,
            10,
            0.0f,
            SensorAlerts.LowPressure,
            29f,
            SensorAlerts.HighTemperature,
            timestamp = "21/07/2025 14:20:00",
            "",
            10,
            {},
            modifier = Modifier.fillMaxSize()
        )
    }
}