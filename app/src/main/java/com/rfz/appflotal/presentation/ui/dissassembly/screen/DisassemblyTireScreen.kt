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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.data.model.tire.Tire
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
import com.rfz.appflotal.presentation.ui.utils.OperationStatus
import com.rfz.appflotal.presentation.ui.utils.validate

@Composable
fun DisassemblyTireScreen(
    positionTire: String,
    viewModel: DisassemblyViewModel,
    onBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(positionTire) {
        viewModel.loadData(tirePosition = positionTire)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.cleanUiState()
        }
    }

    LaunchedEffect(uiState.value.operationStatus) {
        if (uiState.value.operationStatus == OperationStatus.Success) {
            Toast.makeText(
                context,
                context.getString(R.string.desmontaje_exitoso), Toast.LENGTH_SHORT
            ).show()
            onFinish()
        }
    }

    DisassemblyTireView(
        uiState = uiState.value,
        onBack = {
            viewModel.cleanUiState()
            onBack()
        },
        onError = {
            Toast.makeText(
                context,
                context.getString(R.string.error_desmontaje), Toast.LENGTH_SHORT
            ).show()
            viewModel.restartOperationStatus()
        },
        onDismount = { causeId, destinationId ->
            viewModel.dismountTire(causeId, destinationId)
        },
        modifier = modifier
    )
}

@Composable
fun DisassemblyTireView(
    uiState: DisassemblyUiState,
    onBack: () -> Unit,
    onDismount: (causeId: Int, destinationId: Int) -> Unit,
    onError: suspend () -> Unit,
    modifier: Modifier = Modifier
) {
    var causesSelected: CatalogItem? by remember { mutableStateOf(null) }
    var destinationSelected: CatalogItem? by remember { mutableStateOf(null) }
    val scroll = rememberScrollState()

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
        topBar = {
            SimpleTopBar(
                title = stringResource(R.string.desmontaje),
                subTitle = uiState.positionTire,
                onBack = onBack
            )
        },
        bottomBar = {
            CompleteFormButton(
                textButton = stringResource(R.string.desmontar),
                isValid = isFormValid
            ) {
                if (causesSelected != null && destinationSelected != null) {
                    onDismount(causesSelected?.id ?: 0, destinationSelected?.id ?: 0)
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .verticalScroll(scroll)
                        .safeContentPadding()
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
                            causesSelected = it
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    CatalogDropdown(
                        catalog = uiState.destinationList,
                        selected = destinationSelected?.description,
                        errorText = destinationSelected.validate(),
                        onSelected = { destinationSelected = it },
                        label = stringResource(R.string.destino),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun DisassemblyTireViewPreview() {
    HombreCamionTheme {
        DisassemblyTireView(
            onError = {},
            onBack = {},
            onDismount = { _, _ -> },
            uiState = DisassemblyUiState(
                positionTire = "P1",
                tire = Tire(
                    id = 101,
                    description = "Michelin - size: 205/55R16",
                    size = "205/55R16",
                    brand = "Michelin",
                    model = "Primacy 4",
                    thread = 7.5,
                    loadingCapacity = "615"
                ),
                screenLoadStatus = OperationStatus.Success
            ),
        )
    }
}