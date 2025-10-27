package com.rfz.appflotal.presentation.ui.marcarenovados.screens

import android.widget.Toast
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.ui.commonscreens.listmanager.screen.AddItemDialog
import com.rfz.appflotal.presentation.ui.commonscreens.listmanager.screen.ItemDialog
import com.rfz.appflotal.presentation.ui.commonscreens.listmanager.screen.ListItemContent
import com.rfz.appflotal.presentation.ui.commonscreens.listmanager.screen.ListManagementScreen
import com.rfz.appflotal.presentation.ui.components.AwaitDialog
import com.rfz.appflotal.presentation.ui.marcarenovados.viewmodel.MarcaRenovadosViewModel
import com.rfz.appflotal.presentation.ui.marcarenovados.viewmodel.RetreadBrandFields
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MarcaRenovadosScreen(
    modifier: Modifier = Modifier,
    viewModel: MarcaRenovadosViewModel = hiltViewModel(),
    onBackScreen: () -> Unit
) {
    val state = viewModel.uiState.collectAsState()
    val dialogState = viewModel.dialogState.collectAsState()

    val title = pluralStringResource(R.plurals.marca_renovado_tag, 2)
    val fieldTitle = pluralStringResource(R.plurals.marca_renovado_tag, 1)

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
                    viewModel.setItemBrandById(item)
                    viewModel.onShowDialog()
                }
            ) {
                Text(text = pluralStringResource(R.plurals.codigo_field, 2, item.id))
            }
        },
        dialogContent = {
            AddItemDialog(
                title = if (!state.value.isEditing) stringResource(
                    R.string.agregar_elemento,
                    fieldTitle
                ) else pluralStringResource(R.plurals.editar_elemento, 2, fieldTitle),
                content = {
                    ItemDialog(
                        label = pluralStringResource(R.plurals.codigo_field, 1),
                        value = dialogState.value.id,
                        isEmpty = dialogState.value.id.isBlank(),
                        onValueChange = { code ->
                            viewModel.onDialogFieldChanged(
                                field = RetreadBrandFields.ID,
                                value = code
                            )
                        },
                        keyboardType = KeyboardType.NumberPassword
                    )
                    ItemDialog(
                        label = pluralStringResource(R.plurals.description_field, 1),
                        value = dialogState.value.description,
                        isEmpty = dialogState.value.description.isBlank(),
                        onValueChange = { description ->
                            viewModel.onDialogFieldChanged(
                                field = RetreadBrandFields.DESCRIPTION,
                                value = description
                            )
                        },
                    )
                },
                onConfirm = {
                    if (!state.value.isEditing) {
                        viewModel.onAddItem()
                    } else {
                        viewModel.onUpdateItem()
                    }
                },
                onDismiss = viewModel::onDismissDialog,
                isEntryValid = dialogState.value.id.isNotBlank() && dialogState.value.description.isNotBlank()
            )
        },
        onBackScreen = onBackScreen,
        modifier = modifier,
    )
}