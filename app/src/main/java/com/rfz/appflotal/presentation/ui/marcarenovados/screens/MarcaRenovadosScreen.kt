package com.rfz.appflotal.presentation.ui.marcarenovados.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.ui.common.screen.AddItemDialog
import com.rfz.appflotal.presentation.ui.common.screen.ItemDialog
import com.rfz.appflotal.presentation.ui.common.screen.ListItemContent
import com.rfz.appflotal.presentation.ui.common.screen.ListManagementScreen
import com.rfz.appflotal.presentation.ui.marcarenovados.viewmodel.MarcaRenovadosViewModel

@Composable
fun MarcaRenovadosScreen(
    modifier: Modifier = Modifier,
    viewModel: MarcaRenovadosViewModel = hiltViewModel(),
    onBackScreen: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val ctx = LocalContext.current
    val code = state.dialogData["idRetreatedBrand"]
    val description = state.dialogData["description"]


    LaunchedEffect(Unit) {
        viewModel.setTitle(ctx.getString(R.string.marca_renovada))
        viewModel.loadItems()
        viewModel.onDialogFieldChanged(field = "idRetreatedBrand", value = null)
        viewModel.onDialogFieldChanged(field = "description", value = null)
    }

    ListManagementScreen(
        state = state,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onShowDialog = viewModel::onShowDialog,
        onClearSearchQuery = viewModel::onClearQuery,
        listItemContent = { item ->
            ListItemContent(
                title = item.description,
                onEditClick = {}
            ) { Text(text = item.id.toString()) }
        },
        dialogContent = {
            AddItemDialog(
                title = "Agregar marca de renovado",
                content = {
                    ItemDialog(
                        label = "Código",
                        value = if (code != null) code as String else "",
                        isEmpty = state.dialogData["idRetreatedBrand"] == "",
                        onValueChange = { code ->
                            viewModel.onDialogFieldChanged(
                                field = "idRetreatedBrand",
                                value = code
                            )
                        },
                    )
                    ItemDialog(
                        label = "Descripción",
                        value = if (description != null) description as String else "",
                        isEmpty = state.dialogData["description"] == "",
                        onValueChange = { description ->
                            viewModel.onDialogFieldChanged(
                                field = "description",
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