package com.rfz.appflotal.presentation.ui.monitor.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.RegisterMonitorViewModel

@Composable
fun MonitorConfigurationScreen(
    registerMonitorViewModel: RegisterMonitorViewModel,
    onConfigurationConfirmed: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val configurations by registerMonitorViewModel.configurationList.collectAsState()
    val loadState by registerMonitorViewModel.configurationsLoadState.collectAsState()
    val registeredState by registerMonitorViewModel.configurationRegisteredState.collectAsState()
    val context = LocalContext.current

    var selectedConfiguration by remember { mutableStateOf<Pair<Int, String>?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    LaunchedEffect(registeredState) {
        if (!isSubmitting) return@LaunchedEffect
        when (val result = registeredState) {
            is ApiResult.Success -> {
                isSubmitting = false
                onConfigurationConfirmed()
            }

            is ApiResult.Error -> {
                isSubmitting = false
                Toast.makeText(
                    context,
                    result.message ?: context.getString(R.string.error_desconocido),
                    Toast.LENGTH_LONG
                ).show()
            }

            ApiResult.Loading -> {}
        }
    }

    MonitorConfigurationScreenContent(
        configurations = configurations,
        loadState = loadState,
        selectedConfiguration = selectedConfiguration,
        isSubmitting = isSubmitting,
        onConfigurationSelected = { selectedConfiguration = it },
        onRetryLoad = { registerMonitorViewModel.loadConfigurations() },
        onConfirm = {
            selectedConfiguration?.let {
                isSubmitting = true
                registerMonitorViewModel.selectMonitorConfiguration(it, context)
            }
        },
        onCancel = onCancel,
        modifier = modifier
    )
}

@Composable
private fun MonitorConfigurationScreenContent(
    configurations: Map<Int, String>,
    loadState: ApiResult<Unit>,
    selectedConfiguration: Pair<Int, String>?,
    isSubmitting: Boolean,
    onConfigurationSelected: (Pair<Int, String>) -> Unit,
    onRetryLoad: () -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.monitor_configuracion_titulo),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.monitor_configuracion_descripcion),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            when (loadState) {
                ApiResult.Loading -> CircularProgressIndicator()

                is ApiResult.Error -> {
                    Text(
                        text = stringResource(R.string.monitor_configuracion_error),
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Button(onClick = onRetryLoad) {
                        Text(stringResource(R.string.reintentar))
                    }
                }

                is ApiResult.Success -> {
                    DropDownConfigurationMenu(
                        title = R.string.monitor,
                        values = configurations,
                        defaultOption = selectedConfiguration?.second ?: "",
                        modifier = Modifier.fillMaxWidth()
                    ) { onConfigurationSelected(it) }

                    Button(
                        onClick = onConfirm,
                        enabled = selectedConfiguration != null && !isSubmitting,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(modifier = Modifier.padding(2.dp))
                        } else {
                            Text(stringResource(R.string.confirmar))
                        }
                    }
                }
            }

            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.cerrar))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MonitorConfigurationScreenPreview() {
    HombreCamionTheme {
        MonitorConfigurationScreenContent(
            configurations = mapOf(1 to "TALON 6", 2 to "TALON 10"),
            loadState = ApiResult.Success(Unit),
            selectedConfiguration = 1 to "TALON 6",
            isSubmitting = false,
            onConfigurationSelected = {},
            onRetryLoad = {},
            onConfirm = {},
            onCancel = {}
        )
    }
}
