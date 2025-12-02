package com.rfz.appflotal.presentation.ui.repararrenovar.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.repararrenovar.viewmodel.RepararRenovarUiState
import com.rfz.appflotal.presentation.ui.repararrenovar.viewmodel.RepararRenovarViewModel

@Composable
fun RepararRenovarScreen(
    onBackClick: () -> Unit,
    viewModel: RepararRenovarViewModel,
    modifier: Modifier = Modifier
) {
    RepararRenovarView(uiState = viewModel.uiState.value, modifier = modifier)

}

@Composable
fun RepararRenovarView(uiState: RepararRenovarUiState, modifier: Modifier = Modifier) {

}

@Preview(showBackground = true, showSystemUi = true, locale = "en")
@Composable
fun RepararRenovarPreview() {
    HombreCamionTheme {

    }
}
