package com.rfz.appflotal.presentation.ui.registertires

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.components.CatalogDropdown
import com.rfz.appflotal.presentation.ui.registrollantasscreen.screens.DatePickerField
import com.rfz.appflotal.presentation.ui.registrollantasscreen.screens.DialogTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterTireScreen(
    onBack: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dot by remember { mutableStateOf("") }
    var tireNumber by remember { mutableStateOf("") }
    var acquisitionDate by remember { mutableStateOf(getCurrentDate()) }
    var folioFactura by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var treadDepth by remember { mutableStateOf("") }

    val dotWarning =
        if (dot.trim().length > 10) stringResource(R.string.dot_excedio_caracteres) else ""

    val tireNumberWarning =
        if (tireNumber.trim().length > 15) stringResource(R.string.numero_llanta_excedio_caracteres) else ""

    val isFormValid = remember(
        dot,
        tireNumber,
        acquisitionDate,
        folioFactura,
        cost,
        treadDepth,
        dotWarning,
        tireNumberWarning
    ) {
        dot.isNotBlank() &&
                tireNumber.isNotBlank() &&
                acquisitionDate.isNotBlank() &&
                folioFactura.isNotBlank() &&
                cost.isNotBlank() &&
                treadDepth.isNotBlank() &&
                dotWarning.isEmpty() &&
                tireNumberWarning.isEmpty()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Registro de llantas",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                ),
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
                    .shadow(4.dp)
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = onSaveClick,
                    enabled = isFormValid,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.guardar),
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            CatalogDropdown(
                catalog = emptyList(),
                selected = null,
                errorText = null,
                onSelected = {},
                label = stringResource(R.string.tipo_de_adquisici_n),
                onAddNewItem = { /* Navegación a definir por el usuario */ }
            )

            CatalogDropdown(
                catalog = emptyList(),
                selected = null,
                errorText = null,
                onSelected = {},
                label = stringResource(R.string.tamano_de_llanta)
            )

            DatePickerField(acquisitionDate) { date ->
                acquisitionDate = date
            }

            DialogTextField(
                label = stringResource(R.string.folio_factura),
                value = folioFactura
            ) { value -> folioFactura = value }

            DialogTextField(
                label = stringResource(R.string.costo),
                value = cost,
                keyboardType = KeyboardType.NumberPassword
            ) { value -> cost = value.filter { c -> c.isDigit() || c == '.' } }

            DialogTextField(
                label = stringResource(R.string.profundidad),
                value = treadDepth,
                keyboardType = KeyboardType.NumberPassword,
                isEditable = false
            ) { value ->
                treadDepth = value.filter { c -> c.isDigit() }
            }

            DialogTextField(
                label = stringResource(R.string.numero_de_llanta),
                value = tireNumberWarning,
                warningMessage = tireNumberWarning
            ) { value -> tireNumber = value }

            DialogTextField(
                label = stringResource(R.string.dot),
                value = dot,
                warningMessage = dotWarning
            ) { value -> dot = value }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterTireScreenPreview() {
    HombreCamionTheme {
        RegisterTireScreen(onBack = {}, onSaveClick = {})
    }
}
