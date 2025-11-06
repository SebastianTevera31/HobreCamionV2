package com.rfz.appflotal.presentation.ui.inspection.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.components.AwaitDialog
import com.rfz.appflotal.presentation.ui.inspection.components.NumberField
import com.rfz.appflotal.presentation.ui.inspection.components.ReportDropdown
import com.rfz.appflotal.presentation.ui.inspection.viewmodel.InspectionUi
import com.rfz.appflotal.presentation.ui.inspection.viewmodel.InspectionUiState
import com.rfz.appflotal.presentation.ui.inspection.viewmodel.InspectionViewModel
import com.rfz.appflotal.presentation.ui.inspection.viewmodel.rememberInspectionFormState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun InspectionRoute(
    tire: String,
    temperature: Float,
    pressure: Float,
    onBack: () -> Unit = {},
    onFinish: (tire: String) -> Unit = {},
    viewModel: InspectionViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val requestState = viewModel.requestState.collectAsState()
    val context = LocalContext.current

    BackHandler {
        onBack()
        viewModel.clearInspectionUiState()
    }

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    LaunchedEffect(true) {
        viewModel.eventFlow.collectLatest { event ->
            Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
        }
    }

    if (requestState.value.isSending) {
        AwaitDialog()
    }

    InspectionScreen(
        tireLabel = tire,
        initialTemperature = temperature.toInt(),
        initialPressure = pressure.toInt(),
        uiState = uiState.value,
        onBack = onBack,
        onFinish = { report ->
            viewModel.uploadInspection(tire, report)
            onFinish(tire)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionScreen(
    tireLabel: String,
    initialTemperature: Int,
    initialPressure: Int,
    uiState: InspectionUiState,
    onBack: () -> Unit,
    onFinish: (InspectionUi) -> Unit
) {
    val context = LocalContext.current
    val scroll = rememberScrollState()
    val form = rememberInspectionFormState(
        initialTemperature = initialTemperature,
        initialPressure = initialPressure
    )
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.regresar),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                title = {
                    Column {
                        Text(
                            stringResource(R.string.inspeccion),
                            style = MaterialTheme.typography.titleLarge
                                .copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                        )
                        Text(
                            tireLabel,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                // Mantengo el gradiente como en tu versión
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
                    .heightIn(min = 56.dp)
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            Surface(shadowElevation = 4.dp, contentColor = Color.White) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .imePadding()
                ) {
                    val isValid = form.validate()
                    Button(
                        onClick = {
                            val ui = form.toUiOrNull()
                            if (ui != null) {
                                onFinish(ui)
                            } else scope.launch {
                                val message = context.getString(R.string.corregir_campos_marcados)
                                snackbar.showMessage(message)
                            }
                        },
                        enabled = isValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text(stringResource(R.string.terminar_inspeccion))
                    }
                }
            }
        }
    ) { inner ->
        when (uiState) {
            is InspectionUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .padding(inner)
                        .fillMaxSize()
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            is InspectionUiState.Empty -> {
                Box(
                    modifier = Modifier
                        .padding(inner)
                        .fillMaxSize()
                ) {
                    Text(
                        text = stringResource(R.string.sin_informacion),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            is InspectionUiState.Error -> {
                Box(
                    modifier = Modifier
                        .padding(inner)
                        .fillMaxSize()
                ) {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            is InspectionUiState.Success -> {
                Column(
                    modifier = Modifier
                        .padding(inner)
                        .fillMaxSize()
                        .verticalScroll(scroll)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    SectionHeader(stringResource(R.string.tipo_de_reporte))
                    // Reporte
                    ReportDropdown(
                        reports = uiState.inspectionList,
                        selectedId = form.selectedReportId,
                        onSelected = { form.selectedReportId = it },
                        errorText = form.selectedReportIdError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))

                    // Bloque: Temperatura y Odometro
                    SectionHeader(stringResource(R.string.lecturas))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            dimensionResource(id = R.dimen.small_dimen)
                        )
                    ) {
                        NumberField(
                            value = form.temperature,
                            onValueChange = { form.temperature = it },
                            label = stringResource(R.string.temperatura_c),
                            errorText = form.temperatureError,
                            modifier = Modifier.weight(1f)
                        )
                        NumberField(
                            value = form.odometer,
                            onValueChange = { form.odometer = it },
                            label = stringResource(R.string.odometro),
                            errorText = form.odometerError,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            dimensionResource(id = R.dimen.small_dimen)
                        )
                    ) {
                        NumberField(
                            value = form.pressureMeasured,
                            onValueChange = { form.pressureMeasured = it },
                            label = stringResource(R.string.presion_medida),
                            errorText = form.pressureMeasuredError,
                            modifier = Modifier.weight(1f)
                        )

                        NumberField(
                            value = form.adjustedPressure,
                            onValueChange = { form.adjustedPressure = it },
                            label = stringResource(R.string.presion_ajustada),
                            errorText = form.adjustedPressureError,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(12.dp))

                    // Bloque: Profundidad de piso
                    SectionHeader(stringResource(R.string.profundidad_de_piso_mm))
                    Spacer(Modifier.height(8.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(
                                dimensionResource(id = R.dimen.small_dimen)
                            )
                        ) {
                            NumberField(
                                value = form.treadDepth1,
                                onValueChange = { form.treadDepth1 = it },
                                label = "T1",
                                errorText = form.treadDepth1Error,
                                modifier = Modifier
                                    .widthIn(min = 220.dp)
                                    .heightIn(min = 56.dp)
                                    .weight(1f),
                                keyboardType = KeyboardType.Number
                            )

                            NumberField(
                                value = form.treadDepth2,
                                onValueChange = { form.treadDepth2 = it },
                                label = "T2",
                                errorText = form.treadDepth2Error,
                                modifier = Modifier
                                    .widthIn(min = 220.dp)
                                    .heightIn(min = 56.dp)
                                    .weight(1f),
                                keyboardType = KeyboardType.Number
                            )

                            NumberField(
                                value = form.treadDepth3,
                                onValueChange = { form.treadDepth3 = it },
                                label = "T3",
                                errorText = form.treadDepth3Error,
                                modifier = Modifier
                                    .widthIn(min = 220.dp)
                                    .heightIn(min = 56.dp)
                                    .weight(1f),
                                keyboardType = KeyboardType.Number
                            )

                            NumberField(
                                value = form.treadDepth4,
                                onValueChange = { form.treadDepth4 = it },
                                label = "T4",
                                errorText = form.treadDepth4Error,
                                modifier = Modifier
                                    .widthIn(min = 220.dp)
                                    .heightIn(min = 56.dp)
                                    .weight(1f),
                                keyboardType = KeyboardType.Number
                            )
                        }
                        if (form.oneTreadDepthAtLeast != null) {
                            Text(
                                text = stringResource(R.string.una_medidad_profundidad_mayor_0),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        Image(
                            painterResource(R.drawable.tire_tread_diagram),
                            contentDescription = null,
                            modifier = Modifier.height(140.dp)
                        )
                    }


                    Spacer(Modifier.height(80.dp)) // espacio para que no lo tape la bottomBar
                }
            }
        }
    }
}

// ========= Helpers =========
@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

private suspend fun SnackbarHostState.showMessage(
    message: String,
    withDismiss: Boolean = true
) {
    showSnackbar(
        message = message,
        withDismissAction = withDismiss,
        duration = SnackbarDuration.Short
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 800)
@Composable
private fun PreviewInspection() {
    HombreCamionTheme {
        InspectionScreen(
            tireLabel = "P2",
            initialTemperature = 2,
            initialPressure = 2,
            uiState = InspectionUiState.Success(inspectionList = emptyList()),
            onBack = {},
            onFinish = {}
        )
    }
}