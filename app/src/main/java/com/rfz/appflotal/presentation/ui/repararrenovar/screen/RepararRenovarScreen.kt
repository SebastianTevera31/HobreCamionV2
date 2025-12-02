package com.rfz.appflotal.presentation.ui.repararrenovar.screen

import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.rfz.appflotal.presentation.ui.components.AwaitDialog
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
        if (uiState.value.operationStatus is OperationStatus.Success) {
            Toast.makeText(
                context,
                context.getString(R.string.llanta_enviada_al_destino),
                Toast.LENGTH_SHORT
            ).show()
            onBack()
        }
    }

    RepararRenovarView(
        uiState = uiState.value,
        onBack = onBack,
        onError = {
            Toast.makeText(
                context,
                context.getString(R.string.error_enviar_destino),
                Toast.LENGTH_SHORT
            ).show()
            viewModel.cleanOperationStatus()
        },
        onSelectedTire = { tire, destinationId ->
            viewModel.updateSelectedTire(tire, destinationId)
        },
        onSendTireToDestination = { destinationId, tireId, unitCost, idSelection ->
            viewModel.sendTire(destinationId, tireId, unitCost.toInt(), idSelection)
        },
        modifier = modifier
    )
}

@Composable
fun RepararRenovarView(
    uiState: RepararRenovarUiState,
    onBack: () -> Unit,
    onError: () -> Unit,
    onSelectedTire: (tireId: Int, destinationId: Int) -> Unit,
    onSendTireToDestination: (destinationId: Int, tireId: Int, unitCost: Double, idSelection: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var destinationSelected: CatalogItem? by remember { mutableStateOf(null) }
    var tireSelected: CatalogItem? by remember { mutableStateOf(null) }
    var unitCost by remember { mutableStateOf("") }
    var repairCause: CatalogItem? by remember { mutableStateOf(null) }
    var retreadedDesignId: Int? by remember { mutableStateOf(null) }

    LaunchedEffect(uiState.tireCost) {
        if (uiState.tireCost > 0) {
            unitCost = uiState.tireCost.toString()
        }
    }

    val tireList = if (destinationSelected == null) emptyList<CatalogItem>()
    else if (destinationSelected!!.id == DestinationSelection.REPARAR.id) uiState.repairedTireList
    else uiState.retreadedTireList

    val selectionId =
        if (destinationSelected?.id == DestinationSelection.REPARAR.id) {
            repairCause?.id
        } else {
            retreadedDesignId
        }

    val areFormValid =
        destinationSelected != null && tireSelected != null && unitCost.isNotBlank() && selectionId != null

    when (uiState.operationStatus) {
        is OperationStatus.Error -> {
            LaunchedEffect(uiState.operationStatus) {
                destinationSelected = null
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
                title = stringResource(R.string.renovar_reparar),
                onBack = onBack,
            )
        },
        bottomBar = {
            CompleteFormButton(
                textButton = stringResource(R.string.enviar_rueda),
                isValid = areFormValid,
                onFinish = {
                    onSendTireToDestination(
                        destinationSelected!!.id,
                        tireSelected!!.id,
                        unitCost.toDouble(),
                        selectionId!!
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
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        CatalogDropdown(
                            catalog = uiState.destinationList,
                            selected = destinationSelected?.description,
                            onSelected = {
                                tireSelected = null
                                unitCost = ""
                                destinationSelected = it
                                if (it?.id == DestinationSelection.REPARAR.id) {
                                    retreadedDesignId = null
                                } else {
                                    repairCause = null
                                }
                            },
                            label = stringResource(R.string.destino),
                            errorText = destinationSelected.validate(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        CatalogDropdown(
                            catalog = tireList,
                            selected = tireSelected?.description,
                            onSelected = {
                                if (it != null) {
                                    onSelectedTire(it.id, destinationSelected?.id ?: 0)
                                    tireSelected = it
                                }
                            },
                            label = stringResource(R.string.llantas),
                            errorText = tireSelected.validate(),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    if (DestinationSelection.RENOVAR.id == destinationSelected?.id) {
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
                                modifier = Modifier.clickable { retreadedDesignId = item.idBrand }
                            )
                        }
                    } else if (DestinationSelection.REPARAR.id == destinationSelected?.id) {
                        item {
                            CatalogDropdown(
                                catalog = uiState.repairCauseList,
                                selected = repairCause?.description,
                                onSelected = { repairCause = it },
                                label = stringResource(R.string.motivo_de_reparacion),
                                errorText = repairCause.validate(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    item {
                        NumberField(
                            value = unitCost,
                            onValueChange = { unitCost = it },
                            errorText = unitCost.toIntOrError().second,
                            modifier = Modifier.fillMaxWidth(),
                            label = stringResource(R.string.costo),
                            isEditable = destinationSelected?.id == DestinationSelection.REPARAR.id,
                        )
                    }

                    item {
                        AnimatedVisibility(
                            visible = tireSelected != null,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut(),
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            TireInfoCard(
                                tire = uiState.selectedTire,
                                modifier = Modifier.width(240.dp)
                            )
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
            onError = {},
            onSelectedTire = { _, _ -> },
            onSendTireToDestination = { _, _, _, _ -> }
        )
    }
}
