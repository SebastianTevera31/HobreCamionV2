package com.rfz.appflotal.presentation.ui.monitor.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorUiState
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.RegisterMonitorViewModel

@Composable
fun MonitorSetupWizard(
    monitorUiState: MonitorUiState,
    registerMonitorViewModel: RegisterMonitorViewModel,
    paymentPlan: PaymentPlanType,
    onMountTireClick: (position: String) -> Unit,
    onLinkedSensor: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val hasConfiguration = monitorUiState.monitorId != 0
    var showDialog by rememberSaveable { mutableStateOf(!hasConfiguration) }

    WheelMountingScreen(
        hasConfiguration = hasConfiguration,
        baseConfigLabel = monitorUiState.baseConfig?.let { "BASE ${it.base}" },
        isSensorLinked = monitorUiState.monitorMac.isNotBlank(),
        tires = monitorUiState.listOfTires,
        image = monitorUiState.imageBitmap,
        imageDimens = monitorUiState.imageDimen,
        onMountTireClick = onMountTireClick,
        onDefineConfigClick = { showDialog = true },
        onLinkSensorClick = { showDialog = true },
        onFinishClick = onFinish,
        modifier = modifier
    )

    if (showDialog) {
        ShowMonitorRegisterDialog(
            monitorId = monitorUiState.monitorId,
            cancelButtonText = stringResource(R.string.cerrar),
            registerMonitorViewModel = registerMonitorViewModel,
            onDialogCancel = {
                showDialog = false
                if (!hasConfiguration) onFinish()
            },
            onSuccessRegister = { _ ->
                showDialog = false
                onLinkedSensor()
            },
            context = context,
            paymentPlan = paymentPlan,
        )
    }
}
