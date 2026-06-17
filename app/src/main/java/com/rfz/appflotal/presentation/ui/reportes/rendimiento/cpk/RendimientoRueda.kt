package com.rfz.appflotal.presentation.ui.reportes.rendimiento.cpk

import android.net.Uri
import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.report.CpkReportResponse
import com.rfz.appflotal.data.model.tire.Tire
import com.rfz.appflotal.presentation.commons.ErrorView
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.ui.components.CompleteFormButton
import com.rfz.appflotal.presentation.ui.components.LoadingDialog
import com.rfz.appflotal.presentation.ui.components.TireInfoCard
import com.rfz.appflotal.presentation.ui.reportes.components.SharePdfReportBottomSheet
import com.rfz.appflotal.presentation.ui.reportes.pdf.createRendimientoPdf
import com.rfz.appflotal.presentation.ui.reportes.viewmodel.formatCurrency
import com.rfz.appflotal.presentation.ui.reportes.viewmodel.formatDecimal
import com.rfz.appflotal.presentation.ui.utils.LoadState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RendimientoRuedaScreen(
    onBack: () -> Unit,
    tirePosition: String,
    loadState: LoadState<Unit>,
    exportLoadState: LoadState<Unit>,
    report: CpkReportResponse?,
    tire: Tire?,
    pdfUri: Uri?,
    modifier: Modifier = Modifier,
    onExportPdf: (uri: Uri?) -> Unit,
    onShareImage: (uri: Uri?) -> Unit
) {
    val canExport = loadState is LoadState.Success && report != null
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showSharePdf by remember { mutableStateOf(false) }

    LaunchedEffect(exportLoadState) {
        if (exportLoadState is LoadState.Success) {
            showSharePdf = true
        }
    }

    when (exportLoadState) {
        is LoadState.Loading -> {
            LoadingDialog(message = R.string.guardando_reporte)
        }

        is LoadState.Error -> {
            Toast.makeText(
                context,
                "Error al guardar el reporte",
                Toast.LENGTH_SHORT
            ).show()
        }

        else -> Unit
    }

    SharePdfReportBottomSheet(
        show = showSharePdf,
        tirePosition = tirePosition,
        pdfUri = pdfUri,
        onDismiss = {
            showSharePdf = false
        },
        title = "Reporte de rendimiento",
        onSharePdf = { uri ->
            onShareImage(uri)
        }
    )
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
                        text = "Guardar reporte",
                        isValid = true,
                        onFinish = {
                            scope.launch {
                                try {
                                    val uri = withContext(Dispatchers.IO) {
                                        createRendimientoPdf(
                                            context = context,
                                            tirePosition = tirePosition,
                                            report = report!!,
                                            tire = tire
                                        )
                                    }

                                    onExportPdf(uri)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    onExportPdf(null)

                                    Toast.makeText(
                                        context,
                                        "Error al generar el reporte PDF",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
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
                    // Vista para el Usuario (UI Normal)
                    RendimientoRuedaContent(
                        tirePosition = tirePosition,
                        report = report,
                        tire = tire,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
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