package com.rfz.appflotal.presentation.ui.registrollantasscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.tire.response.TireListResponse
import com.rfz.appflotal.presentation.ui.languaje.LocalizedApp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoRegistroLlantasScreen(
    navController: NavController,
    viewModel: NuevoRegistroLlantasViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Registro de Llantas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::onAddNewTireClicked) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Llanta")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            SearchBar(uiState.searchQuery, viewModel::onSearchQueryChanged)
            Spacer(Modifier.height(16.dp))

            if (uiState.isLoading && uiState.tires.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                TireList(tires = uiState.displayedTires, onEdit = viewModel::onEditTireClicked)
            }
        }

        if (uiState.isDialogShown) {
            TireDialog(uiState = uiState, viewModel = viewModel)
        }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChanged: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Buscar llanta...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
    )
}

@Composable
private fun TireList(tires: List<TireListResponse>, onEdit: (TireListResponse) -> Unit) {
    if (tires.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No se encontraron llantas.")
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tires) { tire ->
                TireItem(tire = tire, onEdit = { onEdit(tire) })
            }
        }
    }
}

@Composable
private fun TireItem(tire: TireListResponse, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(tire.brand, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TireDialog(
    uiState: NuevoRegistroLlantasUiState,
    viewModel: NuevoRegistroLlantasViewModel
) {
    Dialog(onDismissRequest = viewModel::onDismissDialog) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isLoadingDialogData) {
                Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    item {
                        Text(
                            if (uiState.isEditing) "Editar Llanta" else "Nueva Llanta",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }

                    item { Spacer(Modifier.height(16.dp)) }

                    item {
                        FieldSpinner(
                            label = "Tipo de adquisición",
                            selectedValue = uiState.dialogState.selectedAcquisitionType?.description
                                ?: "",
                            values = uiState.acquisitionTypes.map { it.description },
                            onValueSelected = { selected ->
                                viewModel.onDialogFieldChange { state ->
                                    state.copy(selectedAcquisitionType = uiState.acquisitionTypes.find { it.description == selected })
                                }
                            }
                        )
                    }

                    item {
                        FieldSpinner(
                            label = "Producto",
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
                    }

                    item {
                        DatePickerField(uiState.dialogState.acquisitionDate) { date ->
                            viewModel.onDialogFieldChange {
                                it.copy(
                                    acquisitionDate = date
                                )
                            }
                        }
                    }

                    item {
                        DialogTextField(
                            label = "Folio Factura",
                            value = uiState.dialogState.folioFactura
                        ) { value -> viewModel.onDialogFieldChange { it.copy(folioFactura = value) } }
                    }

                    item {
                        DialogTextField(
                            label = "Costo",
                            value = uiState.dialogState.cost,
                            keyboardType = KeyboardType.Number
                        ) { value -> viewModel.onDialogFieldChange { it.copy(cost = value) } }
                    }

                    item {
                        DialogTextField(
                            label = "Profundidad (mm)",
                            value = uiState.dialogState.treadDepth,
                            keyboardType = KeyboardType.Number
                        ) { value -> viewModel.onDialogFieldChange { it.copy(treadDepth = value) } }
                    }

                    item {
                        DialogTextField(
                            label = "Número de llanta",
                            value = uiState.dialogState.tireNumber
                        ) { value -> viewModel.onDialogFieldChange { it.copy(tireNumber = value) } }
                    }

                    item {
                        DialogTextField(
                            label = "DOT",
                            value = uiState.dialogState.dot
                        ) { value -> viewModel.onDialogFieldChange { it.copy(dot = value) } }
                    }

                    item { Spacer(Modifier.height(16.dp)) }

                    item {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = viewModel::onDismissDialog) {
                                Text("Cancelar")
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = viewModel::saveTire) {
                                LocalizedApp {
                                    Text(stringResource(R.string.save))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogTextField(
    label: String,
    value: String,
    isEditable: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        enabled = isEditable,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next)
    )
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
                contentDescription = "Seleccionar fecha",
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
                            stringResource(R.string.save)
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