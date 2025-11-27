package com.rfz.appflotal.presentation.ui.dissassembly.screen

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.dissassembly.viewmodel.DisassemblyUiState
import com.rfz.appflotal.presentation.ui.dissassembly.viewmodel.DisassemblyViewModel


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
    Scaffold(modifier = modifier, topBar = { SimpleTopBar("Disassembly", onBack = onBack) }) {

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