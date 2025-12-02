package com.rfz.appflotal.presentation.ui.tirewastepile.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.rfz.appflotal.presentation.ui.tirewastepile.viewmodel.TireWasteUiState
import com.rfz.appflotal.presentation.ui.tirewastepile.viewmodel.TireWasteViewModel
import com.rfz.appflotal.presentation.ui.utils.OperationStatus
import com.rfz.appflotal.presentation.ui.utils.validate

@Composable
fun TireWastePileScreen(
    onBack: () -> Unit,
    viewModel: TireWasteViewModel,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.cleanUiState()
        }
    }

    // Apartado de la lógica "when()" por efecto visual
    LaunchedEffect(uiState.value.operationStatus) {
        if (uiState.value.operationStatus is OperationStatus.Success) {
            Toast.makeText(
                context,
                context.getString(R.string.llanta_enviada_desecho),
                Toast.LENGTH_SHORT
            ).show()

            onBack()
        }
    }

    TireWasteView(
        uiState = uiState.value,
        onBack = onBack,
        onSelectedTire = { tireId ->
            viewModel.updateSelectedTire(tireId)
        },
        onError = {
            Toast.makeText(
                context,
                context.getString(R.string.error_enviar_desecho),
                Toast.LENGTH_SHORT
            ).show()
            viewModel.cleanOperationStatus()
        },
        modifier = modifier
    ) { wasteReportId, tireId ->
        viewModel.sendTireToTireWastePile(
            wasteReportId = wasteReportId,
            tireId = tireId
        )
    }
}

@Composable
fun TireWasteView(
    uiState: TireWasteUiState,
    onBack: () -> Unit,
    onError: () -> Unit,
    onSelectedTire: (tireId: Int) -> Unit,
    modifier: Modifier = Modifier,
    onSendTireToWastePile: (wasteReportId: Int, tireId: Int) -> Unit,
) {
    val scroll = rememberScrollState()
    var wasteReportSelected: CatalogItem? by remember { mutableStateOf(null) }
    var tireSelected: CatalogItem? by remember { mutableStateOf(null) }

    val areFormValid by remember {
        derivedStateOf {
            wasteReportSelected != null && tireSelected != null
        }
    }

    when (uiState.operationStatus) {
        is OperationStatus.Error -> {
            LaunchedEffect(uiState.operationStatus) {
                wasteReportSelected = null
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
                title = stringResource(R.string.pila_de_desecho),
                onBack = onBack,
            )
        },
        bottomBar = {
            CompleteFormButton(
                textButton = stringResource(R.string.enviar_a_desecho),
                isValid = areFormValid,
                onFinish = {
                    onSendTireToWastePile(
                        wasteReportSelected!!.id,
                        tireSelected!!.id
                    )
                }
            )
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
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .verticalScroll(scroll)
                        .padding(16.dp)
                ) {
                    CatalogDropdown(
                        catalog = uiState.wasteReportList,
                        selected = wasteReportSelected?.description,
                        onSelected = { wasteReportSelected = it },
                        label = stringResource(R.string.reporte_de_desecho),
                        errorText = wasteReportSelected.validate(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    CatalogDropdown(
                        catalog = uiState.dismountedTireList,
                        selected = tireSelected?.description,
                        onSelected = {
                            if (it != null) {
                                onSelectedTire(it.id)
                            }
                            tireSelected = it
                        },
                        label = stringResource(R.string.llantas),
                        errorText = tireSelected.validate(),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    AnimatedVisibility(
                        visible = tireSelected != null,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut(),
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        TireInfoCard(
                            tire = uiState.selectedTire,
                            modifier.width(240.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, locale = "en")
@Composable
fun TireWasteViewPreview() {
    HombreCamionTheme {
        TireWasteView(
            onBack = {},
            onSelectedTire = { _ -> },
            uiState = TireWasteUiState(
                dismountedTireList = listOf(
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
                selectedTire = Tire(
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
            onError = {},
            onSendTireToWastePile = { _, _ -> }
        )
    }
}