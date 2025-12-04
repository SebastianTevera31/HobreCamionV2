package com.rfz.appflotal.presentation.ui.registrollantasscreen.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.AppLocale
import com.rfz.appflotal.data.model.tire.response.TireListResponse
import com.rfz.appflotal.presentation.ui.commonscreens.listmanager.screen.AddItemDialog
import com.rfz.appflotal.presentation.ui.commonscreens.listmanager.screen.ItemDialog
import com.rfz.appflotal.presentation.ui.languaje.LocalizedApp
import com.rfz.appflotal.presentation.ui.registrollantasscreen.viewmodel.NuevoRegistroLlantasUiState
import com.rfz.appflotal.presentation.ui.registrollantasscreen.viewmodel.NuevoRegistroLlantasViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoRegistroLlantasScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: NuevoRegistroLlantasViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.registro_de_llantas),
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    MaterialTheme.colorScheme.tertiaryContainer
                                )
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    SearchBar(
                        uiState.value.searchQuery,
                        onClearSearchQuery = viewModel::onClearQuery,
                        onQueryChanged = viewModel::onSearchQueryChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(16.dp)),
                    )
                }
            }

        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::onAddNewTireClicked,
                modifier = Modifier.shadow(elevation = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.agregar_llanta),
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.value.isLoading && uiState.value.tires.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                TireList(
                    tires = uiState.value.displayedTires,
                    onEdit = viewModel::onEditTireClicked,
                    modifier = Modifier
                )
            }
        }

        if (uiState.value.isDialogShown) {
            TireDialog(uiState = uiState.value, viewModel = viewModel, onShowMessage = {
                if (uiState.value.isSending) {
                    uiState.value.errorMessage?.let {
                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onClearSearchQuery: () -> Unit,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = modifier,
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = stringResource(R.string.buscar),
                tint = Color.White,
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearSearchQuery) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.limpiar),
                        tint = Color.White
                    )
                }
            }
        },
        placeholder = {
            Text(
                "${stringResource(R.string.buscar)}...",
                color = Color.White
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                alpha = 0.4f
            ),
            cursorColor = MaterialTheme.colorScheme.onSecondaryContainer,
            focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
            unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
            focusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                alpha = 0.1f
            ),
            unfocusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                alpha = 0.1f
            )
        ),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
    )
}

@Composable
private fun TireList(
    tires: List<TireListResponse>,
    onEdit: (TireListResponse) -> Unit,
    modifier: Modifier = Modifier
) {
    if (tires.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_se_encontraron_llantas))
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier.fillMaxSize()
        ) {
            items(tires) { tire ->
                TireItem(tire = tire, onEdit = { onEdit(tire) })
            }
        }
    }
}

@Composable
private fun TireItem(tire: TireListResponse, onEdit: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = tire.brand, style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(stringResource(R.string.modelo, tire.model), fontSize = 16.sp)
                Text(stringResource(R.string.tama_o, tire.size), fontSize = 16.sp)
                Text(stringResource(R.string.adquisici_n, tire.typeAcquisition), fontSize = 16.sp)
                Text(stringResource(R.string.id, tire.idTire), fontSize = 12.sp)
            }
            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(R.string.editar_elemento)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TireDialog(
    uiState: NuevoRegistroLlantasUiState,
    viewModel: NuevoRegistroLlantasViewModel,
    onShowMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val acquisitionType = uiState.dialogState.selectedAcquisitionType?.idAcquisitionType
        ?: 0

    val dotWarning =
        if (uiState.dialogState.dot.trim().length > 10) stringResource(R.string.dot_excedio_caracteres) else ""

    val tireNumber =
        if (uiState.dialogState.tireNumber.trim().length > 15) stringResource(R.string.numero_llanta_excedio_caracteres) else ""

    val currentLanguage = AppLocale.currentLocale.collectAsState().value.language
    AddItemDialog(
        title = if (uiState.isEditing) stringResource(R.string.editar_llanta) else stringResource(R.string.nueva_llanta),
        content = {
            LaunchedEffect(uiState.errorMessage) {
                onShowMessage()
            }

            FieldSpinner(
                label = stringResource(R.string.tipo_de_adquisici_n),
                selectedValue = (if (currentLanguage == "es") uiState.dialogState.selectedAcquisitionType?.description
                else uiState.dialogState.selectedAcquisitionType?.enDescription) ?: "",
                values = uiState.acquisitionTypes.map {
                    if (currentLanguage == "es") it.description
                    else it.enDescription
                },
                onValueSelected = { selected ->
                    viewModel.onDialogFieldChange { state ->
                        state.copy(selectedAcquisitionType = uiState.acquisitionTypes.find {
                            (if (currentLanguage == "es") it.description
                            else it.enDescription) == selected
                        })
                    }
                }
            )

            FieldSpinner(
                label = stringResource(R.string.products),
                selectedValue = uiState.dialogState.selectedProduct?.descriptionProduct
                    ?: "",
                values = uiState.products.map { it.descriptionProduct },
                onValueSelected = { selected ->
                    viewModel.onDialogFieldChange {
                        it.copy(
                            selectedProduct = uiState.products.find { p ->
                                p.descriptionProduct == selected
                            },
                            treadDepth = uiState.products.find { p ->
                                p.descriptionProduct == selected
                            }?.treadDepth.toString()
                        )
                    }
                }
            )

            DatePickerField(uiState.dialogState.acquisitionDate) { date ->
                viewModel.onDialogFieldChange {
                    it.copy(
                        acquisitionDate = date
                    )
                }
            }

            DialogTextField(
                label = stringResource(R.string.folio_factura),
                value = uiState.dialogState.folioFactura
            ) { value -> viewModel.onDialogFieldChange { it.copy(folioFactura = value) } }

            DialogTextField(
                label = stringResource(R.string.costo),
                value = uiState.dialogState.cost,
                keyboardType = KeyboardType.Number
            ) { value -> viewModel.onDialogFieldChange { it.copy(cost = value) } }

            DialogTextField(
                label = stringResource(R.string.profundidad),
                value = uiState.dialogState.treadDepth,
                keyboardType = KeyboardType.Number,
                isEditable = acquisitionType != 1 && acquisitionType != 2
            ) { value -> viewModel.onDialogFieldChange { it.copy(treadDepth = value) } }

            DialogTextField(
                label = stringResource(R.string.numero_de_llanta),
                value = uiState.dialogState.tireNumber,
                warningMessage = tireNumber
            ) { value -> viewModel.onDialogFieldChange { it.copy(tireNumber = value) } }

            DialogTextField(
                label = stringResource(R.string.dot),
                value = uiState.dialogState.dot,
                warningMessage = dotWarning
            ) { value -> viewModel.onDialogFieldChange { it.copy(dot = value) } }
        },
        onConfirm = viewModel::saveTire,
        onDismiss = viewModel::onDismissDialog,
        isEntryValid = true,
        modifier = modifier
    )
}

@Composable
private fun DialogTextField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isEditable: Boolean = true,
    warningMessage: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit,
) {
    Column {
        ItemDialog(
            label = label,
            value = value,
            isEmpty = false,
            isEditable = isEditable,
            onValueChange = onValueChange,
            modifier = modifier,
            keyboardType = keyboardType
        )
        if (warningMessage.isNotEmpty()) {
            Text(
                text = warningMessage,
                color = Color.Red,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FieldSpinner(
    label: String,
    selectedValue: String,
    values: List<String>,
    onValueSelected: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded }
        ) {
            OutlinedTextField(
                value = selectedValue,
                onValueChange = {}, // No-op
                readOnly = true,
                label = { Text(label) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            )
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                values.forEach { value ->
                    DropdownMenuItem(
                        text = { Text(value) },
                        onClick = {
                            onValueSelected(value)
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(selectedDate: String, onDateSelected: (String) -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = selectedDate.substringBefore('T'),
        onValueChange = {}, // No-op
        readOnly = true,
        label = { Text(stringResource(R.string.fecha_de_adquisicion)) },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = stringResource(R.string.seleccionar_fecha),
                modifier = Modifier.clickable { showDatePicker = true; focusManager.clearFocus() }
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter =
                                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                            onDateSelected(formatter.format(Date(millis)))
                        }
                        showDatePicker = false
                    }
                ) {
                    LocalizedApp {
                        Text(
                            stringResource(R.string.guardar)
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDatePicker = false
                }) {
                    LocalizedApp {
                        Text(stringResource(R.string.cancelar))
                    }
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}