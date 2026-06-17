package com.rfz.appflotal.presentation.ui.reportes.rendimiento.fuel

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.data.model.report.FuelConsumptionReportResponse
import com.rfz.appflotal.presentation.commons.ErrorView
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.ui.components.LoadingDialog
import com.rfz.appflotal.presentation.ui.reportes.components.MonthSelector
import com.rfz.appflotal.presentation.ui.utils.LoadState

@Composable
fun FuelConsumptionReportRoute(
    screenState: LoadState<Unit>,
    reports: List<FuelConsumptionReportResponse>,
    onBack: () -> Unit
) {
    var selectedMonth by remember {
        mutableStateOf(reports.firstOrNull()?.month)
    }

    FuelEmissionReportScreen(
        screenState = screenState,
        reports = reports,
        selectedMonth = selectedMonth,
        onMonthSelected = { month ->
            selectedMonth = month
        },
        onBack = onBack
    )
}

@Composable
fun FuelEmissionReportScreen(
    screenState: LoadState<Unit>,
    reports: List<FuelConsumptionReportResponse>,
    selectedMonth: String?,
    onMonthSelected: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedReport = reports.firstOrNull { it.month == selectedMonth }
        ?: reports.firstOrNull()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SimpleTopBar(
                title = "Consumo de Combustible",
                onBack = onBack,
                showBackButton = true,
                subTitle = ""
            )
        }
    ) { innerPadding ->

        when (screenState) {
            is LoadState.Error -> {
                ErrorView { }
            }

            LoadState.Loading -> {
                LoadingDialog()
            }

            is LoadState.Success -> {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Consumo de Combustible",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "Seleccione un periodo para ver la información de consumo de combustible.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    MonthSelector(
                        months = reports.map { it.month },
                        selectedMonth = selectedReport?.month,
                        onMonthSelected = onMonthSelected
                    )

                    FuelConsumptionInfoCard(
                        report = selectedReport
                    )
                }
            }

            else -> Unit
        }
    }
}

@Composable
fun FuelConsumptionInfoCard(
    report: FuelConsumptionReportResponse?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Text(
                text = "Ficha informativa",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            HorizontalDivider()

            if (report == null) {
                Text(
                    text = "No hay información disponible para mostrar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                InfoRow(
                    label = "Mes",
                    value = report.month
                )

                InfoRow(
                    label = "Odómetro mensual",
                    value = report.monthlyOdometer
                )

                InfoRow(
                    label = "Combustible mensual",
                    value = report.monthlyFuel
                )

                InfoRow(
                    label = "Número de cargas",
                    value = report.loadCount.toString()
                )

                InfoRow(
                    label = "Tipo de combustible",
                    value = report.fuelTypeName
                )

                InfoRow(
                    label = "Rendimiento mensual",
                    value = report.monthlyPerformance
                )
            }
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}