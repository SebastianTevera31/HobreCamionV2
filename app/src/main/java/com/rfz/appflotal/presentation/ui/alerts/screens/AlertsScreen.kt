package com.rfz.appflotal.presentation.ui.alerts.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.BatteryAlert
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.alerts.viewmodel.AlertViewModel
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.components.AlertCard
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.AlertStatus
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.AlertUi
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.asIcon

@Composable
fun AlertsRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlertViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    AlertScreen(
        selectedAlert = uiState.selectedAlert,
        selectedDate = uiState.date,
        selectedWheel = uiState.selectedWheel,
        wheels = uiState.wheels,
        onBack = onBack,
        onApplyFilters = { date, wheel, alert ->
            viewModel.applyFilter(
                date = date,
                wheel = wheel,
                alert = alert
            )
        },
        modifier = modifier
    )
}

@Composable
fun AlertScreen(
    selectedAlert: AlertType,
    selectedDate: String,
    selectedWheel: String,
    wheels: List<String>,
    onBack: () -> Unit,
    onApplyFilters: (date: String, wheel: String, alert: AlertType) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilters by remember { mutableStateOf(false) }
    var date by remember { mutableStateOf(selectedDate) }
    var wheel by remember { mutableStateOf(selectedWheel) }
    var alert by remember { mutableStateOf(selectedAlert) }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SimpleTopBar(
                title = "Historial de Alertas",
                onBack = onBack,
                showBackButton = true,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(Dimens.PaddingMedium),
                verticalArrangement = Arrangement.spacedBy(Dimens.ListItemSpacing),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = Dimens.PaddingSmall),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(Dimens.PaddingMedium),
                            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.FilterList,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
                                    Text(
                                        text = "Filtros",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                IconButton(onClick = { showFilters = !showFilters }) {
                                    Icon(
                                        imageVector = if (showFilters) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = if (showFilters) "Colapsar" else "Expandir",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            if (showFilters) {
                                BoxWithConstraints {
                                    val isWide = maxWidth > 600.dp
                                    if (isWide) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
                                        ) {
                                            TireFilterField(
                                                selectedWheel = wheel,
                                                wheels = wheels,
                                                onSelectedWheel = { wheel = it },
                                                modifier = Modifier.weight(1f)
                                            )
                                            AlertTypeFilterField(
                                                selectedAlert = alert,
                                                onSelectAlert = { alert = it },
                                                modifier = Modifier.weight(1f)
                                            )
                                            DateFilterField(
                                                selectedDate = date,
                                                onDateSelected = { date = it },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    } else {
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
                                        ) {
                                            TireFilterField(
                                                selectedWheel = wheel,
                                                wheels = wheels,
                                                onSelectedWheel = { wheel = it }
                                            )
                                            AlertTypeFilterField(
                                                selectedAlert = alert,
                                                onSelectAlert = { alert = it }
                                            )
                                            DateFilterField(
                                                selectedDate = date,
                                                onDateSelected = { date = it }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.size(Dimens.PaddingSmall))

                                Button(
                                    onClick = {
                                        onApplyFilters(date, wheel, alert)
                                        showFilters = false
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text(
                                        text = "Aplicar Filtros",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                // Resumen de filtros aplicados cuando está colapsado
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
                                ) {
                                    FilterChipSummary(
                                        label = "Rueda",
                                        value = wheel.ifEmpty { "Todas" },
                                        modifier = Modifier.weight(1f)
                                    )
                                    FilterChipSummary(
                                        label = "Alerta",
                                        value = stringResource(alert.title),
                                        modifier = Modifier.weight(1f)
                                    )
                                    FilterChipSummary(
                                        label = "Fecha",
                                        value = date.ifEmpty { "Todas" },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = "Historial de alertas",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = Dimens.PaddingSmall)
                    )
                }

                items(sampleAlerts) { alert ->
                    AlertCard(alert)
                }
            }
        }
    }
}

@Composable
fun FilterChipSummary(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = Dimens.PaddingSmall, vertical = Dimens.PaddingExtraSmall),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1
        )
    }
}

val sampleAlerts = listOf(
    AlertUi(
        icon = Icons.Outlined.Warning.asIcon(),
        title = "Presión Crítica - Eje 1 Izq",
        detailLabel = "Presión:",
        detailValue = "2.1 bar",
        detailExtra = "(mín. 6.5)",
        status = AlertStatus.CRITICA
    ),
    AlertUi(
        icon = Icons.Outlined.Thermostat.asIcon(),
        title = "Alta Temperatura - Eje 2 Der",
        detailLabel = "Temp:",
        detailValue = "95°C",
        status = AlertStatus.CRITICA
    ),
    AlertUi(
        icon = Icons.Outlined.BatteryAlert.asIcon(),
        title = "Batería Baja Sensor",
        detailLabel = "Nivel:",
        detailValue = "15%",
        status = AlertStatus.PENDIENTE
    ),
    AlertUi(
        icon = Icons.Outlined.GpsFixed.asIcon(),
        title = "Desgaste de Piso Bajo",
        detailLabel = "Profundidad:",
        detailValue = "3.5 mm",
        status = AlertStatus.PENDIENTE
    ),
    AlertUi(
        icon = Icons.Outlined.Warning.asIcon(),
        title = "Fuga Rápida Detectada",
        detailLabel = "Pérdida:",
        detailValue = "0.5 bar/min",
        status = AlertStatus.CRITICA
    ),
    AlertUi(
        icon = Icons.Outlined.Timer.asIcon(),
        title = "Inspección Programada",
        detailLabel = "Vence en:",
        detailValue = "2 días",
        status = AlertStatus.PENDIENTE
    ),
    AlertUi(
        icon = Icons.Outlined.Speed.asIcon(),
        title = "Exceso de Velocidad",
        detailLabel = "Máx:",
        detailValue = "110 km/h",
        status = AlertStatus.PENDIENTE
    ),
    AlertUi(
        icon = Icons.Outlined.Warning.asIcon(),
        title = "Presión Alta - Remolque",
        detailLabel = "Presión:",
        detailValue = "9.2 bar",
        status = AlertStatus.CRITICA
    ),
    AlertUi(
        icon = Icons.Outlined.Thermostat.asIcon(),
        title = "Sobrecalentamiento Frenos",
        detailLabel = "Eje:",
        detailValue = "Trasero",
        status = AlertStatus.CRITICA
    ),
    AlertUi(
        icon = Icons.Outlined.GpsFixed.asIcon(),
        title = "Alineación Requerida",
        detailLabel = "Desviación:",
        detailValue = "Leve",
        status = AlertStatus.PENDIENTE
    )
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AlertsRoutePreview() {
    HombreCamionTheme {
        AlertScreen(
            selectedAlert = AlertType.PRESSURE,
            selectedDate = "01/09/2026",
            selectedWheel = "Eje 1 Izq",
            wheels = listOf("Todas", "Eje 1 Izq"),
            onBack = {},
            onApplyFilters = { _, _, _ -> }
        )
    }
}
