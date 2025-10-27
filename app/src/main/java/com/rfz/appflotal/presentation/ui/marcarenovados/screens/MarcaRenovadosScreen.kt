package com.rfz.appflotal.presentation.ui.marcarenovados.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.ui.common.screen.AddItemDialog
import com.rfz.appflotal.presentation.ui.common.screen.ItemDialog
import com.rfz.appflotal.presentation.ui.common.screen.ListItemContent
import com.rfz.appflotal.presentation.ui.common.screen.ListManagementScreen
import com.rfz.appflotal.presentation.ui.marcarenovados.viewmodel.MarcaRenovadosViewModel
import com.rfz.appflotal.presentation.ui.marcarenovados.viewmodel.RetreadBrandFields

@Composable
fun MarcaRenovadosScreen(
    modifier: Modifier = Modifier,
    viewModel: MarcaRenovadosViewModel = hiltViewModel(),
    onBackScreen: () -> Unit
) {
    val state = viewModel.uiState.collectAsState()
    val ctx = LocalContext.current
    val code = state.value.dialogData[RetreadBrandFields.ID.value]
    val description = state.value.dialogData[RetreadBrandFields.DESCRIPTION.value]

    LaunchedEffect(Unit) {
        viewModel.setTitle(ctx.getString(R.string.marca_renovada))
        viewModel.loadItems()
        viewModel.onDialogFieldChanged(field = RetreadBrandFields.ID.value, value = null)
        viewModel.onDialogFieldChanged(field = RetreadBrandFields.DESCRIPTION.value, value = null)
    }

    ListManagementScreen(
        state = state.value,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onShowDialog = {
            viewModel.onIsEditing(false)
            viewModel.onShowDialog()
        },
        onClearSearchQuery = viewModel::onClearQuery,
        listItemContent = { item ->
            ListItemContent(
                title = item.description,
                onEditClick = {
                    viewModel.onIsEditing(true)
                    viewModel.setItemBrandById(item.id)
                    viewModel.onShowDialog()
                }
            ) {
                Text(text = item.id.toString())
            }
        },
        dialogContent = {
            AddItemDialog(
                title = if (!state.value.isEditing) "Agregar marca de renovado" else "Editar marca de renovado",
                content = {
                    ItemDialog(
                        label = "Código",
                        value = if (code != null) code as String else "",
                        isEmpty = state.value.dialogData[RetreadBrandFields.ID.value] == "",
                        onValueChange = { code ->
                            viewModel.onDialogFieldChanged(
                                field = RetreadBrandFields.ID.value,
                                value = code
                            )
                        },
                    )
                    ItemDialog(
                        label = "Descripción",
                        value = if (description != null) description as String else "",
                        isEmpty = state.value.dialogData[RetreadBrandFields.DESCRIPTION.value] == "",
                        onValueChange = { description ->
                            viewModel.onDialogFieldChanged(
                                field = RetreadBrandFields.DESCRIPTION.value,
                                value = description
                            )
                        },
                    )
                },
                onConfirm = viewModel::onSaveItem,
                onDismiss = viewModel::onDismissDialog,
                isEntryValid = if (description != null && code != null) description as String != "" && code as String != "" else false
            )
        },
        onBackScreen = onBackScreen,
        modifier = modifier,
    )
}