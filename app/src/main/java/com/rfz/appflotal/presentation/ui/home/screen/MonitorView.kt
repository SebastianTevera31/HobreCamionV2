package com.rfz.appflotal.presentation.ui.home.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rfz.appflotal.R
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.presentation.ui.monitor.screen.MonitorRegisterDialog
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorConfigurationUiState


@Composable
fun RegisterMonitorDialog(
    configurations: Map<Int, String>,
    monitorConfigurationUiState: MonitorConfigurationUiState,
    registerMonitorStatus: ApiResult<Int>,
    onContinueButton: (String, Pair<Int, String>?) -> Unit,
    onScan: () -> Unit,
    onCloseButton: () -> Unit,
    onMonitorConfiguration: (Pair<Int, String>?) -> Unit,
    onSuccess: () -> Unit
) {
    MonitorRegisterDialog(
        configurations = configurations,
        isScanning = monitorConfigurationUiState.isScanning,
        showCloseButton = true,
        monitorSelected = monitorConfigurationUiState.configurationSelected,
        macValue = monitorConfigurationUiState.mac,
        onScan = { onScan() },
        onCloseButton = { onCloseButton() },
        onContinueButton = { mac, configuration -> onContinueButton(mac, configuration) },
        registerMonitorStatus = registerMonitorStatus,
        onSuccessRegister = { onSuccess() },
        closeText = stringResource(R.string.cerrar),
        onMonitorConfiguration = { config -> onMonitorConfiguration(config) }
    )
}

