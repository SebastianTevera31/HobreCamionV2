package com.rfz.appflotal.presentation.ui.reportes.ui.fuel

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.report.FuelConsumptionReportResponse
import com.rfz.appflotal.presentation.commons.ErrorView
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
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
                title = stringResource(R.string.consumo_combustible),
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
                        text = stringResource(R.string.consumo_combustible),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = stringResource(R.string.seleccione_periodo_fuel),
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
                text = stringResource(R.string.ficha_informativa),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            HorizontalDivider()

            if (report == null) {
                Text(
                    text = stringResource(R.string.no_info_mostrar),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                InfoRow(
                    label = stringResource(R.string.mes),
                    value = report.month
                )

                InfoRow(
                    label = stringResource(R.string.odometro_mensual),
                    value = report.monthlyOdometer
                )

                InfoRow(
                    label = stringResource(R.string.combustible_mensual),
                    value = report.monthlyFuel
                )

                InfoRow(
                    label = stringResource(R.string.numero_cargas),
                    value = report.loadCount.toString()
                )

                InfoRow(
                    label = stringResource(R.string.tipo_combustible),
                    value = report.fuelTypeName
                )

                InfoRow(
                    label = stringResource(R.string.rendimiento_mensual),
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
fun FuelEmissionReportScreenPreview() {
    val sampleReports = listOf(
        FuelConsumptionReportResponse(
            month = "Enero",
            monthlyOdometer = "1200 km",
            monthlyFuel = "100 gal",
            loadCount = 5,
            fuelTypeName = "Diesel",
            monthlyPerformance = "12 km/gal"
        ),
        FuelConsumptionReportResponse(
            month = "Febrero",
            monthlyOdometer = "1100 km",
            monthlyFuel = "90 gal",
            loadCount = 4,
            fuelTypeName = "Diesel",
            monthlyPerformance = "12.2 km/gal"
        )
    )
    HombreCamionTheme {
        FuelEmissionReportScreen(
            screenState = LoadState.Success(Unit),
            reports = sampleReports,
            selectedMonth = "Enero",
            onMonthSelected = {},
            onBack = {}
        )
    }
}
