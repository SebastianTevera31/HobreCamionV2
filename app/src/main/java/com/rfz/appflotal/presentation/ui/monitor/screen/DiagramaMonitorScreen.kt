package com.rfz.appflotal.presentation.ui.monitor.screen

import android.R.attr.visible
import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import com.rfz.appflotal.R
import com.rfz.appflotal.data.repository.bluetooth.BluetoothSignalQuality
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.monitor.component.BluetoothSnackBanner
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.SensorAlerts
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.Tire

@Composable
fun DiagramaMonitorScreen(
    image: Bitmap?,
    imageDimens: Pair<Int, Int>,
    currentWheel: String,
    temperature: Float,
    pressure: Float,
    timestamp: String?,
    temperatureStatus: SensorAlerts,
    batteryStatus: SensorAlerts,
    pressionStatus: SensorAlerts,
    ponchaduraStatus: SensorAlerts,
    updateSelectedTire: (String) -> Unit,
    getSensorData: (String) -> Unit,
    tires: List<Tire>?,
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
            if (image != null && tires != null) {
                DiagramImage(
                    tires = tires,
                    image = image,
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
                        batteryStatus = batteryStatus,
                        ponchaduraStatus = ponchaduraStatus,
                        modifier = Modifier
                            .height(320.dp)
                            .padding(bottom = dimensionResource(R.dimen.small_dimen))
                            .weight(1f)
                    )
                    PanelLlantas(
                        tiresList = tires,
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
    tiresList: List<Tire>?,
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

            if (tiresList != null) {
                val filterTireList = tiresList.filter { it.isActive }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items = filterTireList, key = { tire -> tire.sensorPosition }) {
                        val colorStatus =
                            if (it.inAlert) Pair(Color.Red, Color.White)
                            else Pair(Color(0x402E3192), Color.Black)

                        val border = if (tireSelected == it.sensorPosition) {
                            BorderStroke(width = 4.dp, color = MaterialTheme.colorScheme.primary)
                        } else null

                        Button(
                            onClick = {
                                // LOGICA PARA MANEJO DE SELECCIONAR | DESELECCIONAR LLANTA
                                val tire = if (it.sensorPosition == tireSelected) "" else {
                                    getSensorData(it.sensorPosition)
                                    it.sensorPosition
                                }
                                updateSelectedTire(tire)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(colorStatus.first),
                            border = border,
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = it.sensorPosition,
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
    ponchaduraStatus: SensorAlerts,
    batteryStatus: SensorAlerts,
    modifier: Modifier = Modifier
) {
    val isTempAlert = temperatureStatus == SensorAlerts.HIGH_TEMPERATURE
    val isPressureAlert = pressureStatus != SensorAlerts.NO_DATA
    val isPonchadura = ponchaduraStatus != SensorAlerts.NO_DATA

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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(0.5f)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = pluralStringResource(R.plurals.llanta_tag, 1, wheel),
                                color = Color("#2E3192".toColorInt()),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = if (!timestamp.isNullOrEmpty()) {
                                    stringResource(
                                        R.string.actualizado,
                                        timestamp
                                    )
                                } else "N/A",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                lineHeight = 16.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                            )
                        }

                        BatteryAlertIcon(
                            batteryStatus = batteryStatus
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .weight(1f)
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
                .weight(1.6f),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (wheel.isNotEmpty()) {
                    if (isTempAlert || isPressureAlert || isPonchadura) {
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

                                if (ponchaduraStatus != SensorAlerts.NO_DATA) {
                                    CeldaAlerta(wheel, stringResource(ponchaduraStatus.message))
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
fun BatteryAlertIcon(batteryStatus: SensorAlerts, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val isAlert = batteryStatus == SensorAlerts.LOW_BATTERY

    Image(
        painter = if (isAlert) painterResource(R.drawable.dead_battery)
        else painterResource(R.drawable.full_battery),
        contentDescription = null,
        modifier = modifier
            .size(dimensionResource(R.dimen.huge_dimen))
            .alpha(if (isAlert) alpha else 1f)
    )
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun PanelSensorViewPreview() {
    HombreCamionTheme {
        PanelSensor(
            wheel = "P1",
            temperature = 40.0f,
            pressure = 3f,
            timestamp = "",
            temperatureStatus = SensorAlerts.HIGH_TEMPERATURE,
            pressureStatus = SensorAlerts.LOW_PRESSURE,
            batteryStatus = SensorAlerts.NO_DATA,
            ponchaduraStatus = SensorAlerts.NO_DATA,
            modifier = Modifier
                .safeDrawingPadding()
                .height(320.dp)
                .padding(bottom = dimensionResource(R.dimen.small_dimen))
        )
    }
}