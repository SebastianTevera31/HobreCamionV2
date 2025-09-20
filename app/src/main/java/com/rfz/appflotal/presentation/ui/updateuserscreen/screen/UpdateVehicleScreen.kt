package com.rfz.appflotal.presentation.ui.updateuserscreen.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
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
            })

        FormTextField(
            title = R.string.placas,
            value = userData.plates,
            onValueChange = { saveVehicleData(userData.typeVehicle, it) })
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

