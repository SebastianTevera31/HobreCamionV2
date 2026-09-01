package com.rfz.appflotal.presentation.ui.alerts.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatteryAlert
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
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
        onDateSelect = { viewModel.filterByFecha(it) },
        onSelectedWheel = { viewModel.filterByTire(it) },
        onAlertType = { viewModel.filterByAlert(it) },
        modifier = modifier
    )
}

@Composable
fun AlertScreen(
    selectedAlert: AlertType?,
    selectedDate: String,
    selectedWheel: String,
    wheels: List<String>,
    onBack: () -> Unit,
    onDateSelect: (String) -> Unit,
    onSelectedWheel: (String) -> Unit,
    onAlertType: (AlertType) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SimpleTopBar(
                title = "Alertas",
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
            Card(
                modifier = Modifier
                    .padding(Dimens.PaddingMedium)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.PaddingMedium),
                    verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
                ) {
                    Text(
                        text = "Filtrar por",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    TireFilterField(
                        selectedWheel = selectedWheel,
                        wheels = wheels,
                        onSelectedWheel = onSelectedWheel
                    )

                    AlertTypeFilterField(
                        selectedAlert = selectedAlert,
                        onSelectAlert = onAlertType
                    )

                    DateFilterField(
                        selectedDate = selectedDate,
                        onDateSelected = onDateSelect
                    )
                }
            }

            Text(
                text = "Historial de alertas",
                style = MaterialTheme.typography.titleMedium.copy(Color.Black),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    top = Dimens.PaddingMedium,
                    bottom = Dimens.PaddingSmall,
                    start = Dimens.PaddingMedium,
                    end = Dimens.PaddingMedium
                )
            )

            LazyColumn(
                contentPadding = PaddingValues(Dimens.PaddingMedium),
                verticalArrangement = Arrangement.spacedBy(Dimens.ListItemSpacing),
                modifier = Modifier.weight(1f)
            ) {
                items(sampleAlerts) { alert ->
                    AlertCard(alert)
                }
            }
        }
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
            onDateSelect = {},
            onSelectedWheel = {},
            onAlertType = {}
        )
    }
}
