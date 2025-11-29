package com.rfz.appflotal.presentation.ui.monitor.screen

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import com.rfz.appflotal.presentation.ui.monitor.component.MonitorMenuButton
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorTire
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.SensorAlerts
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.TireUiState

@Composable
fun DiagramaMonitorScreen(
    paymentPlan: PaymentPlanType,
    isInspectionAvailable: Boolean,
    tireUiState: TireUiState,
    image: Bitmap?,
    imageDimens: Pair<Int, Int>,
    updateSelectedTire: (String) -> Unit,
    getSensorData: (String) -> Unit,
    onInspectClick: (tire: String, temperature: Float, pressure: Float) -> Unit,
    onAssemblyClick: (tire: String) -> Unit,
    onDisassemblyClick: (tire: String, temperature: Float, pressure: Float) -> Unit,
    tires: List<MonitorTire>?,
    modifier: Modifier = Modifier
) {
    var isLoading by remember { mutableStateOf(true) }
    var tireSelected by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val panelDimension = 380.dp

    // Actualizar rueda
    tireSelected = tireUiState.currentTire

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

        Spacer(Modifier.height(8.dp))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .height(panelDimension)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .height(panelDimension)
                    .fillMaxWidth()
            ) {
                PanelSensor(
                    paymentPlan = paymentPlan,
                    isAssembled = tireUiState.isAssembled,
                    isInspectionAvailable = isInspectionAvailable,
                    wheel = tireUiState.currentTire,
                    temperature = tireUiState.temperature.first,
                    pressure = tireUiState.pressure.first,
                    timestamp = tireUiState.timestamp,
                    temperatureStatus = tireUiState.temperature.second,
                    pressureStatus = tireUiState.pressure.second,
                    batteryStatus = tireUiState.batteryStatus,
                    flatTireStatus = tireUiState.flatTireStatus,
                    tireRemovingStatus = tireUiState.tireRemovingStatus,
                    onInspectClick = {
                        onInspectClick(
                            tireUiState.currentTire,
                            tireUiState.temperature.first,
                            tireUiState.pressure.first
                        )
                    },
                    onAssemblyClick = { onAssemblyClick(tireUiState.currentTire) },
                    onDisassemblyClick = onDisassemblyClick,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )

                PanelLlantas(
                    tiresList = tires,
                    tireSelected = tireSelected,
                    updateSelectedTire = updateSelectedTire,
                    getSensorData = getSensorData,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Composable
fun PanelLlantas(
    tiresList: List<MonitorTire>?,
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
        if (tiresList != null) {
            val filterTireList = tiresList.filter { it.isActive }
            if (filterTireList.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.sin_llantas),
                        color = Color("#2E3192".toColorInt()),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            } else {
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
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        items(items = filterTireList, key = { tire -> tire.sensorPosition }) {
                            val colorStatus =
                                if (it.inAlert) Pair(Color.Red, Color.White)
                                else Pair(Color(0x402E3192), Color.Black)

                            val border = if (tireSelected == it.sensorPosition) {
                                BorderStroke(
                                    width = 4.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
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
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.sin_llantas),
                    color = Color("#2E3192".toColorInt()),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
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
    paymentPlan: PaymentPlanType,
    isInspectionAvailable: Boolean,
    isAssembled: Boolean,
    wheel: String,
    temperature: Float,
    pressure: Float,
    timestamp: String?,
    temperatureStatus: SensorAlerts,
    pressureStatus: SensorAlerts,
    flatTireStatus: SensorAlerts,
    tireRemovingStatus: SensorAlerts,
    batteryStatus: SensorAlerts,
    onInspectClick: () -> Unit,
    onAssemblyClick: () -> Unit,
    onDisassemblyClick: (tire: String, temperature: Float, pressure: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeAlerts =
        remember(temperatureStatus, pressureStatus, flatTireStatus, tireRemovingStatus) {
            buildList {
                if (temperatureStatus == SensorAlerts.HIGH_TEMPERATURE) {
                    add(temperatureStatus)
                }
                if (pressureStatus != SensorAlerts.NO_DATA) {
                    add(pressureStatus)
                }
                if (flatTireStatus != SensorAlerts.NO_DATA) {
                    add(flatTireStatus)
                }
                if (tireRemovingStatus != SensorAlerts.NO_DATA) {
                    add(tireRemovingStatus)
                }
            }
        }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier   // aquí ya viene con height(panelDimension)
    ) {
        // TARJETA SUPERIOR: DATOS DEL SENSOR
        Card(
            colors = CardDefaults.cardColors(Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.75f),
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
                    // Encabezado: llanta + batería
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
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
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        BatteryAlertIcon(
                            batteryStatus = batteryStatus
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 110.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        CeldaDatosSensor(
                            title = stringResource(R.string.temperatura),
                            img = R.drawable.temperature__1_,
                            value = "${temperature.toInt()} ℃",
                            modifier = Modifier.fillMaxWidth()
                        )

                        CeldaDatosSensor(
                            title = stringResource(R.string.presion),
                            img = R.drawable.tire_pressure,
                            value = "${pressure.toInt()} PSI",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (paymentPlan == PaymentPlanType.Complete) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            if (isAssembled) {
                                if (isInspectionAvailable) {
                                    MonitorMenuButton(
                                        text = stringResource(R.string.inspeccionar),
                                        onClick = onInspectClick
                                    )
                                }
                                MonitorMenuButton(
                                    text = stringResource(R.string.desmontar),
                                    onClick = { onDisassemblyClick(wheel, temperature, pressure) }
                                )
                            } else {
                                MonitorMenuButton(
                                    text = stringResource(R.string.montar),
                                    onClick = onAssemblyClick
                                )
                            }
                        }
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

        // TARJETA INFERIOR: ALERTAS
        Card(
            colors = CardDefaults.cardColors(Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.25f),
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
                    if (activeAlerts.isNotEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val tireRemovingAlert = activeAlerts.find { it == SensorAlerts.REMOVAL }

                            Text(
                                text = stringResource(R.string.alertas_activas),
                                color = Color("#2E3192".toColorInt()),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (tireRemovingAlert != null) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Red)
                                ) {
                                    CeldaAlerta(
                                        wheel,
                                        stringResource(tireRemovingAlert.message),
                                    )
                                }
                            } else {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    activeAlerts.forEach { alerts ->
                                        CeldaAlerta(wheel, stringResource(alerts.message))
                                    }
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
            paymentPlan = PaymentPlanType.Complete,
            wheel = "P1",
            temperature = 40.0f,
            pressure = 3f,
            timestamp = "",
            isAssembled = true,
            temperatureStatus = SensorAlerts.HIGH_TEMPERATURE,
            pressureStatus = SensorAlerts.LOW_PRESSURE,
            batteryStatus = SensorAlerts.NO_DATA,
            flatTireStatus = SensorAlerts.NO_DATA,
            tireRemovingStatus = SensorAlerts.REMOVAL,
            onInspectClick = {},
            onAssemblyClick = {},
            onDisassemblyClick = { _, _, _ -> },
            modifier = Modifier
                .safeDrawingPadding()
                .height(420.dp)
                .padding(bottom = dimensionResource(R.dimen.small_dimen)),
            isInspectionAvailable = false
        )
    }
}