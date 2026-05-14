package com.rfz.appflotal.presentation.ui.blog.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.rfz.appflotal.presentation.ui.blog.viewmodel.BlogUiState
import com.rfz.appflotal.presentation.ui.blog.viewmodel.BlogViewModel

@Composable
fun BlogRoute(viewModel: BlogViewModel, modifier: Modifier = Modifier) {
    val uiState = viewModel.uiState.collectAsState()
    BlogNavigationScreen(uiState = uiState.value, modifier = modifier)
}

@Composable
fun BlogNavigationScreen(uiState: BlogUiState, modifier: Modifier = Modifier) {

}