package com.rfz.appflotal.presentation.ui.reportes.ui.co2

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.report.CO2EmissionsReportResponse
import com.rfz.appflotal.presentation.commons.ErrorView
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.components.LoadingDialog
import com.rfz.appflotal.presentation.ui.reportes.components.MonthSelector
import com.rfz.appflotal.presentation.ui.reportes.ui.fuel.InfoRow
import com.rfz.appflotal.presentation.ui.utils.LoadState

@Composable
fun Co2EmissionReportRoute(
    screenState: LoadState<Unit>,
    reports: List<CO2EmissionsReportResponse>,
    onBack: () -> Unit
) {
    var selectedMonth by remember {
        mutableStateOf(reports.firstOrNull()?.month)
    }

    CO2EmissionReportScreen(
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
fun CO2EmissionReportScreen(
    screenState: LoadState<Unit>,
    reports: List<CO2EmissionsReportResponse>,
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
                title = stringResource(R.string.emisiones_co2),
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
                        text = stringResource(R.string.emisiones_co2),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = stringResource(R.string.seleccione_periodo_co2),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    MonthSelector(
                        months = reports.map { it.month },
                        selectedMonth = selectedReport?.month,
                        onMonthSelected = onMonthSelected
                    )

                    Co2EmissionsInfoCard(
                        report = selectedReport
                    )
                }
            }

            else -> Unit
        }
    }
}

@Composable
fun Co2EmissionsInfoCard(
    report: CO2EmissionsReportResponse?,
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
                    label = stringResource(R.string.emisiones_mensuales),
                    value = report.monthlyCO2Emissions
                )

                InfoRow(
                    label = stringResource(R.string.tipo_combustible),
                    value = report.fuelTypeName
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CO2EmissionReportScreenPreview() {
    val sampleReports = listOf(
        CO2EmissionsReportResponse(
            month = "Enero",
            monthlyOdometer = "1200 km",
            monthlyCO2Emissions = "250 kg",
            fuelTypeName = "Diesel"
        ),
        CO2EmissionsReportResponse(
            month = "Febrero",
            monthlyOdometer = "1100 km",
            monthlyCO2Emissions = "230 kg",
            fuelTypeName = "Diesel"
        )
    )
    HombreCamionTheme {
        CO2EmissionReportScreen(
            screenState = LoadState.Success(Unit),
            reports = sampleReports,
            selectedMonth = "Enero",
            onMonthSelected = {},
            onBack = {}
        )
    }
}
