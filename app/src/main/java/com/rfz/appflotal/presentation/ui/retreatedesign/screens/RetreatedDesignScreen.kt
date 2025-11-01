package com.rfz.appflotal.presentation.ui.retreatedesign.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rfz.appflotal.R
import com.rfz.appflotal.domain.Catalog
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.commonscreens.listmanager.screen.AddItemDialog
import com.rfz.appflotal.presentation.ui.commonscreens.listmanager.screen.ItemDialog
import com.rfz.appflotal.presentation.ui.commonscreens.listmanager.screen.ListItemContent
import com.rfz.appflotal.presentation.ui.commonscreens.listmanager.screen.ListManagementScreen
import com.rfz.appflotal.presentation.ui.components.AwaitDialog
import com.rfz.appflotal.presentation.ui.retreatedesign.viewmodel.RetreadCatalogDesignFields
import com.rfz.appflotal.presentation.ui.retreatedesign.viewmodel.RetreadDesignFields
import com.rfz.appflotal.presentation.ui.retreatedesign.viewmodel.RetreatedDesignViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RetreatedDesignScreen(
    modifier: Modifier = Modifier,
    viewModel: RetreatedDesignViewModel = hiltViewModel(),
    onBackScreen: () -> Unit
) {
    val state = viewModel.uiState.collectAsState()
    val dialogState = viewModel.dialogState.collectAsState()

    val title = pluralStringResource(R.plurals.disenio_renovado_tag, 2)
    val fieldTitle = pluralStringResource(R.plurals.disenio_renovado_tag, 1)

    val marcasRenovado = viewModel.brandList
    val utilizacion = viewModel.utilizationList

    val ctx = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.setTitle(title)
    }

    LaunchedEffect(true) {
        viewModel.eventFlow.collectLatest { event ->
            Toast.makeText(ctx, event.message, Toast.LENGTH_SHORT).show()
        }
    }

    if (state.value.isSending) {
        AwaitDialog()
    }

    ListManagementScreen(
        state = state.value,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onShowDialog = {
            viewModel.onEditing(false)
            viewModel.onShowDialog()
        },
        onClearSearchQuery = viewModel::onClearQuery,
        listItemContent = { item ->
            ListItemContent(
                title = item.description,
                onEditClick = {
                    viewModel.setItemToDialog(item)
                    viewModel.onShowDialog()
                }
            ) {
                DescriptionText(
                    title = stringResource(R.string.profundidad_de_piso),
                    description = item.treadDepth.toString()
                )
                DescriptionText(
                    title = stringResource(R.string.utilizacion),
                    description = item.utilization
                )
                DescriptionText(title = pluralStringResource(R.plurals.marca_renovado_tag, 1))
                Text(text = item.retreadBrand)
            }
        },
        dialogContent = {
            AddItemDialog(
                title = if (!state.value.isEditing) {
                    stringResource(R.string.agregar_elemento, fieldTitle)
                } else {
                    stringResource(R.string.editar_elemento, fieldTitle)
                },
                content = {
                    ItemDialog(
                        label = pluralStringResource(R.plurals.description_field, 1),
                        value = dialogState.value.description,
                        isEmpty = dialogState.value.description.isBlank(),
                        onValueChange = { description ->
                            viewModel.onDialogFieldChanged(
                                field = RetreadDesignFields.DESCRIPTION,
                                value = description
                            )
                        },
                    )
                    FieldSpinner(
                        label = pluralStringResource(R.plurals.marca_renovado_tag, 1),
                        selectedValue = dialogState.value.marcaRenovado?.description ?: "",
                        isEmpty = dialogState.value.marcaRenovado == null,
                        values = marcasRenovado
                    ) { selectedBrand ->
                        viewModel.onDialogCatalogFieldChanged(
                            field = RetreadCatalogDesignFields.MARCA_RENOVADO,
                            value = selectedBrand
                        )
                    }
                    ItemDialog(
                        label = stringResource(R.string.profundidad_de_piso),
                        value = dialogState.value.profundidadPiso,
                        isEmpty = dialogState.value.profundidadPiso.isBlank(),
                        keyboardType = KeyboardType.Decimal,
                        onValueChange = { description ->
                            viewModel.onDialogFieldChanged(
                                field = RetreadDesignFields.PROFUNDIDAD_PISO,
                                value = description
                            )
                        },
                    )
                    FieldSpinner(
                        label = stringResource(R.string.utilizacion),
                        selectedValue = dialogState.value.utilizacion?.description ?: "",
                        isEmpty = dialogState.value.utilizacion == null,
                        values = utilizacion
                    ) { utilizationItem ->
                        viewModel.onDialogCatalogFieldChanged(
                            field = RetreadCatalogDesignFields.UTILIZACION,
                            value = utilizationItem
                        )
                    }
                },
                onConfirm = viewModel::onSaveItem,
                onDismiss = viewModel::onDismissDialog,
                isEntryValid = dialogState.value.description.isNotBlank()
                        && dialogState.value.profundidadPiso.isNotBlank()
                        && dialogState.value.utilizacion != null
                        && dialogState.value.marcaRenovado != null
            )
        },
        onBackScreen = {
            viewModel.onDismissDialog()
            onBackScreen()
        },
        modifier = modifier,
    )
}

@Composable
fun DescriptionText(title: String, modifier: Modifier = Modifier, description: String = "") {
    Text(buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        ) {
            append("$title: ")
        }
        append(description)
    }, modifier = modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldSpinner(
    modifier: Modifier = Modifier,
    label: String,
    isEmpty: Boolean,
    selectedValue: String,
    values: List<Catalog>,
    onValueSelected: (Catalog) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
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
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(label) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }
                ) {
                    values.forEach { value ->
                        DropdownMenuItem(
                            text = { Text(value.description) },
                            onClick = {
                                onValueSelected(value)
                                isExpanded = false
                            }
                        )
                    }
                }
            }
        }
        if (isEmpty) {
            Text(
                stringResource(R.string.es_requerido_message, label),
                color = Color.Red,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RetreatedDesignScreenPreview() {
    HombreCamionTheme {
        RetreatedDesignScreen(onBackScreen = {})
    }
}