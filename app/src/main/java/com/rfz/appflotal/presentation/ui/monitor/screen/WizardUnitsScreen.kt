package com.rfz.appflotal.presentation.ui.monitor.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.repository.UnidadOdometro
import com.rfz.appflotal.data.repository.UnidadPresion
import com.rfz.appflotal.data.repository.UnidadTemperatura
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.updateuserscreen.screen.UnitToggle

@Composable
fun WizardUnitsScreen(
    temperatureUnit: UnidadTemperatura,
    pressureUnit: UnidadPresion,
    odometerUnit: UnidadOdometro,
    onSwitchTemperature: () -> Unit,
    onSwitchPressure: () -> Unit,
    onSwitchOdometer: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Button(
                    onClick = onContinue,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(stringResource(R.string.siguiente))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.unidades_de_medida),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.unidades_wizard_descripcion),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 24.dp, horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    UnitToggle(
                        title = R.string.temperatura,
                        firstUnit = UnidadTemperatura.FAHRENHEIT,
                        secondUnit = UnidadTemperatura.CELCIUS,
                        selectedUnit = temperatureUnit
                    ) { if (it != temperatureUnit) onSwitchTemperature() }

                    UnitToggle(
                        title = R.string.presion,
                        firstUnit = UnidadPresion.BAR,
                        secondUnit = UnidadPresion.PSI,
                        selectedUnit = pressureUnit
                    ) { if (it != pressureUnit) onSwitchPressure() }

                    UnitToggle(
                        title = R.string.odometro,
                        firstUnit = UnidadOdometro.MILLAS,
                        secondUnit = UnidadOdometro.KILOMETROS,
                        selectedUnit = odometerUnit
                    ) { if (it != odometerUnit) onSwitchOdometer() }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WizardUnitsScreenPreview() {
    HombreCamionTheme {
        WizardUnitsScreen(
            temperatureUnit = UnidadTemperatura.CELCIUS,
            pressureUnit = UnidadPresion.PSI,
            odometerUnit = UnidadOdometro.KILOMETROS,
            onSwitchTemperature = {},
            onSwitchPressure = {},
            onSwitchOdometer = {},
            onContinue = {}
        )
    }
}
