package com.rfz.appflotal.presentation.ui.dissassembly.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.components.CatalogDropdown
import com.rfz.appflotal.presentation.ui.components.CompleteFormButton
import com.rfz.appflotal.presentation.ui.components.TireInfoCard
import com.rfz.appflotal.presentation.ui.dissassembly.viewmodel.DisassemblyUiState
import com.rfz.appflotal.presentation.ui.dissassembly.viewmodel.DisassemblyViewModel
import com.rfz.appflotal.presentation.ui.dissassembly.viewmodel.OperationStatus


@Composable
fun DisassemblyScreen(
    viewModel: DisassemblyViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsState()
    DisassemblyView(uiState = uiState.value, onBack = onBack, modifier = modifier)
}

@Composable
fun DisassemblyView(
    uiState: DisassemblyUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var causesSelected: CatalogItem? by remember { mutableStateOf(null) }
    val destinationSelected: CatalogItem? by remember { mutableStateOf(null) }

    val isValid = {
        causesSelected != null && destinationSelected != null
    }

    LaunchedEffect(uiState.operationStatus) {
        if (uiState.operationStatus == OperationStatus.Success) {
            onBack()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = { SimpleTopBar("Disassembly", onBack = onBack) },
        bottomBar = {
            CompleteFormButton(
                textButton = "Desmontar",
                isValid = false
            ) { }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .safeContentPadding()
        ) {

            TireInfoCard(null)
            CatalogDropdown(
                catalog = emptyList(),
                selected = causesSelected?.description ?: "",
                errorText = null,
                onSelected = {},
            )
            CatalogDropdown(
                catalog = emptyList(),
                selected = destinationSelected?.description ?: "",
                errorText = null,
                onSelected = {},
            )
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun DisassemblyViewPreview(modifier: Modifier = Modifier) {
    HombreCamionTheme {
        DisassemblyView(
            onBack = {},
            uiState = DisassemblyUiState(),
        )
    }
}