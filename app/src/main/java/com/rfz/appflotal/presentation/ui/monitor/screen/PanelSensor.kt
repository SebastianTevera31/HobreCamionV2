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
import com.rfz.appflotal.domain.sincronizacion.BloqueoInspeccion
import com.rfz.appflotal.presentation.ui.monitor.component.MonitorMenuButton
import com.rfz.appflotal.presentation.ui.monitor.component.MotivoBloqueoInspeccion
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.components.IndicadorSincronizacionLlanta
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.model.AvisoLlantaUi
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorTire
import com.rfz.appflotal.data.model.tpms.SensorAlerts
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.message
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.TireUiState
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.VOID_DATE

@Composable
fun PanelSensor(
    paymentPlan: PaymentPlanType,
    isAssembled: Boolean,
    bloqueoInspeccion: BloqueoInspeccion?,
    wheel: String,
    temperature: String,
    pressureUnit: String,
    pressure: String,
    timestamp: String?,
    temperatureStatus: SensorAlerts,
    pressureStatus: SensorAlerts,
    flatTireStatus: SensorAlerts,
    tireRemovingStatus: SensorAlerts,
    batteryStatus: SensorAlerts,
    onInspectClick: () -> Unit,
    onAssemblyClick: () -> Unit,
    onSwitchTempUnit: () -> Unit,
    onSwitchPressureUnit: () -> Unit,
    onDisassemblyClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    avisoSincronizacion: AvisoLlantaUi? = null,
    onVerSincronizacion: () -> Unit = {},
) {
    // Con un rechazo en la posición, una operación nueva quedaría bloqueada detrás de él.
    val operacionesHabilitadas = avisoSincronizacion?.conError != true
    val (tirePanelWeight, alertPanelWeight) =
        if (paymentPlan == PaymentPlanType.Complete) 0.65f to 0.35f
        else 1f to 1f


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

    // Con llanta seleccionada la tarjeta toma la altura de su contenido para que las
    // mediciones y los botones se vean completos; la tarjeta de alertas usa el espacio restante
    val wrapSensorCard = wheel.isNotEmpty() && !isLoading

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier   // aquí ya viene con height(panelDimension)
    ) {
        // TARJETA SUPERIOR: DATOS DEL SENSOR
        Card(
            colors = CardDefaults.cardColors(Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .then(if (wrapSensorCard) Modifier else Modifier.weight(tirePanelWeight)),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .then(if (wrapSensorCard) Modifier.fillMaxWidth() else Modifier.fillMaxSize())
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = if (wheel.isNotEmpty()) Arrangement.Top else Arrangement.Center
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp
                        )
                    }
                } else if (wheel.isNotEmpty()) {
                    // Encabezado: llanta + batería
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = pluralStringResource(R.plurals.llanta_tag, 1, wheel),
                            color = Color("#2E3192".toColorInt()),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )

                        if (avisoSincronizacion != null) {
                            IndicadorSincronizacionLlanta(
                                posicion = wheel,
                                aviso = avisoSincronizacion,
                                onVerSincronizacion = onVerSincronizacion
                            )
                        }

                        BatteryAlertIcon(
                            batteryStatus = batteryStatus
                        )
                    }

                    // Fecha a ancho completo para evitar que se parta en 3 líneas junto a la batería
                    Text(
                        text = if (!timestamp.isNullOrEmpty() && !isVoidDate(timestamp)) {
                            stringResource(
                                R.string.actualizado,
                                timestamp
                            )
                        } else "N/A",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    // weight(fill = false): solo si el contenido no cabe (p. ej. fuente del sistema muy grande)
                    // los datos ceden espacio y hacen scroll, para que los botones no se recorten
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .padding(dimensionResource(R.dimen.thin_dimen))
                            .verticalScroll(rememberScrollState())
                    ) {
                        CeldaDatosSensor(
                            title = stringResource(R.string.temperatura),
                            img = R.drawable.temperature__1_,
                            value = temperature,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onSwitchTempUnit
                        )

                        CeldaDatosSensor(
                            title = stringResource(R.string.presion),
                            img = R.drawable.tire_pressure,
                            value = "$pressure $pressureUnit",
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onSwitchPressureUnit
                        )
                    }

                    if (paymentPlan == PaymentPlanType.Complete) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            if (isAssembled) {
                                if (bloqueoInspeccion != null) {
                                    MotivoBloqueoInspeccion(bloqueoInspeccion)
                                } else {
                                    MonitorMenuButton(
                                        text = stringResource(R.string.inspeccionar),
                                        enabled = operacionesHabilitadas,
                                        onClick = onInspectClick
                                    )
                                }
                                MonitorMenuButton(
                                    text = stringResource(R.string.desmontar),
                                    enabled = operacionesHabilitadas,
                                    onClick = { onDisassemblyClick() }
                                )
                            } else {
                                MonitorMenuButton(
                                    text = stringResource(R.string.montar),
                                    enabled = operacionesHabilitadas,
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
                .weight(alertPanelWeight),
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
@Preview(showBackground = true, showSystemUi = true)
fun PanelSensorViewPreview() {
    HombreCamionTheme {
        PanelSensor(
            paymentPlan = PaymentPlanType.Complete,
            wheel = "P1",
            temperature = "40 C",
            pressureUnit = "PSI",
            pressure = "3",
            timestamp = VOID_DATE,
            bloqueoInspeccion = BloqueoInspeccion.INSPECCIONADA_HOY,
            isAssembled = true,
            temperatureStatus = SensorAlerts.HIGH_TEMPERATURE,
            pressureStatus = SensorAlerts.LOW_PRESSURE,
            batteryStatus = SensorAlerts.NO_DATA,
            flatTireStatus = SensorAlerts.NO_DATA,
            tireRemovingStatus = SensorAlerts.REMOVAL,
            onInspectClick = {},
            onAssemblyClick = {},
            onSwitchTempUnit = {},
            onSwitchPressureUnit = {},
            onDisassemblyClick = {},
            modifier = Modifier
                .safeDrawingPadding()
                .height(460.dp)
                .padding(bottom = dimensionResource(R.dimen.small_dimen))
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PanelSensorPreview() {
    HombreCamionTheme {
        PanelSensor(
            paymentPlan = PaymentPlanType.Complete,
            isAssembled = true,
            bloqueoInspeccion = null,
            wheel = "P2",
            temperature = "32 °C",
            pressureUnit = "PSI",
            pressure = "110",
            timestamp = "12/05/2024 10:30",
            temperatureStatus = SensorAlerts.NO_DATA,
            pressureStatus = SensorAlerts.NO_DATA,
            flatTireStatus = SensorAlerts.NO_DATA,
            tireRemovingStatus = SensorAlerts.NO_DATA,
            batteryStatus = SensorAlerts.NO_DATA,
            onInspectClick = {},
            onAssemblyClick = {},
            onSwitchTempUnit = {},
            onSwitchPressureUnit = {},
            onDisassemblyClick = {},
            modifier = Modifier
                .height(460.dp)
                .padding(8.dp)
        )
    }
}