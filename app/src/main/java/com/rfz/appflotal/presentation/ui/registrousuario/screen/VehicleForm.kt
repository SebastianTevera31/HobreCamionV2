package com.rfz.appflotal.presentation.ui.registrousuario.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.components.FormTextField
import com.rfz.appflotal.presentation.ui.registrousuario.viewmodel.SignUpUiState

@Composable
fun VehicleForm(
    signUpUiState: SignUpUiState,
    enableRegisterButton: Boolean,
    modifier: Modifier = Modifier,
    onBack: (vehicleType: String, plates: String) -> Unit,
    onRegister: (vehicleType: String, plates: String) -> Unit,
) {
    var vehicleType by remember { mutableStateOf("") }
    var plates by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        vehicleType = signUpUiState.vehicleType
        plates = signUpUiState.plates
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        modifier = modifier.verticalScroll(scrollState)
    ) {
        Text(
            stringResource(R.string.registrar_vehiculo),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        FormTextField(
            title = R.string.tipo_vehiculo,
            value = vehicleType,
            onValueChange = { vehicleType = it })

        FormTextField(title = R.string.placas, value = plates, onValueChange = { plates = it })

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onBack(vehicleType, plates) },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiary),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.weight(1f)
            ) { Text(text = stringResource(R.string.regresar)) }

            Button(
                onClick = { onRegister(vehicleType, plates) },
                shape = MaterialTheme.shapes.medium,
                enabled = enableRegisterButton,
                modifier = Modifier.weight(1f)
            ) { Text(text = stringResource(R.string.registrarse)) }
        }
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun RegistrarDatosVehiculoPreview() {
    HombreCamionTheme {
        VehicleForm(
            signUpUiState = SignUpUiState(),
            enableRegisterButton = true,
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            onBack = { _, _ -> },
            { _, _ -> },
        )
    }
}