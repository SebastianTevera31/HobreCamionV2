package com.rfz.appflotal.presentation.ui.updateuserscreen.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
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
import com.rfz.appflotal.presentation.ui.components.FormTextField
import com.rfz.appflotal.presentation.ui.updateuserscreen.viewmodel.VehicleData

@Composable
fun UpdateVehicleScreen(
    @StringRes title: Int,
    vehicleData: VehicleData,
    modifier: Modifier = Modifier,
    saveVehicleData: (
        vehicleType: String,
        plates: String,
        temperatrueUnit: UnitProvider,
        pressureUnit: UnitProvider,
        odometerUnit: UnitProvider
    ) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .safeContentPadding()
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text(
            stringResource(title),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        FormTextField(
            title = R.string.tipo_vehiculo,
            value = vehicleData.typeVehicle,
            onValueChange = {
                saveVehicleData(
                    it,
                    vehicleData.plates,
                    vehicleData.temperatureUnit,
                    vehicleData.pressureUnit,
                    vehicleData.odometerUnit
                )
            }
        )

        FormTextField(
            title = R.string.placas,
            value = vehicleData.plates,
            onValueChange = {
                saveVehicleData(
                    vehicleData.typeVehicle,
                    it,
                    vehicleData.temperatureUnit,
                    vehicleData.pressureUnit,
                    vehicleData.odometerUnit
                )
            }
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.unidades_de_medida),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            )
            UnitToggle(
                title = R.string.temperatura,
                firstUnit = UnidadTemperatura.FAHRENHEIT,
                secondUnit = UnidadTemperatura.CELCIUS,
                selectedUnit = vehicleData.temperatureUnit,
                modifier = Modifier

            )
            {
                saveVehicleData(
                    vehicleData.typeVehicle,
                    vehicleData.plates,
                    it,
                    vehicleData.pressureUnit,
                    vehicleData.odometerUnit
                )
            }
            UnitToggle(
                title = R.string.presion,
                firstUnit = UnidadPresion.BAR,
                secondUnit = UnidadPresion.PSI,
                selectedUnit = vehicleData.pressureUnit,
                modifier = Modifier
            )
            {
                saveVehicleData(
                    vehicleData.typeVehicle,
                    vehicleData.plates,
                    vehicleData.temperatureUnit,
                    it,
                    vehicleData.odometerUnit
                )
            }
            UnitToggle(
                title = R.string.odometro,
                firstUnit = UnidadOdometro.MILLAS,
                secondUnit = UnidadOdometro.KILOMETROS,
                selectedUnit = vehicleData.odometerUnit,
                modifier = Modifier
            )
            {
                saveVehicleData(
                    vehicleData.typeVehicle,
                    vehicleData.plates,
                    vehicleData.temperatureUnit,
                    vehicleData.pressureUnit,
                    it
                )
            }
        }
    }
}

@Composable
fun UnitToggle(
    @StringRes title: Int,
    firstUnit: UnitProvider,
    secondUnit: UnitProvider,
    selectedUnit: UnitProvider,
    modifier: Modifier = Modifier,
    styleForFirst: TextStyle = MaterialTheme.typography.labelMedium,
    styleForSecond: TextStyle = MaterialTheme.typography.labelMedium,
    onUnitSelected: (UnitProvider) -> Unit
) {
    val isFirstSelected = selectedUnit == firstUnit

    Row(
        horizontalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.small_dimen),
            alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier.width(80.dp)
        )
        Row(
            modifier = modifier
                .width(160.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(dimensionResource(R.dimen.thin_dimen))

        ) {
            UnitToggleItem(
                text = firstUnit.symbol,
                selected = isFirstSelected,
                style = styleForFirst,
                modifier = Modifier.weight(1f)
            ) { onUnitSelected(firstUnit) }

            UnitToggleItem(
                text = secondUnit.symbol,
                selected = !isFirstSelected,
                style = styleForSecond,
                modifier = Modifier.weight(1f)
            ) { onUnitSelected(secondUnit) }
        }
    }
}

@Composable
private fun UnitToggleItem(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.labelMedium,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(
                if (selected)
                    MaterialTheme.colorScheme.primary
                else
                    Color.Transparent
            )
            .clickable { onClick() }
            .padding(
                horizontal = dimensionResource(R.dimen.medium_dimen),
                vertical = dimensionResource(R.dimen.small_dimen)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            style = style
        )
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun UpdateVehicleScreenPreview() {
    HombreCamionTheme {
        UpdateVehicleScreen(
            title = R.string.vehiculo,
            vehicleData = VehicleData(),
            modifier = Modifier.safeContentPadding(),
        ) { _, _, _, _, _ -> }
    }
}

