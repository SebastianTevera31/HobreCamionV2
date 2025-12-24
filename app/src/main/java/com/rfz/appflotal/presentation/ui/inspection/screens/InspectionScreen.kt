package com.rfz.appflotal.presentation.ui.inspection.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rfz.appflotal.R
import com.rfz.appflotal.data.repository.UnidadOdometro
import com.rfz.appflotal.data.repository.UnidadPresion
import com.rfz.appflotal.data.repository.UnidadTemperatura
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.components.AwaitDialog
import com.rfz.appflotal.presentation.ui.inspection.components.InspectionContent
import com.rfz.appflotal.presentation.ui.inspection.viewmodel.InspectionUi
import com.rfz.appflotal.presentation.ui.inspection.viewmodel.InspectionUiState
import com.rfz.appflotal.presentation.ui.inspection.viewmodel.InspectionViewModel
import com.rfz.appflotal.presentation.ui.inspection.viewmodel.OperationState
import com.rfz.appflotal.presentation.ui.inspection.viewmodel.rememberInspectionFormState
import com.rfz.appflotal.presentation.ui.utils.showMessage
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
        viewModel.loadData()
    }

    when (requestState.value.operationState) {
        OperationState.Error -> {
            viewModel.clearInspectionRequestState()
        }

        OperationState.Loading -> {
            if (requestState.value.isSending) {
                AwaitDialog()
            }
        }

        OperationState.Success -> {
            viewModel.clearInspectionUiState()
            onFinish(tire)
        }
    }

    LaunchedEffect(true) {
        viewModel.eventFlow.collectLatest { event ->
            Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
        }
    }

    InspectionScreen(
        tireLabel = tire,
        initialTemperature = temperature,
        initialPressure = pressure,
        uiState = uiState.value,
        onBack = onBack,
        onFinish = { report ->
            viewModel.uploadInspection(tire, report)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionScreen(
    tireLabel: String,
    initialTemperature: Float,
    initialPressure: Float,
    uiState: InspectionUiState,
    onBack: () -> Unit,
    onFinish: (InspectionUi) -> Unit
) {
    val scroll = rememberScrollState()

    val form = rememberInspectionFormState(
        initialTemperature = initialTemperature,
        initialPressure = initialPressure,
        lastOdometer = 0,
        isValidatingReports = true
    )
    val message = stringResource(R.string.corregir_campos_marcados)
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Use LaunchedEffect to safely update the form state when uiState changes.
    LaunchedEffect(uiState) {
        if (uiState is InspectionUiState.Success) {
            if (uiState.lastOdometer > 0) {
                form.odometer = uiState.lastOdometer.toString()
            }
        }
    }

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
                InspectionContent(
                    modifier = Modifier
                        .padding(inner)
                        .fillMaxSize()
                        .verticalScroll(scroll)
                        .padding(16.dp),
                    form = form,
                    isOdometerEditable = uiState.isOdometerEditable,
                    lastOdometer = uiState.lastOdometer,
                    inspectionList = uiState.inspectionList,
                    temperatureUnit = uiState.temperatureUnit.symbol,
                    pressureUnit = uiState.pressureUnit.symbol,
                    odometerUnit = uiState.odometerUnit.symbol
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 800)
@Composable
private fun PreviewInspection() {
    HombreCamionTheme {
        InspectionScreen(
            tireLabel = "P2",
            initialTemperature = 2.0f,
            initialPressure = 2.0f,
            uiState = InspectionUiState.Success(
                inspectionList = emptyList(),
                lastOdometer = 1000,
                isOdometerEditable = true,
                pressureUnit = UnidadPresion.PSI,
                temperatureUnit = UnidadTemperatura.CELCIUS,
                odometerUnit = UnidadOdometro.KILOMETROS,
            ),
            onBack = {},
            onFinish = {}
        )
    }
}
