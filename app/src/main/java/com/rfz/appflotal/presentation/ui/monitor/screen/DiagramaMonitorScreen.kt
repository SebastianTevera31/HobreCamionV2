package com.rfz.appflotal.presentation.ui.monitor.screen

import com.rfz.appflotal.core.util.Commons.isVoidDate
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
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
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.AvisoLlantaUi
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorTire
import com.rfz.appflotal.data.model.tpms.SensorAlerts
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.TireUiState
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.VOID_DATE

@Composable
fun DiagramaMonitorScreen(
    paymentPlan: PaymentPlanType,
    temperatureUnit: String,
    pressureUnit: String,
    tireUiState: TireUiState,
    image: Bitmap?,
    imageDimens: Pair<Int, Int>,
    updateSelectedTire: (String) -> Unit,
    getSensorData: (String) -> Unit,
    onInspectClick: (tire: String, temperature: Float, pressure: Float) -> Unit,
    onAssemblyClick: (tire: String) -> Unit,
    onDisassemblyClick: (tire: String, temperature: Float, pressure: Float) -> Unit,
    onSwitchTempUnit: () -> Unit,
    onSwitchPressureUnit: () -> Unit,
    tires: List<MonitorTire>?,
    modifier: Modifier = Modifier,
    avisosSincronizacion: Map<String, AvisoLlantaUi> = emptyMap(),
    onVerSincronizacion: () -> Unit = {},
) {
    var isLoading by remember { mutableStateOf(true) }
    var tireSelected by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val panelDimension = if (paymentPlan == PaymentPlanType.Complete)
        if (tireUiState.isAssembled && tireUiState.isInspectionAvailable) 460.dp
        else 380.dp
    else 400.dp

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
                    height = imageDimens.second,
                    insigniasSincronizacion = avisosSincronizacion.mapValues { it.value.conError }
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
                    wheel = tireUiState.currentTire,
                    isInspectionAvailable = tireUiState.isInspectionAvailable,
                    temperature = "${tireUiState.temperature.first} $temperatureUnit",
                    pressureUnit = pressureUnit,
                    pressure = "${tireUiState.pressure.first}",
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
                    onSwitchTempUnit = onSwitchTempUnit,
                    onSwitchPressureUnit = onSwitchPressureUnit,
                    onAssemblyClick = { onAssemblyClick(tireUiState.currentTire) },
                    onDisassemblyClick = {
                        onDisassemblyClick(
                            tireUiState.currentTire,
                            tireUiState.temperature.first,
                            tireUiState.pressure.first
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    isLoading = tireUiState.isLoading,
                    avisoSincronizacion = avisosSincronizacion[tireUiState.currentTire],
                    onVerSincronizacion = onVerSincronizacion
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
            if (tiresList.isEmpty()) {
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
                        items(items = tiresList, key = { tire -> tire.sensorPosition }) {
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
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x402E3192))
            .padding(4.dp)
            .heightIn(min = 44.dp)
    ) {
        Image(
            painter = painterResource(img),
            colorFilter = ColorFilter.tint(color = Color("#2E3192".toColorInt())),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .weight(0.8f)
                .size(height = 28.dp, width = 1.dp)
        )
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(2.5f)
        ) {
            Text(
                title,
                fontSize = 12.sp,
                color = Color("#3C3C3C".toColorInt()),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                value,
                fontSize = 16.sp,
                color = Color("#3C3C3C".toColorInt()),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (onClick != {}) {
            IconButton(onClick = onClick, modifier = Modifier.weight(0.8f)) {
                Icon(
                    imageVector = Icons.Default.SyncAlt,
                    contentDescription = null,
                    tint = Color("#2E3192".toColorInt())
                )
            }
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
@Preview(showBackground = true)
fun PanelLlantasPreview() {
    val sampleTires = listOf(
        MonitorTire(
            "P1",
            inAlert = false,
            isAssembled = true,
            isActive = true,
            xPosition = 100,
            yPosition = 100
        ),
        MonitorTire(
            "P2",
            inAlert = true,
            isAssembled = true,
            isActive = true,
            xPosition = 200,
            yPosition = 100
        ),
        MonitorTire(
            "P3",
            inAlert = false,
            isAssembled = false,
            isActive = true,
            xPosition = 300,
            yPosition = 100
        ),
        MonitorTire(
            "P4",
            inAlert = false,
            isAssembled = true,
            isActive = true,
            xPosition = 400,
            yPosition = 100
        )
    )
    HombreCamionTheme {
        PanelLlantas(
            tiresList = sampleTires,
            tireSelected = "P1",
            updateSelectedTire = {},
            getSensorData = {}
        )
    }
}
