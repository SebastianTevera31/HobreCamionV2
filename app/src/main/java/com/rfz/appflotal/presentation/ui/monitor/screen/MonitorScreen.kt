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
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.rfz.appflotal.R
import com.rfz.appflotal.data.repository.bluetooth.BluetoothSignalQuality
import com.rfz.appflotal.data.repository.bluetooth.MonitorDataFrame
import com.rfz.appflotal.data.repository.bluetooth.SensorAlertDataFrame
import com.rfz.appflotal.data.repository.bluetooth.decodeAlertDataFrame
import com.rfz.appflotal.data.repository.bluetooth.decodeDataFrame
import com.rfz.appflotal.presentation.theme.ProyectoFscSoftTheme
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorViewModel

@Composable
fun MonitorScreen(
    monitorViewModel: MonitorViewModel,
    modifier: Modifier = Modifier
) {
    val monitorUiState = monitorViewModel.monitorUiState.collectAsState()

    val sensorId = monitorUiState.value.sensorId

    Scaffold(topBar = {}) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            MonitorScreenView(
                wheel = monitorUiState.value.wheel,
                id = monitorUiState.value.sensorId,
                battery = monitorUiState.value.battery,
                pression = monitorUiState.value.pression.first,
                pressionStatus = monitorUiState.value.pression.second,
                temperature = monitorUiState.value.temperature.first,
                temperatureStatus = monitorUiState.value.temperature.second,
                qualityBluetooth = monitorUiState.value.signalIntensity.first,
                measuredBluetooth = monitorUiState.value.signalIntensity.second,
                timestamp = monitorViewModel.getCurrentRecordDate(),
                modifier = modifier.fillMaxSize()
            )
        }

    }
}

@Composable
fun MonitorScreenView(
    wheel: String,
    id: String,
    battery: String,
    pression: String,
    pressionStatus: String,
    temperature: String,
    temperatureStatus: String,
    qualityBluetooth: BluetoothSignalQuality,
    measuredBluetooth: String,
    timestamp: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color("#EDF0F8".toColorInt()))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            "Unidad 001",
            color = Color.Black,
            style = MaterialTheme.typography.displayLarge,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Box {
            Image(
                painter = painterResource(R.drawable.chasis_example),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .height(250.dp)
                    .fillMaxWidth()
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PanelSensor(modifier = Modifier.weight(1f))
            PanelLlantas(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun PanelSensor(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Card(
            colors = CardDefaults.cardColors(Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            ) {
                Text(
                    text = "Llanta P01",
                    color = Color("#2E3192".toColorInt()),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Actualizado: \n30/07/2025 17:10:20",
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
                    CeldaDatosSensor(title = "Temperatura", value = "45 C")
                    CeldaDatosSensor(title = "Presion", value = "71 PSI")
                    CeldaDatosSensor(title = "Profundidad", value = "12mm")
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
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
                    CeldaAlerta()
                    CeldaAlerta()
                    CeldaAlerta()
                }
            }
        }
    }
}

@Composable
fun PanelLlantas(modifier: Modifier = Modifier) {
    Card(colors = CardDefaults.cardColors(Color.White), modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "Llantas",
                color = Color("#2E3192".toColorInt()),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )
            val wheels = Array(18) { it + 1 }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(wheels) {
                    Button(
                        onClick = {},
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(Color(0x402E3192)),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = "P$it",
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
fun CeldaAlerta(modifier: Modifier = Modifier) {
    Text(
        text = "P07: Temperatura alta",
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
    val sensorId = decodeDataFrame(data, MonitorDataFrame.SENSOR_ID)
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
            sensorId,
            battery,
            pressionValue,
            pressionStatus,
            temperatureValue,
            temperatureStatus,
            calidadBluetooth,
            measuredBluetooth,
            timestamp = "21 de Julio de 2025 | hora: 14:20:00",
            modifier = Modifier.fillMaxSize()
        )
    }
}