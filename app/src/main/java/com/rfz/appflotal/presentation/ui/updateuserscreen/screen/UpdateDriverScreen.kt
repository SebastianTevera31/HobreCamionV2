package com.rfz.appflotal.presentation.ui.updateuserscreen.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.components.FormTextField
import com.rfz.appflotal.presentation.ui.registrousuario.screen.SignUpDropDownMenu
import com.rfz.appflotal.presentation.ui.updateuserscreen.viewmodel.UserData

@Composable
fun UpdateDriverScreen(
    @StringRes title: Int,
    userData: UserData,
    countries: Map<Int, String>,
    industries: Map<Int, String>,
    modifier: Modifier = Modifier,
    saveDriverData: (
        name: String, password: String, email: String,
        country: Pair<Int, String>?, sector: Pair<Int, String>?
    ) -> Unit,
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

        FormTextField(title = R.string.nombre, value = userData.name, onValueChange = {
            saveDriverData(
                it,
                userData.password,
                userData.email,
                userData.country,
                userData.industry
            )
        }
        )

        FormTextField(
            title = R.string.correo_electr_nico,
            value = userData.email,
            onValueChange = {
                saveDriverData(
                    userData.name,
                    userData.password,
                    it,
                    userData.country,
                    userData.industry
                )
            },
            keyboardType = KeyboardType.Email
        )

        FormTextField(
            title = R.string.contrase_a,
            value = userData.password,
            onValueChange = {
                saveDriverData(
                    userData.name,
                    it,
                    userData.email,
                    userData.country,
                    userData.industry
                )
            },
            keyboardType = KeyboardType.Password
        )

        val countryText = userData.country?.second ?: stringResource(R.string.pa_s)
        SignUpDropDownMenu(
            title = stringResource(R.string.pa_s),
            text = countryText,
            onSelectedValue = {
                saveDriverData(
                    userData.name,
                    userData.password,
                    userData.email,
                    it,
                    userData.industry
                )
            },
            values = countries,
            modifier = Modifier.fillMaxWidth()
        )

        val sectorText = userData.industry?.second ?: stringResource(R.string.sector)
        SignUpDropDownMenu(
            title = stringResource(R.string.sector),
            text = sectorText,
            onSelectedValue = {
                saveDriverData(
                    userData.name,
                    userData.password,
                    userData.email,
                    userData.country,
                    it
                )
            },
            values = industries,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun UpdateDriverScreenPreview() {
    HombreCamionTheme {
        UpdateDriverScreen(
            title = R.string.chofer,
            userData = UserData(),
            countries = emptyMap(),
            industries = emptyMap(),
            modifier = Modifier.safeContentPadding()
        ) { _, _, _, _, _ -> }
    }
}