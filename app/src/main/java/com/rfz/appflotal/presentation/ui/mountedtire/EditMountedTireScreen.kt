package com.rfz.appflotal.presentation.ui.mountedtire

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.AppLocale
import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.data.repository.UnidadOdometro
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.components.CatalogDropdown
import com.rfz.appflotal.presentation.ui.mountedtire.model.EditMountedTireUiState
import com.rfz.appflotal.presentation.ui.mountedtire.model.MountedTireSummary
import com.rfz.appflotal.presentation.ui.mountedtire.viewmodel.EditMountedTireViewModel
import com.rfz.appflotal.presentation.ui.registertires.asCatalogItem
import com.rfz.appflotal.presentation.ui.registertires.localizedDescription
import com.rfz.appflotal.presentation.ui.registrollantasscreen.screens.DatePickerField
import com.rfz.appflotal.presentation.ui.registrollantasscreen.screens.DialogTextField
import com.rfz.appflotal.presentation.ui.utils.OperationStatus

private val MountedColor = Color(0xFF2E7D32)

@Composable
fun EditMountedTireScreen(
    position: String,
    viewModel: EditMountedTireViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val errorMessageText = uiState.errorRes?.let { stringResource(it) }

    LaunchedEffect(position) { viewModel.load(position) }

    LaunchedEffect(uiState.saveStatus) {
        if (uiState.saveStatus == OperationStatus.Success) {
            Toast.makeText(context, R.string.llanta_actualizada, Toast.LENGTH_SHORT).show()
            viewModel.consumeSaveStatus()
            onBack()
        }
    }

    LaunchedEffect(uiState.errorRes, uiState.errorMessage) {
        val text = errorMessageText ?: return@LaunchedEffect
        val detail = uiState.errorMessage
        val fullText = text + if (detail.isNullOrBlank()) "" else "\n$detail"
        Toast.makeText(context, fullText, Toast.LENGTH_LONG).show()
        viewModel.clearError()
    }

    EditMountedTireContent(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::retry,
        onSave = viewModel::save,
        onAcquisitionTypeSelected = viewModel::updateAcquisitionType,
        onProductSelected = viewModel::updateProduct,
        onAcquisitionDateChange = viewModel::updateAcquisitionDate,
        onFolioFacturaChange = viewModel::updateFolioFactura,
        onCostChange = viewModel::updateCost,
        onTreadDepthChange = viewModel::updateTreadDepth,
        onTireNumberChange = viewModel::updateTireNumber,
        onDotChange = viewModel::updateDot,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditMountedTireContent(
    uiState: EditMountedTireUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onSave: () -> Unit,
    onAcquisitionTypeSelected: (CatalogItem?) -> Unit,
    onProductSelected: (CatalogItem?) -> Unit,
    onAcquisitionDateChange: (String) -> Unit,
    onFolioFacturaChange: (String) -> Unit,
    onCostChange: (String) -> Unit,
    onTreadDepthChange: (String) -> Unit,
    onTireNumberChange: (String) -> Unit,
    onDotChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val language = AppLocale.currentLocale.collectAsState().value.language
    val isSaving = uiState.saveStatus == OperationStatus.Loading

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.editar_llanta_posicion, uiState.position),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.padding(start = 8.dp)) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.regresar),
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
            if (uiState.loadStatus == OperationStatus.Success) {
                Box(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = onSave,
                        enabled = uiState.isFormValid && !isSaving,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Text(text = stringResource(R.string.guardar_cambios))
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        when (uiState.loadStatus) {
            OperationStatus.Success -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                uiState.summary?.let { summary ->
                    MountSummaryCard(position = uiState.position, summary = summary)
                }

                CatalogDropdown(
                    catalog = uiState.acquisitionTypes.map { it.asCatalogItem(language) },
                    selected = uiState.selectedAcquisitionType?.localizedDescription(language),
                    errorText = null,
                    onSelected = onAcquisitionTypeSelected,
                    label = stringResource(R.string.tipo_de_adquisici_n)
                )

                CatalogDropdown(
                    catalog = uiState.products.map { it.asCatalogItem() },
                    selected = uiState.selectedProduct?.descriptionProduct,
                    errorText = null,
                    onSelected = onProductSelected,
                    label = stringResource(R.string.producto_llanta)
                )

                DatePickerField(uiState.acquisitionDate) { date -> onAcquisitionDateChange(date) }

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
                    keyboardType = KeyboardType.NumberPassword
                ) { value -> onTreadDepthChange(value) }

                DialogTextField(
                    label = stringResource(R.string.numero_de_llanta),
                    value = uiState.tireNumber,
                    warningMessage = if (uiState.isTireNumberTooLong) {
                        stringResource(R.string.numero_llanta_excedio_caracteres)
                    } else ""
                ) { value -> onTireNumberChange(value) }

                DialogTextField(
                    label = stringResource(R.string.dot),
                    value = uiState.dot,
                    warningMessage = if (uiState.isDotTooLong) {
                        stringResource(R.string.dot_excedio_caracteres)
                    } else ""
                ) { value -> onDotChange(value) }
            }

            OperationStatus.Error -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
            ) {
                Text(
                    text = stringResource(R.string.error_cargar_llanta),
                    textAlign = TextAlign.Center
                )
                OutlinedButton(onClick = onRetry, shape = RoundedCornerShape(12.dp)) {
                    Text(stringResource(R.string.reintentar))
                }
            }

            else -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

/** Resumen del montaje: es solo referencia, esta pantalla no lo modifica. */
@Composable
private fun MountSummaryCard(position: String, summary: MountedTireSummary) {
    val axleDescription = summary.axleDescription
    val axleText = if (axleDescription != null) {
        stringResource(R.string.montada_en_eje, axleDescription)
    } else {
        stringResource(R.string.leyenda_montada)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(MountedColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = position, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = axleText,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${summary.assemblyDate} · ${summary.odometer} ${summary.odometerUnit.symbol}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.editar_llanta_ayuda),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EditMountedTireContentPreview() {
    HombreCamionTheme {
        EditMountedTireContent(
            uiState = EditMountedTireUiState(
                position = "P3",
                loadStatus = OperationStatus.Success,
                summary = MountedTireSummary(
                    axleDescription = "Eje 2",
                    assemblyDate = "2026-10-21",
                    odometer = 152300,
                    odometerUnit = UnidadOdometro.KILOMETROS
                ),
                acquisitionDate = "2026-10-21",
                folioFactura = "F-10234",
                cost = "8500",
                treadDepth = "18",
                tireNumber = "LL-0042",
                dot = "B6XY2425"
            ),
            onBack = {},
            onRetry = {},
            onSave = {},
            onAcquisitionTypeSelected = {},
            onProductSelected = {},
            onAcquisitionDateChange = {},
            onFolioFacturaChange = {},
            onCostChange = {},
            onTreadDepthChange = {},
            onTireNumberChange = {},
            onDotChange = {}
        )
    }
}
