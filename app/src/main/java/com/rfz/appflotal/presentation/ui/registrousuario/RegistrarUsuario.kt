package com.rfz.appflotal.presentation.ui.registrousuario

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.theme.primaryLight
import com.rfz.appflotal.presentation.theme.secondaryLight

@Composable
fun RegistrarUsuario(modifier: Modifier = Modifier) {
    var isNextScreen by remember { mutableStateOf(false) }

    Scaffold(topBar = {}) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .drawWithContent {
                        drawContent()

                        val path = Path().apply {
                            moveTo(0f, 0f)
                            lineTo(size.width, 0f)
                            lineTo(size.width, size.height * 0.1f)
                            quadraticBezierTo(
                                size.width / 2,
                                size.height * 0.2f,
                                0f,
                                size.height * 0.1f
                            )
                            close()
                        }

                        drawPath(
                            path = path,
                            brush = Brush.verticalGradient(
                                colors = listOf(primaryLight, secondaryLight),
                                startY = 0f,
                                endY = size.height
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (!isNextScreen) {
                    RegistrarUsuarioView(modifier = Modifier.padding(horizontal = 40.dp)) {
                        isNextScreen = true
                    }
                } else RegistrarVehiculo(
                    modifier = Modifier.padding(horizontal = 40.dp),
                    onBack = { isNextScreen = false }) {}
            }
        }
    }
}

@Composable
fun RegistrarUsuarioView(modifier: Modifier = Modifier, onNextButton: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var pais by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var showCountryList by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        modifier = modifier
    ) {
        Text(
            "Registro",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        DropDownCountryMenu(
            text = "Pais",
            expanded = showCountryList,
            onDismissRequest = { showCountryList = false },
            onShowList = { showCountryList = true },
            onSelectedValue = { pais = it },
            list = emptyList(),
            modifier = Modifier.fillMaxWidth()
        )

        DropDownCountryMenu(
            text = "Sector",
            expanded = showCountryList,
            onDismissRequest = { showCountryList = false },
            onShowList = { showCountryList = true },
            onSelectedValue = { pais = it },
            list = emptyList(),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onNextButton,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.align(Alignment.End)
        ) { Text(text = stringResource(R.string.siguiente)) }
    }
}


@Composable
fun DropDownCountryMenu(
    text: String,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onSelectedValue: (String) -> Unit,
    onShowList: () -> Unit,
    list: List<String>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .clickable { onShowList() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text, textAlign = TextAlign.Start, modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(R.drawable.drop_down_arrow),
                contentDescription = "Seleccionar $text",
            )
        }
    }

    DropdownMenu(expanded = expanded, onDismissRequest = onDismissRequest, modifier = modifier) {
        list.forEach { value ->
            DropdownMenuItem(
                text = { Text(text = value) },
                onClick = { onSelectedValue(value) }
            )
        }
    }
}


@Composable
@Preview(showSystemUi = true, showBackground = true)
fun RegistrarUsuarioPreview() {
    HombreCamionTheme {
        RegistrarUsuario(
            modifier = Modifier
                .fillMaxSize()
        )
    }
}
