package com.rfz.appflotal.presentation.ui.registrousuario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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

@Composable
fun RegistrarVehiculo(modifier: Modifier = Modifier, onBack: () -> Unit, onRegister: () -> Unit, ) {
    var tipoVehiculo by remember { mutableStateOf("") }
    var placas by remember { mutableStateOf("") }
    var sector by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        modifier = modifier
    ) {
        Text(
            "Registrar Vehiculo",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        TextField(
            value = tipoVehiculo,
            onValueChange = { tipoVehiculo = it },
            label = { Text("Tipo vehiculo") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = placas,
            onValueChange = { placas = it },
            label = { Text("Placas") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.weight(1f)
            ) { Text(text = stringResource(R.string.regresar)) }

            Button(
                onClick = onRegister,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.weight(1f)
            ) { Text(text = stringResource(R.string.registrarse)) }
        }
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun RegistrarDatosVehiculoPreview() {
    HombreCamionTheme {
        RegistrarVehiculo(
            onBack = {},
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize()
        ) {}
    }
}