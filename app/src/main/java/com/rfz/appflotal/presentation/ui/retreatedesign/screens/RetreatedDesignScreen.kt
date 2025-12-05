package com.rfz.appflotal.presentation.ui.retreatedesign.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.commonscreens.listmanager.screen.AddItemDialog
import com.rfz.appflotal.presentation.ui.commonscreens.listmanager.screen.ItemDialog
import com.rfz.appflotal.presentation.ui.commonscreens.listmanager.screen.ListItemContent
import com.rfz.appflotal.presentation.ui.commonscreens.listmanager.screen.ListManagementScreen
import com.rfz.appflotal.presentation.ui.components.AwaitDialog
import com.rfz.appflotal.presentation.ui.components.FieldSpinner
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
        viewModel.loadItems()
    }

    LaunchedEffect(true) {
        viewModel.eventFlow.collectLatest { event ->
            Toast.makeText(ctx, event.message, Toast.LENGTH_SHORT).show()
        }
    }

    if (state.value.isSending) {
        AwaitDialog()
    }

    val treadWarning = dialogState.value.profundidadPiso.trim().toIntOrNull()?.let { number ->
        if (number <= 0) {
            stringResource(R.string.error_valor_inferior_0)
        } else {
            null
        }
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
                    description = "${item.treadDepth} mm"
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

                    Column {
                        ItemDialog(
                            label = " ${stringResource(R.string.profundidad_de_piso)} (mm)",
                            value = dialogState.value.profundidadPiso,
                            isEmpty = dialogState.value.profundidadPiso.isBlank(),
                            keyboardType = KeyboardType.NumberPassword,
                            onValueChange = { description ->
                                viewModel.onDialogFieldChanged(
                                    field = RetreadDesignFields.PROFUNDIDAD_PISO,
                                    value = description
                                )
                            },
                        )
                        if (treadWarning != null) {
                            Text(
                                stringResource(R.string.error_valor_inferior_0),
                                color = Color.Red,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

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
                        && treadWarning == null
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
fun DescriptionText(
    title: String,
    modifier: Modifier = Modifier,
    description: String = "",
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    Text(
        text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append("$title: ")
            }
            append(description)
        },
        style = style,
        modifier = modifier
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RetreatedDesignScreenPreview() {
    HombreCamionTheme {
        RetreatedDesignScreen(onBackScreen = {})
    }
}
