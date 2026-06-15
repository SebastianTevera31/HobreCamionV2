package com.rfz.appflotal.presentation.ui.reportes.rendimiento

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.report.CpkReportResponse
import com.rfz.appflotal.data.model.tire.Tire
import com.rfz.appflotal.presentation.commons.ErrorView
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.components.CompleteFormButton
import com.rfz.appflotal.presentation.ui.components.LoadingDialog
import com.rfz.appflotal.presentation.ui.components.TireInfoCard
import com.rfz.appflotal.presentation.ui.reportes.viewmodel.formatCurrency
import com.rfz.appflotal.presentation.ui.reportes.viewmodel.formatDecimal
import com.rfz.appflotal.presentation.ui.utils.LoadState

@Composable
fun RendimientoRuedaScreen(
    onBack: () -> Unit,
    tirePosition: String,
    loadState: LoadState<Unit>,
    report: CpkReportResponse?,
    tire: Tire?,
    modifier: Modifier = Modifier,
    onExport: () -> Unit = {}
) {
    val canExport = loadState is LoadState.Success && report != null

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SimpleTopBar(
                title = "Rendimiento - Rueda $tirePosition",
                onBack = onBack,
                showBackButton = true,
                subTitle = ""
            )
        },
        bottomBar = {
            Surface(
                modifier = modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                tonalElevation = 2.dp
            ) {
                if (canExport) {
                    CompleteFormButton(
                        text = "Exportar",
                        isValid = true,
                        onFinish = onExport,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(52.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        when (loadState) {
            is LoadState.Loading, LoadState.Idle -> {
                LoadingDialog()
            }

            is LoadState.Success -> {
                if (report != null) {
                    RendimientoRuedaContent(
                        tirePosition = tirePosition,
                        report = report,
                        tire = tire,
                        modifier = Modifier.padding(innerPadding)
                    )
                } else {
                    EmptyReportState(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }

            is LoadState.Error -> {
                ErrorView { }
            }

            else -> Unit
        }
    }
}

@Composable
private fun RendimientoRuedaContent(
    tirePosition: String,
    report: CpkReportResponse,
    tire: Tire?,
    modifier: Modifier = Modifier
) {
    val mediumPadding = dimensionResource(R.dimen.medium_dimen)
    val smallPadding = dimensionResource(R.dimen.small_dimen)

    val finalDepth = maxOf(
        0.0,
        (tire?.thread ?: 0.0) - report.differenceInTreadDepth
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(mediumPadding),
        verticalArrangement = Arrangement.spacedBy(smallPadding)
    ) {
        item {
            TireReportHeader(
                tirePosition = tirePosition,
                tire = tire
            )
        }

        item {
            ReportSection(
                title = "Resumen"
            ) {
                MetricGrid(
                    items = listOf(
                        MetricItem(
                            title = "Odómetro",
                            value = "${report.differenceOdometer} km"
                        ),
                        MetricItem(
                            title = "Profundidad final",
                            value = "${formatDecimal(finalDepth)} mm"
                        ),
                        MetricItem(
                            title = "Distancia recorrida actual",
                            value = "${report.differenceOdometer} km"
                        ),
                        MetricItem(
                            title = "Desgaste total",
                            value = "${formatDecimal(report.differenceInTreadDepth.toDouble())} mm"
                        ),
                        MetricItem(
                            title = "Ciclo actual",
                            value = report.lifeCycle.toString()
                        )
                    )
                )
            }
        }

        item {
            ReportSection(
                title = "Promedios de desgaste"
            ) {
                MetricGrid(
                    items = listOf(
                        MetricItem(
                            title = "Por distancia",
                            value = "${formatDecimal(report.kmPerMm)} km/mm"
                        )
                    )
                )
            }
        }

        item {
            ReportSection(
                title = "Costos"
            ) {
                MetricGrid(
                    items = listOf(
                        MetricItem(
                            title = "Costo unitario",
                            value = formatCurrency(report.unitCost)
                        ),
                        MetricItem(
                            title = "Por distancia CPK",
                            value = formatCurrency(report.costPerKm)
                        ),
                        MetricItem(
                            title = "Por profundidad",
                            value = formatCurrency(report.costByMm)
                        )
                    )
                )
            }
        }
    }
}

@Composable
private fun TireReportHeader(
    tirePosition: String,
    tire: Tire?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensionResource(R.dimen.medium_dimen)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.medium_dimen)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.small_dimen)
            )
        ) {
            Text(
                text = "Rueda $tirePosition",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            tire?.let {
                TireInfoCard(
                    tire = it,
                    modifier = Modifier
                        .width(240.dp)
                        .wrapContentHeight()
                )
            } ?: Text(
                text = "No hay información de la llanta",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ReportSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensionResource(R.dimen.medium_dimen)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.medium_dimen)),
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.small_dimen)
            )
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider(
                thickness = dimensionResource(R.dimen.thin_dimen)
            )

            content()
        }
    }
}

private data class MetricItem(
    val title: String,
    val value: String
)

@Composable
private fun MetricGrid(
    items: List<MetricItem>,
    modifier: Modifier = Modifier
) {
    val spacing = dimensionResource(R.dimen.small_dimen)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                rowItems.forEach { item ->
                    MetricCard(
                        title = item.title,
                        value = item.value,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(dimensionResource(R.dimen.small_dimen)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.small_dimen)),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun EmptyReportState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No hay información disponible para este reporte",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(dimensionResource(R.dimen.medium_dimen))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RendimientoRuedaScreenPreview() {
    HombreCamionTheme {
        RendimientoRuedaScreen(
            onBack = {},
            loadState = LoadState.Success(Unit),
            report = CpkReportResponse(
                idTire = 1,
                differenceOdometer = 15000,
                differenceInTreadDepth = 5,
                kmPerMm = 3000.0,
                lifeCycle = 1,
                unitCost = 500.0,
                costPerKm = 0.033,
                renovatedDesign = "Regrabado",
                costByMm = 100.0,
                tireNumber = "T001"
            ),
            tire = Tire(
                id = 101,
                description = "Michelin - size: 205/55R16",
                size = "205/55R16",
                brand = "Michelin",
                model = "Primacy 4",
                thread = 7.5,
                loadingCapacity = "615",
                destination = "Eje delantero"
            ),
            tirePosition = "1",
            onExport = {}
        )
    }
}