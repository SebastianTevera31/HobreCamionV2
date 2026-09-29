package com.rfz.appflotal.presentation.ui.registertires

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.assembly.viewmodel.OdometerValidation
import com.rfz.appflotal.presentation.ui.components.CatalogDropdown
import com.rfz.appflotal.presentation.ui.registrollantasscreen.screens.DatePickerField
import com.rfz.appflotal.presentation.ui.registrollantasscreen.screens.DialogTextField
import com.rfz.appflotal.presentation.ui.utils.OperationStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterTireScreen(
    positionTire: String,
    viewModel: RegisterTireViewModel,
    onBack: () -> Unit,
    onNavigateToProducts: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(positionTire) {
        viewModel.loadData(positionTire)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.reloadProducts()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(uiState.operationStatus) {
        when (uiState.operationStatus) {
            OperationStatus.Success -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.llanta_registrada_y_montada),
                    Toast.LENGTH_LONG
                ).show()
                viewModel.restartOperationStatus()
                onBack()
            }

            OperationStatus.Error -> {
                Toast.makeText(
                    context,
                    uiState.errorMessage ?: context.getString(R.string.error_desconocido),
                    Toast.LENGTH_LONG
                ).show()
                viewModel.restartOperationStatus()
            }

            else -> {}
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    RegisterTireScreenContent(
        positionTire = positionTire,
        uiState = uiState,
        onBack = onBack,
        onSaveClick = { viewModel.saveAndMount(context) },
        onAcquisitionTypeSelected = viewModel::updateAcquisitionType,
        onProductSelected = viewModel::updateProduct,
        onAxleSelected = viewModel::updateAxle,
        onAcquisitionDateChange = viewModel::updateAcquisitionDate,
        onFolioFacturaChange = viewModel::updateFolioFactura,
        onCostChange = viewModel::updateCost,
        onTireNumberChange = viewModel::updateTireNumber,
        onDotChange = viewModel::updateDot,
        onOdometerChange = viewModel::updateOdometer,
        onAddProduct = onNavigateToProducts,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterTireScreenContent(
    positionTire: String,
    uiState: RegisterTireUiState,
    onBack: () -> Unit,
    onSaveClick: () -> Unit,
    onAcquisitionTypeSelected: (CatalogItem?) -> Unit,
    onProductSelected: (CatalogItem?) -> Unit,
    onAxleSelected: (CatalogItem?) -> Unit,
    onAcquisitionDateChange: (String) -> Unit,
    onFolioFacturaChange: (String) -> Unit,
    onCostChange: (String) -> Unit,
    onTireNumberChange: (String) -> Unit,
    onDotChange: (String) -> Unit,
    onOdometerChange: (String) -> Unit,
    onAddProduct: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dotWarning =
        if (uiState.dot.trim().length > 10) stringResource(R.string.dot_excedio_caracteres) else ""

    val tireNumberWarning =
        if (uiState.tireNumber.trim().length > 15) stringResource(R.string.numero_llanta_excedio_caracteres) else ""

    val odometerWarning =
        if (uiState.isOdometerValid == OdometerValidation.INVALID) {
            stringResource(R.string.error_odometro_inferior)
        } else ""

    val isFormValid = uiState.selectedAcquisitionType != null &&
            uiState.selectedProduct != null &&
            uiState.selectedAxle != null &&
            uiState.acquisitionDate.isNotBlank() &&
            uiState.folioFactura.isNotBlank() &&
            uiState.cost.isNotBlank() &&
            uiState.treadDepth.isNotBlank() &&
            uiState.tireNumber.isNotBlank() &&
            uiState.dot.isNotBlank() &&
            uiState.isOdometerValid == OdometerValidation.VALID &&
            dotWarning.isEmpty() &&
            tireNumberWarning.isEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.registro_montaje_llanta_titulo, positionTire),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                ),
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
                    .shadow(4.dp)
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = onSaveClick,
                    enabled = isFormValid && uiState.operationStatus != OperationStatus.Loading,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    if (uiState.operationStatus == OperationStatus.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Text(text = stringResource(R.string.guardar))
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        if (uiState.screenLoadStatus == OperationStatus.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                CatalogDropdown(
                    catalog = uiState.acquisitionTypes.map { it.asCatalogItem() },
                    selected = uiState.selectedAcquisitionType?.description,
                    errorText = null,
                    onSelected = onAcquisitionTypeSelected,
                    label = stringResource(R.string.tipo_de_adquisici_n)
                )

                CatalogDropdown(
                    catalog = uiState.products.map { it.asCatalogItem() },
                    selected = uiState.selectedProduct?.descriptionProduct,
                    errorText = null,
                    onSelected = onProductSelected,
                    label = stringResource(R.string.producto_llanta),
                    onAddNewItem = onAddProduct,
                    addNewItemLabel = stringResource(R.string.agregar_producto)
                )

                CatalogDropdown(
                    catalog = uiState.axleList,
                    selected = uiState.selectedAxle?.description,
                    errorText = null,
                    onSelected = onAxleSelected,
                    label = stringResource(R.string.eje)
                )

                DatePickerField(uiState.acquisitionDate) { date ->
                    onAcquisitionDateChange(date)
                }

                DialogTextField(
                    label = stringResource(R.string.folio_factura),
                    value = uiState.folioFactura
                ) { value -> onFolioFacturaChange(value) }

                DialogTextField(
                    label = stringResource(R.string.costo),
                    value = uiState.cost,
                    keyboardType = KeyboardType.NumberPassword
                ) { value -> onCostChange(value) }

                DialogTextField(
                    label = stringResource(R.string.profundidad),
                    value = uiState.treadDepth,
                    keyboardType = KeyboardType.NumberPassword,
                    isEditable = false
                ) { }

                DialogTextField(
                    label = stringResource(R.string.numero_de_llanta),
                    value = uiState.tireNumber,
                    warningMessage = tireNumberWarning
                ) { value -> onTireNumberChange(value) }

                DialogTextField(
                    label = stringResource(R.string.dot),
                    value = uiState.dot,
                    warningMessage = dotWarning
                ) { value -> onDotChange(value) }

                DialogTextField(
                    label = stringResource(R.string.odometro),
                    value = uiState.odometer,
                    keyboardType = KeyboardType.NumberPassword,
                    warningMessage = odometerWarning
                ) { value -> onOdometerChange(value.filter { c -> c.isDigit() }) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterTireScreenPreview() {
    HombreCamionTheme {
        RegisterTireScreenContent(
            positionTire = "P1",
            uiState = RegisterTireUiState(screenLoadStatus = OperationStatus.Success),
            onBack = {},
            onSaveClick = {},
            onAcquisitionTypeSelected = {},
            onProductSelected = {},
            onAxleSelected = {},
            onAcquisitionDateChange = {},
            onFolioFacturaChange = {},
            onCostChange = {},
            onTireNumberChange = {},
            onDotChange = {},
            onOdometerChange = {},
            onAddProduct = {}
        )
    }
}
