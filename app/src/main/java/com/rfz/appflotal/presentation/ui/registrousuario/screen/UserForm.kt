package com.rfz.appflotal.presentation.ui.registrousuario.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
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
        modifier = modifier
    ) {
        Text(
            stringResource(R.string.registro),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.nombre)) },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.correo_electr_nico)) },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.contrase_a)) },
            modifier = Modifier.fillMaxWidth()
        )

        SignUpDropDownMenu(
            text = stringResource(R.string.pa_s),
            onSelectedValue = { country = it },
            values = countries,
            modifier = Modifier.fillMaxWidth()
        )

        SignUpDropDownMenu(
            text = stringResource(R.string.sector),
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
    text: String,
    onSelectedValue: (Pair<Int, String>) -> Unit,
    values: Map<Int, String>,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf(text) }
    var showList by remember { mutableStateOf(false) }
    var parentSize by remember { mutableStateOf(IntSize.Zero) }


    Column(
        modifier = Modifier
            .onGloballyPositioned { coordinates ->
                parentSize = coordinates.size
            }
            .clickable { showList = true }) {
        Row(
            modifier = modifier
                .height(60.dp)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                title, textAlign = TextAlign.Start, modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(R.drawable.drop_down_arrow),
                contentDescription = stringResource(R.string.seleccionar, text),
            )
        }

        DropdownMenu(
            expanded = showList,
            onDismissRequest = { showList = false },
            modifier = Modifier.width(with(LocalDensity.current) { parentSize.width.toDp() })
        ) {
            values.forEach { value ->
                DropdownMenuItem(
                    text = { Text(text = value.value) },
                    onClick = {
                        title = value.value
                        onSelectedValue(Pair(value.key, value.value))
                    }
                )
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