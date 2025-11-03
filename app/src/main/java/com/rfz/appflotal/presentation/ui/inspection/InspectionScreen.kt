package com.rfz.appflotal.presentation.ui.inspection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.components.FieldSpinner
import com.rfz.appflotal.presentation.ui.components.FormTextField

@Composable
fun InspectionRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InspectionViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState()
    InspectionScreen(modifier = modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "Inspección") }, navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
            }
        }
        )
    }, modifier = modifier) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .safeContentPadding()
        ) {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tire #",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
                FieldSpinner(
                    label = "Tipo de Reporte",
                    isEmpty = false,
                    selectedValue = "",
                    values = emptyList()
                ) { }
                GeneralFieldContent(modifier = Modifier)
                PressureFieldContent(modifier = Modifier)
                ThreadDepthFieldContent(modifier = Modifier)
                Button(onClick = {}, shape = RoundedCornerShape(16.dp)) {
                    Text("Terminar inspeccion", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
fun GeneralFieldContent(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        FormTextField(
            title = R.string.temperatura,
            value = "",
            onValueChange = {},
            modifier = Modifier.weight(1f)
        )
        FormTextField(
            title = R.string.temperatura,
            value = "",
            onValueChange = {},
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
fun PressureFieldContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
    }
}

@Composable
fun ThreadDepthFieldContent(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InspectionScreenPreview() {
    HombreCamionTheme { InspectionScreen() }
}