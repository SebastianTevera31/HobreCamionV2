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
import com.rfz.appflotal.presentation.ui.registrousuario.screen.VehicleForm
import com.rfz.appflotal.presentation.ui.registrousuario.viewmodel.SignUpAlerts
import com.rfz.appflotal.presentation.ui.registrousuario.viewmodel.SignUpViewModel

@Composable
fun VehicleDataScreen(
    viewModel: SignUpViewModel,
    navController: NavController
) {
    val ctx = LocalContext.current
    val uiState by viewModel.signUpUiState.collectAsState()

    VehicleForm(
        title = R.string.registrar_vehiculo,
        vehicleData = uiState.vehicleData,
        modifier = Modifier.padding(horizontal = 40.dp),
        enableRegisterButton = true, // Controlado internamente o por estado
        onBack = { vehicleType, plates ->
            viewModel.chargeVehicleData(typeVehicle = vehicleType, plates = plates)
            navController.popBackStack()
        }
    ) { vehicleType, plates ->
        val message = viewModel.chargeVehicleData(typeVehicle = vehicleType, plates = plates)
        if (message == SignUpAlerts.DATAREGISTER_SUCCESSFULY) {
            navController.navigate("terms_view")
        } else {
            Toast.makeText(ctx, message.message, Toast.LENGTH_SHORT).show()
        }
    }
}
