package com.rfz.appflotal.presentation.ui.alerts.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rfz.appflotal.R
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

    LaunchedEffect(Unit) {
        viewModel.getData()
    }

    AlertScreen(
        alerts = uiState.alerts,
        currentPage = uiState.currentPage,
        hasNextPage = uiState.hasNextPage,
        isLoading = uiState.isLoading,
        selectedAlert = uiState.selectedAlert,
        selectedDate = uiState.startDate,
        selectedWheel = uiState.selectedWheel,
        wheels = uiState.wheels,
        onBack = onBack,
        onApplyFilters = { startDate, endDate, wheel, alert ->
            viewModel.applyFilter(
                startDate = startDate,
                endDate = endDate,
                wheel = wheel,
                alert = alert
            )
        },
        onPageSelected = { page -> viewModel.goToPage(page) },
        modifier = modifier
    )
}

@Composable
fun AlertScreen(
    alerts: List<AlertUi>,
    currentPage: Int,
    hasNextPage: Boolean,
    isLoading: Boolean,
    selectedAlert: AlertType,
    selectedDate: String,
    selectedWheel: String,
    wheels: List<String>,
    onBack: () -> Unit,
    onApplyFilters: (startDate: String, endDate: String, wheel: String, alert: AlertType) -> Unit,
    onPageSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilters by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf(selectedDate) }
    var endDate by remember { mutableStateOf(selectedDate) }
    var wheel by remember { mutableStateOf(selectedWheel) }
    var alert by remember { mutableStateOf(selectedAlert) }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SimpleTopBar(
                title = stringResource(R.string.alertas_history_title),
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
                        shape = RoundedCornerShape(16.dp),
                        onClick = { showFilters = !showFilters }
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
                                        text = stringResource(R.string.filtros),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                IconButton(onClick = { showFilters = !showFilters }) {
                                    Icon(
                                        imageVector = if (showFilters) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = stringResource(
                                            if (showFilters) R.string.colapsar else R.string.expandir
                                        ),
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
                                                selectedDate = startDate,
                                                onDateSelected = { startDate = it },
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
                                                selectedDate = startDate,
                                                onDateSelected = { startDate = it }
                                            )
                                            DateFilterField(
                                                selectedDate = endDate,
                                                onDateSelected = { endDate = it }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.size(Dimens.PaddingSmall))

                                Button(
                                    onClick = {
                                        onApplyFilters(startDate, endDate, wheel, alert)
                                        showFilters = false
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text(
                                        text = stringResource(R.string.aplicar_filtros),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                // Resumen de filtros aplicados cuando está colapsado
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
                                    ) {
                                        FilterChipSummary(
                                            label = stringResource(R.string.rueda_label),
                                            value = wheel.ifEmpty { stringResource(R.string.todas) },
                                            modifier = Modifier.weight(1f)
                                        )
                                        FilterChipSummary(
                                            label = stringResource(R.string.alerta_label),
                                            value = stringResource(alert.title),
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
                                    ) {
                                        FilterChipSummary(
                                            label = stringResource(R.string.desde_label),
                                            value = startDate.ifEmpty { stringResource(R.string.todas) },
                                            modifier = Modifier.weight(1f)
                                        )
                                        FilterChipSummary(
                                            label = stringResource(R.string.hasta_label),
                                            value = endDate.ifEmpty { stringResource(R.string.todas) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        text = stringResource(R.string.alertas_history_section_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = Dimens.PaddingSmall)
                    )
                }

                items(alerts) { alertItem ->
                    AlertCard(alertItem)
                }

                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimens.PaddingMedium)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            PageNavigator(
                hasData = alerts.isNotEmpty(),
                currentPage = currentPage,
                hasNextPage = hasNextPage,
                isLoading = isLoading,
                onPageSelected = onPageSelected
            )
        }
    }
}

@Composable
private fun PageNavigator(
    hasData: Boolean,
    currentPage: Int,
    hasNextPage: Boolean,
    isLoading: Boolean,
    onPageSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Sin total de páginas confiable del servidor: solo mostramos la anterior,
    // la actual y la siguiente (si existe), en vez de una lista completa 1..N.
    val startPage = (currentPage - 1).coerceAtLeast(1)
    val endPage = if (hasNextPage) currentPage + 1 else currentPage.coerceAtLeast(1)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.PaddingMedium),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onPageSelected(currentPage - 1) },
            enabled = !isLoading && currentPage > 1 && hasData
        ) {
            Icon(
                Icons.Default.ChevronLeft,
                contentDescription = stringResource(R.string.pagina_anterior)
            )
        }

        (startPage..endPage).forEach { page ->
            PageChip(
                page = page,
                isSelected = page == currentPage,
                enabled = !isLoading && hasData,
                onClick = { onPageSelected(page) }
            )
            Spacer(modifier = Modifier.width(Dimens.PaddingExtraSmall))
        }

        IconButton(
            onClick = { onPageSelected(currentPage + 1) },
            enabled = !isLoading && hasNextPage && hasData
        ) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = stringResource(R.string.pagina_siguiente)
            )
        }
    }
}

@Composable
private fun PageChip(page: Int, isSelected: Boolean, enabled: Boolean, onClick: () -> Unit) {
    val containerColor =
        if (isSelected && enabled) MaterialTheme.colorScheme.primary else Color.Transparent
    val contentColor = if (isSelected && enabled) Color.White else MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                RoundedCornerShape(8.dp)
            )
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = page.toString(), color = contentColor, fontWeight = FontWeight.Bold)
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
            alerts = sampleAlerts,
            currentPage = 2,
            hasNextPage = true,
            isLoading = false,
            selectedAlert = AlertType.PRESSURE,
            selectedDate = "01/09/2026",
            selectedWheel = "Eje 1 Izq",
            wheels = listOf("Todas", "Eje 1 Izq"),
            onBack = {},
            onApplyFilters = { _, _, _, _ -> },
            onPageSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PageNavigatorPreview() {
    HombreCamionTheme {
        PageNavigator(
            hasData = false,
            currentPage = 2,
            hasNextPage = true,
            isLoading = false,
            onPageSelected = {}
        )
    }
}
