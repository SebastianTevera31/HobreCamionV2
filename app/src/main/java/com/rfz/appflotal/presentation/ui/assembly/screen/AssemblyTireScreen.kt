package com.rfz.appflotal.presentation.ui.assembly.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.presentation.commons.CircularLoading
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.assembly.viewmodel.AssemblyTireUiState
import com.rfz.appflotal.presentation.ui.assembly.viewmodel.AssemblyTireViewModel


@Composable
fun AssemblyTireScreen(
    modifier: Modifier = Modifier,
    viewModel: AssemblyTireViewModel,
    onBack: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState()

    AssemblyTireView(
        modifier = modifier,
        uiState = uiState.value,
        onBack = onBack
    ) {
        viewModel.registerAssemblyTire()
    }
}

@Composable
fun AssemblyTireView(
    modifier: Modifier = Modifier,
    uiState: AssemblyTireUiState,
    onBack: () -> Unit,
    onAssembly: () -> Unit,
) {
    var odometer by remember { mutableStateOf(uiState.odometer) }

    Scaffold(
        modifier = modifier,
        topBar = { SimpleTopBar(title = "Assembly Tire", onBack = onBack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.tireList != null && uiState.axleList != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "Odometro")
                    OutlinedTextField(
                        value = odometer.toString(),
                        onValueChange = { odometer = it.toInt() },
                        label = { Text("Odometro") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword,
                        ),
                        isError = false,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6A5DD9),
                            unfocusedBorderColor = Color(0xFFAAAAAA),
                            focusedLabelColor = Color(0xFF6A5DD9),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        shape = RoundedCornerShape(14.dp),
                    )
                }
            } else {
                CircularLoading()
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun AssemblyTireViewPreview() {
    HombreCamionTheme {
        AssemblyTireView(
            uiState = AssemblyTireUiState(
                tireList = emptyList(),
                axleList = emptyList()
            ), onBack = {}) {}
    }
}