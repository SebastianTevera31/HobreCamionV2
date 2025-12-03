package com.rfz.appflotal.presentation.ui.assembly.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.data.model.tire.Tire
import com.rfz.appflotal.presentation.commons.CircularLoading
import com.rfz.appflotal.presentation.commons.ErrorView
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.assembly.viewmodel.AssemblyTireUiState
import com.rfz.appflotal.presentation.ui.assembly.viewmodel.AssemblyTireViewModel
import com.rfz.appflotal.presentation.ui.assembly.viewmodel.OdometerValidation
import com.rfz.appflotal.presentation.ui.components.AwaitDialog
import com.rfz.appflotal.presentation.ui.components.CatalogDropdown
import com.rfz.appflotal.presentation.ui.components.CompleteFormButton
import com.rfz.appflotal.presentation.ui.components.NumberField
import com.rfz.appflotal.presentation.ui.components.SectionHeader
import com.rfz.appflotal.presentation.ui.components.TireInfoCard
import com.rfz.appflotal.presentation.ui.components.TireListScreen
import com.rfz.appflotal.presentation.ui.utils.OperationStatus
import com.rfz.appflotal.presentation.ui.utils.SubScreens
import com.rfz.appflotal.presentation.ui.utils.validate

@Composable
fun AssemblyTireScreen(
    positionTire: String,
    viewModel: AssemblyTireViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState = viewModel.uiState.collectAsState()
    val context = LocalContext.current

    DisposableEffect(Unit) {
        onDispose {
            viewModel.cleanUiState()
        }
    }

    LaunchedEffect(uiState.value.operationStatus) {
        if (uiState.value.operationStatus is OperationStatus.Success) {
            Toast.makeText(context, context.getString(R.string.montaje_exitoso), Toast.LENGTH_SHORT)
                .show()
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
            Toast.makeText(context, context.getString(R.string.error_montaje), Toast.LENGTH_SHORT)
                .show()
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

    var navScreens by remember { mutableStateOf(SubScreens.HOME) }

    if (navScreens == SubScreens.LIST) {
        BackHandler {
            navScreens = SubScreens.HOME
        }
    }

    val isFormValid =
        axleSelected != null && tireSelected != null && uiState.isOdometerValid == OdometerValidation.VALID

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
                onBack = {
                    if (navScreens == SubScreens.HOME) onBack()
                    else navScreens = SubScreens.HOME
                }
            )
        },
        bottomBar = {
            if (uiState.screenLoadStatus == OperationStatus.Success) {
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
            OperationStatus.Error -> {
                ErrorView(modifier = Modifier.padding(innerPadding))
            }

            OperationStatus.Loading -> {
                CircularLoading(modifier.padding(innerPadding))
            }

            OperationStatus.Success -> {
                when (navScreens) {
                    SubScreens.LIST -> {
                        TireListScreen(
                            tires = uiState.tireList,
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            updateTire(it.id)
                            tireSelected = it
                            navScreens = SubScreens.HOME
                        }
                    }

                    SubScreens.HOME -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            CatalogDropdown(
                                catalog = uiState.axleList,
                                selected = axleSelected?.description,
                                errorText = axleSelected.validate(),
                                onSelected = { axleSelected = it },
                                label = stringResource(R.string.eje),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    navScreens = SubScreens.LIST
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = MaterialTheme.shapes.large
                            ) {
                                Text(text = stringResource(R.string.seleccione_una_llanta))
                            }

                            if (uiState.currentTire != null){
                                Text(
                                    text = stringResource(R.string.llanta_seleccionada),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                )
                            }

                            AnimatedVisibility(
                                visible = uiState.currentTire != null,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut(),
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                TireInfoCard(
                                    tire = uiState.currentTire,
                                    modifier.width(240.dp)
                                )
                            }

                            Column {
                                SectionHeader(
                                    text = stringResource(R.string.odometro),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                val odometerValue = uiState.currentOdometer.toIntOrNull() ?: 0
                                Text(
                                    text = stringResource(
                                        R.string.advertencia_ingreso_odometro,
                                        odometerValue
                                    ),
                                    style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                                    modifier = Modifier.padding(dimensionResource(R.dimen.small_dimen))
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
                        loadingCapacity = "615",
                        destination = "Oficina"
                    )
                ),
                currentTire = Tire(
                    id = 101,
                    description = "Michelin - size: 205/55R16",
                    size = "205/55R16",
                    brand = "Michelin",
                    model = "Primacy 4",
                    thread = 7.5,
                    loadingCapacity = "615",
                    destination = "Oficina"
                ),
                axleList = emptyList(),
                currentOdometer = "123456",
                isOdometerValid = OdometerValidation.VALID,
                screenLoadStatus = OperationStatus.Success
            ),
            validateOdometer = {},
            onBack = {},
            updateTire = {},
            onError = {}
        ) { _, _, _ -> }
    }
}
