package com.rfz.appflotal.presentation.ui.assembly.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.domain.CatalogItem
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.assembly.viewmodel.AssemblyTireUiState
import com.rfz.appflotal.presentation.ui.assembly.viewmodel.AssemblyTireViewModel
import com.rfz.appflotal.presentation.ui.assembly.viewmodel.OperationStatus
import com.rfz.appflotal.presentation.ui.components.AwaitDialog
import com.rfz.appflotal.presentation.ui.components.CatalogDropdown
import com.rfz.appflotal.presentation.ui.components.CompleteFormButton
import com.rfz.appflotal.presentation.ui.components.NumberField
import com.rfz.appflotal.presentation.ui.components.SectionHeader
import com.rfz.appflotal.presentation.ui.utils.validate
import kotlinx.coroutines.launch

@Composable
fun AssemblyTireScreen(
    positionTire: String,
    viewModel: AssemblyTireViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDataList(positionTire)
    }

    val title = stringResource(R.string.montaje)

    AssemblyTireView(
        title = title,
        tire = positionTire,
        modifier = modifier,
        uiState = uiState.value,
        onBack = {
            viewModel.cleanUiState()
            onBack()
        }
    ) { odometer, idAxle, idTire ->
        viewModel.registerAssemblyTire(
            odometer = odometer,
            idAxle = idAxle,
            idTire = idTire
        )
    }
}

@Composable
fun AssemblyTireView(
    title: String,
    tire: String,
    uiState: AssemblyTireUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onAssembly: (odometer: String, idAxle: Int, idTire: Int) -> Unit
) {
    var odometer by remember { mutableStateOf("") }
    var axleSelected: CatalogItem? by remember { mutableStateOf(null) }
    var tireSelected: CatalogItem? by remember { mutableStateOf(null) }

    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    val scroll = rememberScrollState()

    val areInputsValid = {
        axleSelected != null && tireSelected != null && odometer.toIntOrNull() != null
    }

    val isFormValid by remember(odometer, axleSelected, tireSelected) {
        derivedStateOf {
            areInputsValid()
        }
    }

    when (uiState.operationStatus) {
        is OperationStatus.Success -> {
            scope.launch {
                onBack()
                odometer = ""
                axleSelected = null
                tireSelected = null
            }
        }

        is OperationStatus.Error -> {
            scope.launch {
                odometer = ""
                axleSelected = null
                tireSelected = null
                snackbar.showSnackbar(uiState.operationStatus.message)
            }
        }

        OperationStatus.Loading -> {
            AwaitDialog()
        }

        else -> {}
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            SimpleTopBar(
                title = title,
                subTitle = tire,
                onBack = onBack
            )
        },
        bottomBar = {
            CompleteFormButton(
                textButton = "Montar",
                isValid = isFormValid
            ) {
                onAssembly(odometer, axleSelected!!.id, tireSelected!!.id)
            }
        }
    ) { innerPadding ->
        if (uiState.tireList != null && uiState.axleList != null) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(scroll)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                SectionHeader(text = "Eje")
                CatalogDropdown(
                    catalog = uiState.axleList,
                    selected = axleSelected?.description ?: "",
                    errorText = axleSelected?.validate(),
                    onSelected = { axleSelected = it },
                    label = "Eje",
                    modifier = Modifier.fillMaxWidth()
                )

                SectionHeader(text = "Llantas")
                CatalogDropdown(
                    catalog = uiState.tireList,
                    selected = tireSelected?.description ?: "",
                    errorText = tireSelected?.validate(),
                    onSelected = { tireSelected = it },
                    label = "Llantas",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.padding(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SectionHeader(text = "Odometro")
                    NumberField(
                        value = odometer,
                        onValueChange = { odometer = it },
                        label = "",
                        errorText = odometer.validate(),
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun AssemblyTireViewPreview() {
    HombreCamionTheme {
        AssemblyTireView(
            title = "Montaje",
            tire = "P1",
            uiState = AssemblyTireUiState(
                tireList = emptyList(),
                axleList = emptyList()
            ),
            onBack = {}) { _, _, _ -> }
    }
}
