package com.rfz.appflotal.presentation.ui.reportes.ui.cpk

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.assembly.AssemblyTire
import com.rfz.appflotal.data.model.report.CpkReportResponse
import com.rfz.appflotal.data.model.tire.Tire
import com.rfz.appflotal.presentation.commons.ErrorView
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.components.CompleteFormButton
import com.rfz.appflotal.presentation.ui.components.LoadingDialog
import com.rfz.appflotal.presentation.ui.reportes.components.SharePdfReportBottomSheet
import com.rfz.appflotal.presentation.ui.reportes.pdf.createGeneralRendimientoPdf
import com.rfz.appflotal.presentation.ui.utils.LoadState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MenuRendimientoScreen(
    onLoadData: () -> Unit,
    loadState: LoadState<Unit>,
    exportLoadState: LoadState<Unit>,
    wheels: List<AssemblyTire>,
    reports: List<CpkReportResponse>,
    tires: List<Tire>,
    onWheelSelected: (Int) -> Unit,
    onBack: () -> Unit,
    onExportPdf: (Uri?) -> Unit,
    onSharePdf: (Uri) -> Unit,
    onClearPdfState: () -> Unit,
    pdfUri: Uri?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showSharePdf by remember { mutableStateOf(false) }
    val stringResource = stringResource(R.string.error_guardar_reporte)

    LaunchedEffect(Unit) {
        onLoadData()
    }

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
                stringResource(R.string.error_guardar_reporte),
                Toast.LENGTH_SHORT
            ).show()
        }

        else -> Unit
    }

    SharePdfReportBottomSheet(
        show = showSharePdf,
        tirePosition = stringResource(R.string.general),
        pdfUri = pdfUri,
        onDismiss = {
            showSharePdf = false
            onClearPdfState()
        },
        title = stringResource(R.string.reporte_rendimiento_general),
        onSharePdf = { uri ->
            onSharePdf(uri)
        }
    )

    when (loadState) {
        is LoadState.Loading -> {
            LoadingDialog()
        }

        is LoadState.Success -> {
            Scaffold(
                modifier = modifier.fillMaxSize(),
                topBar = {
                    SimpleTopBar(
                        title = stringResource(R.string.reporte_rendimiento),
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
                        if (wheels.isNotEmpty()) {
                            CompleteFormButton(
                                text = stringResource(R.string.guardar_reporte_general),
                                isValid = true,
                                onFinish = {
                                    scope.launch {
                                        try {
                                            val uri = withContext(Dispatchers.IO) {
                                                createGeneralRendimientoPdf(
                                                    context = context,
                                                    wheels = wheels,
                                                    reports = reports,
                                                    tires = tires
                                                )
                                            }
                                            onExportPdf(uri)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            onExportPdf(null)
                                            Toast.makeText(
                                                context,
                                                stringResource,
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
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.seleccione_llanta),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Text(
                            text = stringResource(R.string.llantas_disponibles, wheels.size),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (wheels.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.no_llantas_disponibles),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(wheels) { wheel ->
                                WheelButton(
                                    wheel = wheel,
                                    onClick = {
                                        onWheelSelected(wheel.idTire)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        is LoadState.Error -> {
            ErrorView {
                onLoadData()
            }
        }

        else -> Unit
    }
}

@Composable
fun WheelButton(
    wheel: AssemblyTire,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true
) {
    val colorScheme = MaterialTheme.colorScheme

    val containerColor = if (selected) {
        colorScheme.primary.copy(alpha = 0.14f)
    } else {
        colorScheme.surface
    }

    val borderColor = if (selected) {
        colorScheme.primary
    } else {
        colorScheme.outlineVariant
    }

    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 76.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            disabledContainerColor = colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = borderColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 3.dp else 1.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        colorScheme.primary.copy(
                            alpha = if (selected) 0.18f else 0.10f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = wheel.positionTire,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Llanta ${wheel.positionTire}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = colorScheme.onSurface
                )
            }

            Text(
                text = "›",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = colorScheme.primary
            )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun MenuRendimientoScreenPreview() {
    HombreCamionTheme {
        MenuRendimientoScreen(
            wheels = listOf(
                AssemblyTire(
                    idAxle = 1,
                    idTire = 1,
                    positionTire = "P1",
                    odometer = 1000,
                    assemblyDate = "15 de junio del 2026",
                    updatedAt = 123449
                )
            ),
            reports = emptyList(),
            tires = emptyList(),
            onLoadData = {},
            loadState = LoadState.Success(Unit),
            exportLoadState = LoadState.Idle,
            onWheelSelected = {},
            onBack = {},
            onExportPdf = {},
            onSharePdf = {},
            onClearPdfState = {},
            pdfUri = null
        )
    }
}