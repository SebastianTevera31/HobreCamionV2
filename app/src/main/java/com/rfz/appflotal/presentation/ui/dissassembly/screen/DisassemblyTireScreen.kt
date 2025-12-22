package com.rfz.appflotal.presentation.ui.dissassembly.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.presentation.commons.CircularLoading
import com.rfz.appflotal.presentation.commons.ErrorView
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.components.AwaitDialog
import com.rfz.appflotal.presentation.ui.components.CatalogDropdown
import com.rfz.appflotal.presentation.ui.components.CompleteFormButton
import com.rfz.appflotal.presentation.ui.components.TireInfoCard
import com.rfz.appflotal.presentation.ui.dissassembly.viewmodel.DisassemblyUiState
import com.rfz.appflotal.presentation.ui.dissassembly.viewmodel.DisassemblyViewModel
import com.rfz.appflotal.presentation.ui.inspection.components.InspectionContent
import com.rfz.appflotal.presentation.ui.inspection.viewmodel.InspectionFormState
import com.rfz.appflotal.presentation.ui.inspection.viewmodel.InspectionUi
import com.rfz.appflotal.presentation.ui.inspection.viewmodel.rememberInspectionFormState
import com.rfz.appflotal.presentation.ui.utils.OperationStatus
import com.rfz.appflotal.presentation.ui.utils.showMessage
import com.rfz.appflotal.presentation.ui.utils.validate
import kotlinx.coroutines.launch

sealed interface NavigationScreen {
    object INSPECTION : NavigationScreen
    object DISASSEMBLY : NavigationScreen
}

@Composable
fun DisassemblyTireScreen(
    positionTire: String,
    initialTemperature: Float,
    initialPressure: Float,
    viewModel: DisassemblyViewModel,
    onBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val errorMessage = stringResource(R.string.error_desmontaje)
    val successMessage = stringResource(R.string.desmontaje_exitoso)

    val form = rememberInspectionFormState(
        initialTemperature = 0,
        initialPressure = 0,
        lastOdometer = 0,
        isValidatingReports = false
    )

    LaunchedEffect(positionTire) {
        viewModel.loadData(
            tirePosition = positionTire,
            initialPressure = initialPressure.toInt(),
            initialTemperature = initialTemperature.toInt()
        )
    }

    LaunchedEffect(uiState.value.operationStatus) {
        if (uiState.value.operationStatus == OperationStatus.Success) {
            Toast.makeText(
                context, successMessage, Toast.LENGTH_SHORT
            ).show()
            onFinish()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.cleanUiState()
        }
    }

    DisassemblyTireView(
        form = form,
        uiState = uiState.value,
        onBack = {
            when (uiState.value.navigationScreen) {
                NavigationScreen.DISASSEMBLY -> {
                    viewModel.updateNavigation(NavigationScreen.INSPECTION)

                }

                NavigationScreen.INSPECTION -> {
                    viewModel.cleanUiState()
                    onBack()
                }
            }
        },
        onError = {
            Toast.makeText(
                context,
                errorMessage, Toast.LENGTH_SHORT
            ).show()
            viewModel.restartOperationStatus()
        },
        onDismount = { causeId, destinationId ->
            viewModel.dismountTire(causeId, destinationId)
        },
        onInspectionFinish = {
            viewModel.updateInspection(it)
            viewModel.updateNavigation(NavigationScreen.DISASSEMBLY)
        },
        modifier = modifier,
    )
}

@Composable
fun DisassemblyTireView(
    form: InspectionFormState,
    uiState: DisassemblyUiState,
    onBack: () -> Unit,
    onDismount: (causeId: Int, destinationId: Int) -> Unit,
    onInspectionFinish: (form: InspectionUi) -> Unit,
    onError: suspend () -> Unit,
    modifier: Modifier = Modifier
) {
    var causesSelected: CatalogItem? by remember { mutableStateOf(null) }
    var destinationSelected: CatalogItem? by remember { mutableStateOf(null) }
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    val errorMessage = stringResource(R.string.corregir_campos_marcados)

    LaunchedEffect(uiState) {
        if (uiState.screenLoadStatus == OperationStatus.Success) {
            if (uiState.lastOdometer > 0) {
                form.odometer = uiState.lastOdometer.toString()
            }
            uiState.initialTemperature?.let {
                form.temperature = it.toString()
            }
            uiState.initialPressure?.let {
                form.pressureMeasured = it.toString()
            }
        }
    }

    val isFormValid by remember {
        derivedStateOf {
            causesSelected != null && destinationSelected != null
        }
    }

    when (uiState.operationStatus) {
        OperationStatus.Error -> {
            LaunchedEffect(uiState.operationStatus) {
                causesSelected = null
                destinationSelected = null
                onError()
            }
        }

        OperationStatus.Loading -> {
            AwaitDialog()
        }

        else -> {}
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbar) },
        topBar = {
            SimpleTopBar(
                title = stringResource(R.string.desmontaje),
                subTitle = uiState.positionTire,
                onBack = onBack
            )
        },
        bottomBar = {
            val (buttonText, isValid) = when (uiState.navigationScreen) {
                NavigationScreen.DISASSEMBLY -> {
                    val isValid =
                        if (uiState.navigationScreen == NavigationScreen.DISASSEMBLY) isFormValid else true
                    Pair(R.string.desmontar, isValid)
                }

                NavigationScreen.INSPECTION -> {
                    val isValid = form.validate()
                    Pair(R.string.siguiente, isValid)
                }
            }

            CompleteFormButton(
                text = stringResource(buttonText).uppercase(),
                isValid = isValid
            ) {
                when (uiState.navigationScreen) {
                    NavigationScreen.DISASSEMBLY -> {
                        if (causesSelected != null && destinationSelected != null) {
                            onDismount(causesSelected?.id ?: 0, destinationSelected?.id ?: 0)
                        }
                    }

                    NavigationScreen.INSPECTION -> {

                        val ui = form.toUiOrNull()
                        if (ui != null) {
                            onInspectionFinish(ui)
                        } else scope.launch {
                            snackbar.showMessage(errorMessage)
                        }
                    }
                }

            }
        }
    ) { innerPadding ->
        when (uiState.screenLoadStatus) {
            OperationStatus.Error -> {
                ErrorView(
                    modifier
                        .safeContentPadding()
                        .padding(innerPadding)
                )
            }

            OperationStatus.Loading -> {
                CircularLoading(
                    modifier
                        .safeContentPadding()
                        .padding(innerPadding)
                )
            }

            OperationStatus.Success -> {
                when (uiState.navigationScreen) {
                    NavigationScreen.DISASSEMBLY -> {
                        DisassemblyTireContent(
                            onCauseSelected = {
                                causesSelected = it
                            },
                            onDestinationSelected = {
                                destinationSelected = it
                            },
                            uiState = uiState,
                            causesSelected = causesSelected,
                            destinationSelected = destinationSelected,
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        )
                    }

                    NavigationScreen.INSPECTION -> {
                        InspectionContent(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            form = form,
                            lastOdometer = uiState.lastOdometer,
                            isOdometerEditable = true,
                            showReportList = false,
                            temperatureUnit = uiState.temperatureUnit.symbol,
                            pressureUnit = uiState.pressureUnit.symbol,
                            odometerUnit = uiState.odometerUnit.symbol
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DisassemblyTireContent(
    causesSelected: CatalogItem?,
    uiState: DisassemblyUiState,
    modifier: Modifier = Modifier,
    destinationSelected: CatalogItem? = null,
    onCauseSelected: (CatalogItem?) -> Unit = {},
    onDestinationSelected: (CatalogItem?) -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        TireInfoCard(
            uiState.tire, modifier = Modifier.height(140.dp)
        )
        CatalogDropdown(
            catalog = uiState.disassemblyCauseList,
            selected = causesSelected?.description,
            errorText = causesSelected.validate(),
            label = stringResource(R.string.motivo_de_desmontaje),
            onSelected = {
                onCauseSelected(it)
            },
            modifier = Modifier.fillMaxWidth()
        )
        CatalogDropdown(
            catalog = uiState.destinationList,
            selected = destinationSelected?.description,
            errorText = destinationSelected.validate(),
            onSelected = { onDestinationSelected(it) },
            label = stringResource(R.string.destino),
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
@Preview(showBackground = true)
fun DisassemblyTireViewPreview() {
    HombreCamionTheme {
        DisassemblyTireContent(
            causesSelected = null,
            uiState = DisassemblyUiState(),
            destinationSelected = null,
            onCauseSelected = {},
            onDestinationSelected = {}
        )
    }
}
