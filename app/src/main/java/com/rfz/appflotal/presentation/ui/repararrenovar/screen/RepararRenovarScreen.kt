package com.rfz.appflotal.presentation.ui.repararrenovar.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.pluralStringResource
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
import com.rfz.appflotal.presentation.ui.commonscreens.listmanager.screen.ListItemContent
import com.rfz.appflotal.presentation.ui.components.CatalogDropdown
import com.rfz.appflotal.presentation.ui.components.CompleteFormButton
import com.rfz.appflotal.presentation.ui.components.NumberField
import com.rfz.appflotal.presentation.ui.components.TireInfoCard
import com.rfz.appflotal.presentation.ui.repararrenovar.viewmodel.DestinationSelection
import com.rfz.appflotal.presentation.ui.repararrenovar.viewmodel.RepararRenovarUiState
import com.rfz.appflotal.presentation.ui.repararrenovar.viewmodel.RepararRenovarViewModel
import com.rfz.appflotal.presentation.ui.retreatedesign.screens.DescriptionText
import com.rfz.appflotal.presentation.ui.utils.OperationStatus
import com.rfz.appflotal.presentation.ui.utils.toIntOrError
import com.rfz.appflotal.presentation.ui.utils.validate

enum class RepararRenovarScreens {
    RETREADED_DESIGN,
    HOME
}

@Composable
fun RepararRenovarScreen(
    onBack: () -> Unit,
    viewModel: RepararRenovarViewModel,
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

    LaunchedEffect(uiState.value.operationStatus) {
        val status = uiState.value.operationStatus
        if (status is OperationStatus.Success) {
            Toast.makeText(
                context,
                context.getString(R.string.llanta_enviada_al_destino),
                Toast.LENGTH_SHORT
            ).show()
            onBack()
        } else if (status is OperationStatus.Error) {
            Toast.makeText(context, R.string.error_enviar_destino, Toast.LENGTH_SHORT).show()
            viewModel.cleanOperationStatus()
        }
    }

    RepararRenovarView(
        uiState = uiState.value,
        onBack = onBack,
        onSelectedTire = { tire, destinationId ->
            viewModel.updateSelectedTire(tire, destinationId)
        },
        onSendTireToDestination = {
            viewModel.sendTire()
        },
        onRetreadedDesign = {
            viewModel.updateRetreadedDesign(it)
        },
        onDeleteRetreadedDesign = {
            viewModel.deleteRetreadedDesign()
        },
        onCostChange = {
            viewModel.updateCost(it)
        },
        onDestinationSelected = {
            viewModel.onSelectedDestination(it)
        },
        onRepairCauseSelected = {
            viewModel.onSelectedRepairCause(it)
        },
        modifier = modifier
    )
}

@Composable
fun RepararRenovarView(
    uiState: RepararRenovarUiState,
    onBack: () -> Unit,
    onRetreadedDesign: (designId: Int) -> Unit,
    onDestinationSelected: (CatalogItem?) -> Unit,
    onRepairCauseSelected: (CatalogItem?) -> Unit,
    onDeleteRetreadedDesign: () -> Unit,
    onCostChange: (cost: String) -> Unit,
    onSelectedTire: (tireId: Int, destinationId: Int) -> Unit,
    onSendTireToDestination: () -> Unit,
    modifier: Modifier = Modifier
) {
    var navScreens by remember { mutableStateOf(RepararRenovarScreens.HOME) }

    if (navScreens == RepararRenovarScreens.RETREADED_DESIGN) {
        BackHandler {
            navScreens = RepararRenovarScreens.HOME
        }
    }

    val selectionId = if (uiState.selectedDestination?.id == DestinationSelection.REPARAR.id) {
        uiState.selectedRepairCause?.id
    } else {
        uiState.selectedRetreadedDesign?.idDesign
    }

    val tireList = when (uiState.selectedDestination?.id) {
        DestinationSelection.REPARAR.id -> uiState.repairedTireList
        DestinationSelection.RENOVAR.id -> uiState.retreadedTireList
        else -> emptyList()
    }

    val tireCost = uiState.tireCost.toString()

    val areFormValid = uiState.selectedDestination != null &&
            uiState.selectedTire != null &&
            tireCost.toIntOrError().second == null &&
            selectionId != null

    Scaffold(
        modifier = modifier,
        topBar = {
            SimpleTopBar(
                title = stringResource(R.string.renovar_reparar),
                onBack = {
                    if (navScreens == RepararRenovarScreens.HOME) onBack()
                    else navScreens = RepararRenovarScreens.HOME
                },
            )
        },
        bottomBar = {
            CompleteFormButton(
                textButton = stringResource(R.string.enviar_rueda),
                isValid = areFormValid,
                onFinish = {
                    onSendTireToDestination()
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
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    when (navScreens) {
                        RepararRenovarScreens.RETREADED_DESIGN -> {
                            items(uiState.retreadDesignList) { item ->
                                ListItemContent(
                                    title = item.description,
                                    isEditable = false,
                                    onEditClick = {},
                                    itemContent = {
                                        DescriptionText(
                                            title = stringResource(R.string.profundidad_de_piso),
                                            description = item.treadDepth.toString()
                                        )
                                        DescriptionText(
                                            title = stringResource(R.string.utilizacion),
                                            description = item.utilization
                                        )
                                        DescriptionText(
                                            title = pluralStringResource(
                                                R.plurals.marca_renovado_tag,
                                                1
                                            )
                                        )
                                        Text(text = item.retreadBrand)
                                    },
                                    modifier = Modifier.clickable {
                                        onRetreadedDesign(item.idDesign)
                                        navScreens = RepararRenovarScreens.HOME
                                    }
                                )
                            }
                        }

                        RepararRenovarScreens.HOME -> {
                            item {
                                CatalogDropdown(
                                    catalog = uiState.destinationList,
                                    selected = uiState.selectedDestination?.description,
                                    onSelected = {
                                        onDestinationSelected(it)
                                        if (it?.id == DestinationSelection.REPARAR.id) {
                                            onDeleteRetreadedDesign()
                                        } else {
                                            onRepairCauseSelected(null)
                                        }
                                    },
                                    label = stringResource(R.string.destino),
                                    errorText = uiState.selectedDestination?.validate(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            item {
                                CatalogDropdown(
                                    catalog = tireList,
                                    selected = uiState.selectedTire?.description,
                                    onSelected = {
                                        if (it != null) {
                                            onSelectedTire(
                                                it.id,
                                                uiState.selectedDestination?.id ?: 0
                                            )
                                        }
                                    },
                                    label = stringResource(R.string.llantas),
                                    errorText = uiState.selectedTire.validate(),
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }

                            item {
                                NumberField(
                                    value = tireCost,
                                    onValueChange = { onCostChange(it) },
                                    label = stringResource(R.string.costo),
                                    errorText = tireCost.toIntOrError().second,
                                    isEditable = DestinationSelection.REPARAR.id == uiState.selectedDestination?.id,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            item {
                                AnimatedVisibility(
                                    visible = uiState.selectedTire != null,
                                    enter = fadeIn() + expandVertically(),
                                    exit = fadeOut() + shrinkVertically()
                                ) {
                                    TireInfoCard(
                                        tire = uiState.selectedTire,
                                        modifier = Modifier.width(240.dp)
                                    )
                                }
                            }

                            if (DestinationSelection.RENOVAR.id == uiState.selectedDestination?.id) {
                                item {
                                    Button(
                                        onClick = {
                                            navScreens = RepararRenovarScreens.RETREADED_DESIGN
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp),
                                        shape = MaterialTheme.shapes.large
                                    ) {
                                        Text(text = "Seleccione modelo de renovado")
                                    }

                                    if (uiState.selectedRetreadedDesign != null) {
                                        val item = uiState.selectedRetreadedDesign
                                        ListItemContent(
                                            title = item.description,
                                            isEditable = false,
                                            onEditClick = {},
                                            itemContent = {
                                                DescriptionText(
                                                    title = stringResource(R.string.profundidad_de_piso),
                                                    description = item.treadDepth.toString()
                                                )
                                                DescriptionText(
                                                    title = stringResource(R.string.utilizacion),
                                                    description = item.utilization
                                                )
                                                DescriptionText(
                                                    title = pluralStringResource(
                                                        R.plurals.marca_renovado_tag,
                                                        1
                                                    )
                                                )
                                                Text(text = item.retreadBrand)
                                            }
                                        )
                                    }
                                }
                            } else if (DestinationSelection.REPARAR.id == uiState.selectedDestination?.id) {
                                item {
                                    CatalogDropdown(
                                        catalog = uiState.repairCauseList,
                                        selected = uiState.selectedRepairCause?.description,
                                        onSelected = { onRepairCauseSelected(it) },
                                        label = stringResource(R.string.motivo_de_reparacion),
                                        errorText = uiState.selectedRepairCause.validate(),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, locale = "en")
@Composable
fun RepararRenovarPreview() {
    HombreCamionTheme {
        RepararRenovarView(
            uiState = RepararRenovarUiState(
                screenLoadStatus = OperationStatus.Success, selectedTire = Tire(
                    id = 101,
                    description = "Michelin - size: 205/55R16",
                    size = "205/55R16",
                    brand = "Michelin",
                    model = "Primacy 4",
                    thread = 7.5,
                    loadingCapacity = "615"
                )
            ),
            onBack = {},
            onSelectedTire = { _, _ -> },
            onSendTireToDestination = { },
            onRetreadedDesign = { _ -> },
            onDeleteRetreadedDesign = { },
            onCostChange = { },
            onDestinationSelected = {},
            onRepairCauseSelected = {},
        )
    }
}
