package com.rfz.appflotal.presentation.ui.assembly.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.data.model.tire.Tire
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.assembly.viewmodel.AssemblyTireUiState
import com.rfz.appflotal.presentation.ui.assembly.viewmodel.AssemblyTireViewModel
import com.rfz.appflotal.presentation.ui.assembly.viewmodel.OdometerValidation
import com.rfz.appflotal.presentation.ui.assembly.viewmodel.OperationStatus
import com.rfz.appflotal.presentation.ui.assembly.viewmodel.ScreenLoadStatus
import com.rfz.appflotal.presentation.ui.components.AwaitDialog
import com.rfz.appflotal.presentation.ui.components.CatalogDropdown
import com.rfz.appflotal.presentation.ui.components.CompleteFormButton
import com.rfz.appflotal.presentation.ui.components.NumberField
import com.rfz.appflotal.presentation.ui.components.SectionHeader
import com.rfz.appflotal.presentation.ui.components.TireInfoCard
import com.rfz.appflotal.presentation.ui.utils.validate

@Composable
fun AssemblyTireScreen(
    positionTire: String,
    viewModel: AssemblyTireViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState = viewModel.uiState.collectAsState()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(uiState.value.operationStatus) {
        if (uiState.value.operationStatus is OperationStatus.Success) {
            viewModel.cleanUiState()
            onBack()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadDataList(positionTire)
    }

    val title = stringResource(R.string.montaje)

    AssemblyTireView(
        title = title,
        tire = positionTire,
        modifier = modifier,
        uiState = uiState.value,
        validateOdometer = { viewModel.validateOdometer(it) },
        updateTire = { viewModel.updateTireField(it) },
        onError = {
            snackbar.showSnackbar((uiState.value.operationStatus as OperationStatus.Error).message)
            viewModel.restartOperationStatus()
        },
        onBack = {
            viewModel.cleanUiState()
            onBack()
        }
    ) { odometer, idAxle, idTire ->
        viewModel.registerAssemblyTire(
            odometer = odometer,
            idAxle = idAxle,
            idTire = idTire
        )
    }
}

@Composable
fun AssemblyTireView(
    title: String,
    tire: String,
    uiState: AssemblyTireUiState,
    validateOdometer: (String) -> Unit,
    updateTire: (Int?) -> Unit,
    onBack: () -> Unit,
    onError: suspend () -> Unit,
    modifier: Modifier = Modifier,
    onAssembly: (odometer: String, idAxle: Int, idTire: Int) -> Unit
) {
    var odometer by remember { mutableStateOf("") }
    var axleSelected: CatalogItem? by remember { mutableStateOf(null) }
    var tireSelected: CatalogItem? by remember { mutableStateOf(null) }

    val scroll = rememberScrollState()

    val areInputsValid = {
        axleSelected != null && tireSelected != null && uiState.isOdometerValid == OdometerValidation.VALID
    }

    val isFormValid by remember(odometer, axleSelected, tireSelected) {
        derivedStateOf {
            areInputsValid()
        }
    }


    when (uiState.operationStatus) {
        is OperationStatus.Error -> {
            LaunchedEffect(uiState.operationStatus) {
                odometer = uiState.currentOdometer
                axleSelected = null
                tireSelected = null
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
        topBar = {
            SimpleTopBar(
                title = title,
                subTitle = tire,
                onBack = onBack
            )
        },
        bottomBar = {
            if (uiState.screenLoadStatus == ScreenLoadStatus.Success) {
                CompleteFormButton(
                    textButton = stringResource(R.string.montar),
                    isValid = isFormValid
                ) {
                    onAssembly(odometer, axleSelected!!.id, tireSelected!!.id)
                }
            }
        }
    ) { innerPadding ->
        when (uiState.screenLoadStatus) {
            ScreenLoadStatus.Error -> {
                val errorMessage = stringResource(R.string.error_carga_datos)
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(R.drawable.camion_descompuesto),
                            contentDescription = null,
                            modifier = Modifier.size(240.dp)
                        )
                        Text(
                            errorMessage,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            ScreenLoadStatus.Loading -> {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            ScreenLoadStatus.Success -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .verticalScroll(scroll)
                        .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                ) {

                    Spacer(modifier = Modifier.padding(top = 16.dp))

                    CatalogDropdown(
                        catalog = uiState.axleList,
                        selected = axleSelected?.description,
                        errorText = axleSelected.validate(),
                        onSelected = { axleSelected = it },
                        label = stringResource(R.string.eje),
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.padding(top = 16.dp))

                    CatalogDropdown(
                        catalog = uiState.tireList,
                        selected = tireSelected?.description,
                        errorText = tireSelected.validate(),
                        onSelected = {
                            updateTire(it?.id)
                            tireSelected = it
                        },
                        label = stringResource(R.string.llantas),
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    AnimatedVisibility(
                        visible = uiState.currentTire != null,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut(),
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        TireInfoCard(
                            tire = uiState.currentTire
                        )
                    }

                    SectionHeader(
                        text = stringResource(R.string.odometro),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        stringResource(R.string.registro_actual, uiState.currentOdometer),
                        modifier = Modifier.fillMaxWidth()
                    )
                    NumberField(
                        value = odometer,
                        onValueChange = {
                            validateOdometer(it)
                            odometer = it
                        },
                        placeHolderText = uiState.currentOdometer,
                        label = "",
                        errorText = uiState.isOdometerValid.message,
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun AssemblyTireViewPreview() {
    HombreCamionTheme {
        AssemblyTireView(
            title = "Montaje",
            tire = "P1",
            uiState = AssemblyTireUiState(
                tireList = listOf(
                    Tire(
                        id = 101,
                        description = "Michelin - size: 205/55R16",
                        size = "205/55R16",
                        brand = "Michelin",
                        model = "Primacy 4",
                        thread = 7.5,
                        loadingCapacity = "615"
                    )
                ),
                currentTire = Tire(
                    id = 101,
                    description = "Michelin - size: 205/55R16",
                    size = "205/55R16",
                    brand = "Michelin",
                    model = "Primacy 4",
                    thread = 7.5,
                    loadingCapacity = "615"
                ),
                axleList = emptyList(),
                currentOdometer = "123456",
                isOdometerValid = OdometerValidation.VALID,
                screenLoadStatus = ScreenLoadStatus.Success
            ),
            validateOdometer = {},
            onBack = {},
            updateTire = {},
            onError = {}
        ) { _, _, _ -> }
    }
}
