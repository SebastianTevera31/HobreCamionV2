package com.rfz.appflotal.presentation.ui.registrousuario.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.ui.registrousuario.screen.TerminosScreen
import com.rfz.appflotal.presentation.ui.registrousuario.viewmodel.SignUpViewModel

@Composable
fun TermsDataScreen(
    viewModel: SignUpViewModel,
    navController: NavController,
    onRegisterStarted: () -> Unit,
    onShowConnectionError: suspend () -> Unit
) {
    val ctx = LocalContext.current
    val uiState by viewModel.signUpUiState.collectAsState()

    TerminosScreen(
        context = ctx,
        onBack = {
            navController.popBackStack()
        }
    ) {
        onRegisterStarted()
        viewModel.signUpUser(ctx) {
            onShowConnectionError()
        }
    }
}
