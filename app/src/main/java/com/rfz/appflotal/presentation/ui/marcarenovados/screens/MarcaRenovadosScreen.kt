package com.rfz.appflotal.presentation.ui.marcarenovados.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rfz.appflotal.presentation.ui.common.screen.ListManagementScreen
import com.rfz.appflotal.presentation.ui.marcarenovados.viewmodel.MarcaRenovadosViewModel

@Composable
fun MarcaRenovadosScreen(
    modifier: Modifier = Modifier,
    viewModel: MarcaRenovadosViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadItems()
    }

    ListManagementScreen(
        state = state,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onShowDialog = viewModel::onShowDialog,
        listItemContent = {
            // Aquí defines cómo se ve un solo ítem de la lista.
            // Este es uno de los "slots" que llenamos.
            Text(text = "Placeholder para el ítem") // Reemplazar con tu Composable de fila
        },
        dialogContent = {
            // Aquí defines el contenido del diálogo para esta pantalla específica.
            // Este es el segundo "slot".
            Text(text = "Placeholder para el diálogo") // Reemplazar con tu Composable de diálogo
        },
        modifier = modifier
    )
}