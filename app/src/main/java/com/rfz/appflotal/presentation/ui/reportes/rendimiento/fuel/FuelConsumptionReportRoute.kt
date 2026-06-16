package com.rfz.appflotal.presentation.ui.reportes.rendimiento.fuel

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import androidx.compose.ui.tooling.preview.Preview
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun FuelConsumptionReportRoute(
    reports: List<FuelConsumptionReportResponse>,
    onBack: () -> Unit
) {
    var selectedMonth by remember {
        mutableStateOf(reports.firstOrNull()?.month)
    }

    FuelConsumptionReportScreen(
        reports = reports,
        selectedMonth = selectedMonth,
        onMonthSelected = { month ->
            selectedMonth = month
        },
        onBack = onBack
    )
}

@Composable
fun FuelConsumptionReportScreen(
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
}

@Composable
fun MonthSelector(
    months: List<String>,
    selectedMonth: String?,
    onMonthSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {

        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = selectedMonth ?: "Mes",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            months.forEach { month ->
                DropdownMenuItem(
                    text = { Text(text = month) },
                    onClick = {
                        expanded = false
                        onMonthSelected(month)
                    }
                )
            }
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

@Preview(showBackground = true)
@Composable
fun FuelConsumptionReportScreenPreview() {
    val sampleReports = listOf(
        FuelConsumptionReportResponse(
            month = "Enero 2024",
            monthlyOdometer = "1500 km",
            monthlyFuel = "120 L",
            loadCount = 4,
            fuelTypeName = "Diesel",
            monthlyPerformance = "12.5 km/L"
        ),
        FuelConsumptionReportResponse(
            month = "Febrero 2024",
            monthlyOdometer = "1650 km",
            monthlyFuel = "135 L",
            loadCount = 5,
            fuelTypeName = "Diesel",
            monthlyPerformance = "12.2 km/L"
        )
    )

    HombreCamionTheme {
        FuelConsumptionReportScreen(
            reports = sampleReports,
            selectedMonth = "Enero 2024",
            onMonthSelected = {},
            onBack = {}
        )
    }
}