package com.rfz.appflotal.presentation.ui.registrousuario.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.theme.primaryLight
import com.rfz.appflotal.presentation.theme.secondaryLight
import com.rfz.appflotal.presentation.ui.components.FormTextField
import com.rfz.appflotal.presentation.ui.languaje.LocalizedApp
import com.rfz.appflotal.presentation.ui.registrousuario.viewmodel.SignUpUiState

@Composable
fun UserForm(
    signUpUiState: SignUpUiState,
    countries: Map<Int, String>,
    sectors: Map<Int, String>,
    modifier: Modifier = Modifier,
    onNextButton: (
        name: String, password: String, email: String,
        country: Pair<Int, String>?, sector: Pair<Int, String>?
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var country by remember { mutableStateOf<Pair<Int, String>?>(null) }
    var sector by remember { mutableStateOf<Pair<Int, String>?>(null) }
    var email by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        name = signUpUiState.name
        password = signUpUiState.password
        country = signUpUiState.country
        sector = signUpUiState.sector
        email = signUpUiState.email
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        modifier = modifier.verticalScroll(scrollState)
    ) {
        Text(
            stringResource(R.string.registro),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        FormTextField(title = R.string.nombre, value = name, onValueChange = { name = it })

        FormTextField(
            title = R.string.correo_electr_nico,
            value = email,
            onValueChange = { email = it },
            keyboardType = KeyboardType.Email
        )

        FormTextField(
            title = R.string.contrase_a,
            value = password,
            onValueChange = { password = it },
            keyboardType = KeyboardType.Password
        )

        val countryText = country?.second ?: stringResource(R.string.pa_s)
        SignUpDropDownMenu(
            title = stringResource(R.string.pa_s),
            text = countryText,
            onSelectedValue = { country = it },
            values = countries,
            modifier = Modifier.fillMaxWidth()
        )

        val sectorText = sector?.second ?: stringResource(R.string.sector)
        SignUpDropDownMenu(
            title = stringResource(R.string.sector),
            text = sectorText,
            onSelectedValue = { sector = it },
            values = sectors,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { onNextButton(name, password, email, country, sector) },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.align(Alignment.End)
        ) { Text(text = stringResource(R.string.siguiente)) }
    }
}


@Composable
fun SignUpDropDownMenu(
    title: String,
    text: String,
    onSelectedValue: (Pair<Int, String>) -> Unit,
    values: Map<Int, String>,
    modifier: Modifier = Modifier
) {
    var searchText by remember { mutableStateOf(text) }
    var showList by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    val filteredValues = values.filter {
        it.value.contains(query, ignoreCase = true)
    }

    searchText = text

    Column(
        modifier = Modifier
            .border(width = 1.dp, color = primaryLight, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable { showList = true }) {
        Row(
            modifier = modifier
                .height(60.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = searchText,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f),
                color = secondaryLight
            )
            Icon(
                painter = painterResource(R.drawable.drop_down_arrow),
                contentDescription = stringResource(R.string.seleccionar, title),
            )
        }

        if (showList) {
            Dialog(onDismissRequest = { showList = false }) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 8.dp
                ) {
                    LocalizedApp {
                        Column {
                            TextField(
                                value = query,
                                onValueChange = { query = it },
                                placeholder = {
                                    Text(
                                        stringResource(
                                            R.string.buscar_valor,
                                            title.lowercase()
                                        )
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            LazyColumn(
                                modifier = Modifier.height(400.dp)
                            ) {
                                items(filteredValues.toList()) { value ->
                                    Text(
                                        text = value.second,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                searchText = value.second
                                                onSelectedValue(value)
                                                showList = false
                                            }
                                            .padding(16.dp)
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun UserFormPreview() {
    HombreCamionTheme {
        UserForm(
            signUpUiState = SignUpUiState(),
            modifier = Modifier.safeContentPadding(),
            countries = mapOf(1 to "Mexico", 2 to "USA"),
            sectors = mapOf()
        ) { _, _, _, _, _ -> }
    }
}