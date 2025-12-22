package com.rfz.appflotal.presentation.ui.updateuserscreen.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import com.rfz.appflotal.data.repository.UnidadOdometro
import com.rfz.appflotal.data.repository.UnidadPresion
import com.rfz.appflotal.data.repository.UnidadTemperatura
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.components.FormTextField
import com.rfz.appflotal.presentation.ui.updateuserscreen.viewmodel.UserData

@Composable
fun UpdateVehicleScreen(
    @StringRes title: Int,
    userData: UserData,
    modifier: Modifier = Modifier,
    saveVehicleData: (vehicleType: String, plates: String) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        modifier = modifier.verticalScroll(scrollState)
    ) {
        Text(
            stringResource(title),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        FormTextField(
            title = R.string.tipo_vehiculo,
            value = userData.typeVehicle,
            onValueChange = {
                saveVehicleData(it, userData.plates)
            }
        )

        FormTextField(
            title = R.string.placas,
            value = userData.plates,
            onValueChange = { saveVehicleData(userData.typeVehicle, it) }
        )

//        Text(
//            text = "Unidades de Medida",
//            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
//        )
//        Row(modifier = Modifier.fillMaxWidth()) {
//            UnitButton(
//                title = R.string.temperatura,
//                firstUnit = UnidadTemperatura.FAHRENHEIT.symbol,
//                secondUnit = UnidadTemperatura.CELCIUS.symbol,
//                selected = userData.temperatureUnit.symbol,
//                modifier = Modifier.weight(1f)
//            )
//            {
//
//            }
//            UnitButton(
//                title = R.string.presion,
//                firstUnit = UnidadPresion.BAR.symbol,
//                secondUnit = UnidadPresion.PSI.symbol,
//                selected = userData.pressureUnit.symbol,
//                modifier = Modifier.weight(1f)
//            )
//            {
//
//            }
//            UnitButton(
//                title = R.string.odometro,
//                firstUnit = UnidadOdometro.MILLAS.symbol,
//                secondUnit = UnidadOdometro.KILOMETROS.symbol,
//                selected = userData.odometerUnit.symbol,
//                modifier = Modifier.weight(1f)
//            )
//            {
//
//            }
//        }
    }
}

@Composable
fun UnitButton(
    @StringRes title: Int,
    firstUnit: String,
    secondUnit: String,
    selected: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var checked by remember { mutableStateOf(selected == firstUnit) }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(title), style = MaterialTheme.typography.labelLarge)
        Switch(
            checked = checked,
            onCheckedChange = { onClick() },
            thumbContent = {
                Text(
                    text = if (checked) firstUnit else secondUnit,
                )
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.width(80.dp)
        )
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun UpdateVehicleScreenPreview() {
    HombreCamionTheme {
        UpdateVehicleScreen(
            title = R.string.vehiculo,
            userData = UserData(),
            modifier = Modifier.safeContentPadding(),
        ) { _, _ -> }
    }
}

