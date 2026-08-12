package com.rfz.appflotal.presentation.ui.registrousuario.screens

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.forms.VehicleFormModel
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.registrousuario.screen.VehicleForm
import com.rfz.appflotal.presentation.ui.registrousuario.viewmodel.SignUpAlerts
import com.rfz.appflotal.presentation.ui.registrousuario.viewmodel.SignUpViewModel

@Composable
fun VehicleDataScreen(
    viewModel: SignUpViewModel,
    navController: NavController,
    onNext: () -> Unit,
) {
    val ctx = LocalContext.current
    val uiState by viewModel.signUpUiState.collectAsState()

    VehicleDataContent(
        vehicleData = uiState.vehicleData,
        onBack = { vehicleType, plates ->
            viewModel.chargeVehicleData(typeVehicle = vehicleType, plates = plates)
            navController.popBackStack()
        },
        onRegister = { vehicleType, plates ->
            val message = viewModel.chargeVehicleData(typeVehicle = vehicleType, plates = plates)
            if (message == SignUpAlerts.DATAREGISTER_SUCCESSFULY) {
                onNext()
            } else {
                Toast.makeText(ctx, message.message, Toast.LENGTH_SHORT).show()
            }
        }
    )
}

@Composable
fun VehicleDataContent(
    vehicleData: VehicleFormModel,
    onBack: (vehicleType: String, plates: String) -> Unit,
    onRegister: (vehicleType: String, plates: String) -> Unit,
    modifier: Modifier = Modifier
) {
    VehicleForm(
        title = R.string.registrar_vehiculo,
        vehicleData = vehicleData,
        modifier = modifier.padding(horizontal = 40.dp),
        enableRegisterButton = true, // Controlado internamente o por estado
        onBack = onBack,
        onRegister = onRegister
    )
}

@Preview(showBackground = true)
@Composable
fun VehicleDataScreenPreview() {
    HombreCamionTheme {
        VehicleDataContent(
            vehicleData = VehicleFormModel(
                vehicleType = "Camión",
                plates = "ABC-123"
            ),
            onBack = { _, _ -> },
            onRegister = { _, _ -> }
        )
    }
}
