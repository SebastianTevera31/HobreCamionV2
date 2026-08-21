package com.rfz.appflotal.presentation.ui.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.repository.UnidadOdometro
import com.rfz.appflotal.data.repository.UnidadPresion
import com.rfz.appflotal.data.repository.UnidadTemperatura
import com.rfz.appflotal.data.repository.UnitProvider
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.updateuserscreen.screen.UnitToggle

@Composable
fun PreferencesScreen(
    modifier: Modifier = Modifier,
    temperatureUnit: UnitProvider,
    pressureUnit: UnitProvider,
    odometerUnit: UnitProvider,
    onTempChange: (UnitProvider) -> Unit,
    onPressureChange: (UnitProvider) -> Unit,
    onOdometerChange: (UnitProvider) -> Unit,
    onBack: () -> Unit,
    onConfirmUnits: (temperature: UnitProvider, pressure: UnitProvider, odometer: UnitProvider) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "PREFERENCIAS",
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = stringResource(R.string.unidades_de_medida),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )
        UnitToggle(
            title = R.string.temperatura,
            firstUnit = UnidadTemperatura.FAHRENHEIT,
            secondUnit = UnidadTemperatura.CELCIUS,
            selectedUnit = temperatureUnit,
            modifier = Modifier

        ) { onTempChange(it) }

        UnitToggle(
            title = R.string.presion,
            firstUnit = UnidadPresion.BAR,
            secondUnit = UnidadPresion.PSI,
            selectedUnit = pressureUnit,
            modifier = Modifier
        ) { onPressureChange(it) }

        UnitToggle(
            title = R.string.odometro,
            firstUnit = UnidadOdometro.MILLAS,
            secondUnit = UnidadOdometro.KILOMETROS,
            selectedUnit = odometerUnit,
            modifier = Modifier
        ) { onOdometerChange(it) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onBack() },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiary),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.weight(1f)
            ) { Text(text = stringResource(R.string.regresar)) }

            Button(
                onClick = { onConfirmUnits(temperatureUnit, pressureUnit, odometerUnit) },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.weight(1f)
            ) { Text(text = stringResource(R.string.siguiente)) }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationScreen() {
    HombreCamionTheme {
        PreferencesScreen(
            temperatureUnit = UnidadTemperatura.CELCIUS,
            pressureUnit = UnidadPresion.PSI,
            odometerUnit = UnidadOdometro.KILOMETROS,
            onTempChange = {},
            onPressureChange = {},
            onOdometerChange = {},
            onBack = {},
            onConfirmUnits = { _, _, _ -> }
        )
    }
}