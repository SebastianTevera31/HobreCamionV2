package com.rfz.appflotal.presentation.ui.cambiodestino.screen

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.destination.Destination
import com.rfz.appflotal.data.model.tire.Tire
import com.rfz.appflotal.presentation.commons.CircularLoading
import com.rfz.appflotal.presentation.commons.ErrorView
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.cambiodestino.viewmodel.CambioDestinoFormState
import com.rfz.appflotal.presentation.ui.cambiodestino.viewmodel.CambioDestinoUiState
import com.rfz.appflotal.presentation.ui.cambiodestino.viewmodel.CambioDestinoViewModel
import com.rfz.appflotal.presentation.ui.components.CatalogDropdown
import com.rfz.appflotal.presentation.ui.components.CompleteFormButton
import com.rfz.appflotal.presentation.ui.components.TireInfoCard
import com.rfz.appflotal.presentation.ui.components.TireListScreen
import com.rfz.appflotal.presentation.ui.utils.OperationStatus
import com.rfz.appflotal.presentation.ui.utils.SubScreens
import com.rfz.appflotal.presentation.ui.utils.validate

@Composable
fun CambioDestinoScreen(
    onBack: () -> Unit,
    viewModel: CambioDestinoViewModel,
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
                context.getString(
                    R.string.llanta_movida_a,
                    uiState.value.form.selectedDestination?.description
                ), Toast.LENGTH_SHORT
            ).show()
            onBack()
        } else if (status is OperationStatus.Error) {
            Toast.makeText(
                context,
                context.getString(R.string.error_al_mover_la_llanta), Toast.LENGTH_SHORT
            ).show()
            viewModel.cleanOperationStatus()
        }
    }

    CambioDestinoView(
        uiState = uiState.value,
        onBack = onBack,
        onSelectedDestination = { viewModel.onSelectedDestination(it) },
        onSelectedOrigin = { viewModel.onSelectedOrigin(it) },
        onSelectedTire = { viewModel.onSelectedTire(it) },
        onReasonChange = { viewModel.onReasonChange(it) },
        onSendTireToDestination = { viewModel.onSendTireToDestination() },
        modifier = modifier
    )
}

@Composable
fun CambioDestinoView(
    onBack: () -> Unit,
    onSelectedDestination: (destinationId: Int?) -> Unit,
    onSelectedOrigin: (originId: Int?) -> Unit,
    onSelectedTire: (tireId: Int?) -> Unit,
    onReasonChange: (reason: String) -> Unit,
    onSendTireToDestination: () -> Unit,
    uiState: CambioDestinoUiState,
    modifier: Modifier = Modifier
) {
    val scroll = rememberScrollState()
    val context = LocalContext.current
    var navScreens by remember { mutableStateOf(SubScreens.HOME) }

    if (navScreens == SubScreens.LIST) {
        BackHandler {
            navScreens = SubScreens.HOME
        }
    }

    val isValid = uiState.form.selectedDestination != null &&
            uiState.form.selectedTire != null && uiState.form.reason.isNotBlank()

    Scaffold(
        topBar = {
            SimpleTopBar(
                title = stringResource(R.string.cambio_de_destino),
                onBack = {
                    if (navScreens == SubScreens.HOME) onBack()
                    else navScreens = SubScreens.HOME
                }
            )
        },
        bottomBar = {
            CompleteFormButton(
                textButton = stringResource(R.string.enviar_rueda),
                isValid = isValid,
                onFinish = onSendTireToDestination
            )
        },
        modifier = modifier
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
                            tires = uiState.selectedTireList,
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            onSelectedTire(it.id)
                            navScreens = SubScreens.HOME
                        }
                    }

                    SubScreens.HOME -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.small_dimen)),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(scroll)
                        ) {
                            Text(
                                text = stringResource(R.string.seleccione_un_origen),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )

                            CatalogDropdown(
                                catalog = uiState.originList,
                                selected = uiState.form.selectedOrigin?.description,
                                onSelected = { onSelectedOrigin(it?.id) },
                                label = stringResource(R.string.origen),
                                errorText = uiState.form.selectedOrigin.validate(),
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (uiState.form.selectedOrigin != null) {
                                Button(
                                    onClick = {
                                        if (uiState.selectedTireList.isEmpty()) {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.no_se_encontraron_llantas),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else navScreens = SubScreens.LIST
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    shape = MaterialTheme.shapes.large
                                ) {
                                    Text(text = stringResource(R.string.seleccione_una_llanta))
                                }

                                if (uiState.form.selectedTire != null) {
                                    Text(
                                        text = stringResource(R.string.detalles_de_llanta),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    AnimatedVisibility(
                                        visible = true,
                                        enter = expandVertically() + fadeIn(),
                                        exit = shrinkVertically() + fadeOut(),
                                        modifier = Modifier.padding(bottom = dimensionResource(R.dimen.small_dimen))
                                    ) {
                                        TireInfoCard(
                                            tire = uiState.form.selectedTire,
                                            modifier.width(240.dp)
                                        )
                                    }

                                    CatalogDropdown(
                                        catalog = uiState.destinationList,
                                        selected = uiState.form.selectedDestination?.description,
                                        errorText = uiState.form.selectedDestination.validate(),
                                        onSelected = { onSelectedDestination(it?.id) },
                                        modifier = Modifier.fillMaxWidth(),
                                        label = stringResource(R.string.destino)
                                    )

                                    OutlinedTextField(
                                        label = {
                                            Text(
                                                text = stringResource(R.string.motivo),
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        },
                                        value = uiState.form.reason,
                                        onValueChange = { onReasonChange(it) },
                                        singleLine = true,
                                        isError = uiState.form.reason.isBlank(),
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(MaterialTheme.colorScheme.primary.value),
                                            unfocusedBorderColor = Color(MaterialTheme.colorScheme.scrim.value),
                                            focusedLabelColor = Color(MaterialTheme.colorScheme.primary.value),
                                            focusedTextColor = Color.Black,
                                            unfocusedTextColor = Color.Black
                                        ),
                                        shape = MaterialTheme.shapes.large
                                    )
                                    if (uiState.form.reason.isBlank()) {
                                        Text(
                                            text = stringResource(R.string.debe_escribir_el_motivo),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.align(Alignment.Start)
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
}

@Preview(showBackground = true, showSystemUi = true, locale = "en")
@Composable
fun CambioDestinoPreview() {
    HombreCamionTheme {
        CambioDestinoView(
            onBack = {}, uiState = CambioDestinoUiState(
                screenLoadStatus = OperationStatus.Success,
                form = CambioDestinoFormState(
                    selectedTire = Tire(
                        id = 101,
                        description = "Michelin - size: 205/55R16",
                        size = "205/55R16",
                        brand = "Michelin",
                        model = "Primacy 4",
                        thread = 7.5,
                        loadingCapacity = "615",
                        destination = "Oficina"
                    ),
                    selectedOrigin = Destination(1, "Oficina"),
                    selectedDestination = Destination(2, "Oficina"),
                    reason = "Cambio de destino"
                ),
            ),
            onSelectedDestination = { _ -> },
            onSelectedTire = { _ -> },
            onReasonChange = { _ -> },
            onSendTireToDestination = {},
            onSelectedOrigin = {}
        )
    }
}
