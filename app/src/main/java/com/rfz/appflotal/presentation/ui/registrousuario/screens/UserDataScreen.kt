package com.rfz.appflotal.presentation.ui.registrousuario.screens

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.ui.registrousuario.screen.UserForm
import com.rfz.appflotal.presentation.ui.registrousuario.viewmodel.SignUpAlerts
import com.rfz.appflotal.presentation.ui.registrousuario.viewmodel.SignUpViewModel

@Composable
fun UserDataScreen(
    viewModel: SignUpViewModel,
    navController: NavController
) {
    val ctx = LocalContext.current
    val uiState by viewModel.signUpUiState.collectAsState()

    UserForm(
        title = R.string.registro,
        profileData = uiState.profileData,
        modifier = Modifier
            .padding(top = 80.dp)
            .padding(horizontal = 40.dp),
        countries = uiState.countries,
        sectors = uiState.sectors
    ) { name, password, email, country, sector ->
        val message = viewModel.chargeUserData(
            name = name,
            username = email,
            email = email,
            password = password,
            country = country,
            sector = sector
        )
        if (message == SignUpAlerts.DATAREGISTER_SUCCESSFULY) {
            navController.navigate("vehicle_data")
        } else {
            Toast.makeText(ctx, message.message, Toast.LENGTH_SHORT).show()
        }
    }
}
