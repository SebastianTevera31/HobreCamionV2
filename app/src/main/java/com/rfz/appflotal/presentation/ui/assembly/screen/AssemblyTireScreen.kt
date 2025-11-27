package com.rfz.appflotal.presentation.ui.assembly.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.assembly.viewmodel.AssemblyTireUiState
import com.rfz.appflotal.presentation.ui.assembly.viewmodel.AssemblyTireViewModel
import com.rfz.appflotal.presentation.ui.assembly.viewmodel.OdometerValidation
import com.rfz.appflotal.presentation.ui.assembly.viewmodel.OperationStatus
import com.rfz.appflotal.presentation.ui.components.AwaitDialog
import com.rfz.appflotal.presentation.ui.components.CatalogDropdown
import com.rfz.appflotal.presentation.ui.components.CompleteFormButton
import com.rfz.appflotal.presentation.ui.components.NumberField
import com.rfz.appflotal.presentation.ui.components.SectionHeader
import com.rfz.appflotal.presentation.ui.utils.validate

@Composable
fun AssemblyTireScreen(
    positionTire: String,
    viewModel: AssemblyTireViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState = viewModel.uiState.collectAsState()

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
    modifier: Modifier = Modifier,
    onAssembly: (odometer: String, idAxle: Int, idTire: Int) -> Unit
) {
    var odometer by remember { mutableStateOf(uiState.currentOdometer) }
    var axleSelected: CatalogItem? by remember { mutableStateOf(null) }
    var tireSelected: CatalogItem? by remember { mutableStateOf(null) }

    val snackbar = remember { SnackbarHostState() }
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
        is OperationStatus.Success -> {
            LaunchedEffect(uiState.operationStatus) {
                onBack()
                odometer = ""
                axleSelected = null
                tireSelected = null
            }
        }

        is OperationStatus.Error -> {
            LaunchedEffect(uiState.operationStatus) {
                odometer = ""
                axleSelected = null
                tireSelected = null
                snackbar.showSnackbar(uiState.operationStatus.message)
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
            CompleteFormButton(
                textButton = "Montar",
                isValid = isFormValid
            ) {
                onAssembly(odometer, axleSelected!!.id, tireSelected!!.id)
            }
        }
    ) { innerPadding ->
        if (uiState.tireList != null && uiState.axleList != null) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(scroll)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                SectionHeader(text = "Eje")
                CatalogDropdown(
                    catalog = uiState.axleList,
                    selected = axleSelected?.description,
                    errorText = axleSelected.validate(),
                    onSelected = { axleSelected = it },
                    label = "Eje",
                    modifier = Modifier.fillMaxWidth()
                )

                SectionHeader(text = "Llantas")
                CatalogDropdown(
                    catalog = uiState.tireList,
                    selected = tireSelected?.description,
                    errorText = tireSelected.validate(),
                    onSelected = {
                        updateTire(it?.id)
                        tireSelected = it
                    },
                    label = "Llantas",
                    modifier = Modifier.fillMaxWidth()
                )

                if (uiState.currentTire != null) {
                    Card {
                        Text("Tire: ${uiState.currentTire.id}")
                        Text(
                            text = "Marca: ${uiState.currentTire.brand}"
                        )
                        Text(
                            text = "Modelo: ${uiState.currentTire.model}"
                        )
                        Text(
                            text = "Tamaño: ${uiState.currentTire.size}"
                        )
                        Text(
                            text = "Profundidad: ${uiState.currentTire.description}"
                        )
                        Text(
                            text = "Capacidad: ${uiState.currentTire.loadingCapacity}"
                        )
                    }
                }

                SectionHeader(text = "Odometro")
                Text("Registro actual: ${uiState.currentOdometer}")
                NumberField(
                    value = odometer,
                    onValueChange = {
                        validateOdometer(it)
                        odometer = it
                    },
                    label = "",
                    errorText = uiState.isOdometerValid.message,
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
                tireList = emptyList(),
                axleList = emptyList()
            ),
            validateOdometer = {},
            onBack = {},
            updateTire = {},
        ) { _, _, _ -> }
    }
}
