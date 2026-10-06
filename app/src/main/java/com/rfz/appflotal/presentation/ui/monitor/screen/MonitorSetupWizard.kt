package com.rfz.appflotal.presentation.ui.monitor.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.BaseConfig
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorTire
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
    onSwitchTemperature: () -> Unit,
    onSwitchPressure: () -> Unit,
    onSwitchOdometer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val hasConfiguration = monitorUiState.monitorId != 0

    MonitorSetupWizardContent(
        monitorUiState = monitorUiState,
        onMountTireClick = onMountTireClick,
        onFinish = onFinish,
        onSwitchTemperature = onSwitchTemperature,
        onSwitchPressure = onSwitchPressure,
        onSwitchOdometer = onSwitchOdometer,
        modifier = modifier,
        dialogContent = { onDismiss ->
            ShowMonitorRegisterDialog(
                monitorId = monitorUiState.monitorId,
                cancelButtonText = stringResource(R.string.cerrar),
                registerMonitorViewModel = registerMonitorViewModel,
                onDialogCancel = {
                    onDismiss()
                    if (!hasConfiguration) onFinish()
                },
                onSuccessRegister = { _ ->
                    onDismiss()
                    onLinkedSensor()
                },
                context = context,
                paymentPlan = paymentPlan,
            )
        }
    )
}

@Composable
fun MonitorSetupWizardContent(
    monitorUiState: MonitorUiState,
    onMountTireClick: (position: String) -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    onSwitchTemperature: () -> Unit = {},
    onSwitchPressure: () -> Unit = {},
    onSwitchOdometer: () -> Unit = {},
    dialogContent: (@Composable (onDismiss: () -> Unit) -> Unit)? = null,
) {
    val hasConfiguration = monitorUiState.monitorId != 0
    var showDialog by rememberSaveable { mutableStateOf(!hasConfiguration) }
    var unitsConfirmed by rememberSaveable { mutableStateOf(false) }

    if (!unitsConfirmed) {
        WizardUnitsScreen(
            temperatureUnit = monitorUiState.temperatureUnit,
            pressureUnit = monitorUiState.pressureUnit,
            odometerUnit = monitorUiState.odometerUnit,
            onSwitchTemperature = onSwitchTemperature,
            onSwitchPressure = onSwitchPressure,
            onSwitchOdometer = onSwitchOdometer,
            onContinue = { unitsConfirmed = true },
            modifier = modifier
        )
        return
    }

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

    if (showDialog && dialogContent != null) {
        dialogContent { showDialog = false }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MonitorSetupWizardPreview() {
    val sampleUiState = MonitorUiState(
        monitorId = 1,
        baseConfig = BaseConfig.BASE6,
        listOfTires = listOf(
            MonitorTire("P1", inAlert = false, isAssembled = true, isActive = true, xPosition = 100, yPosition = 100),
            MonitorTire("P2", inAlert = false, isAssembled = false, isActive = true, xPosition = 300, yPosition = 100)
        ),
        monitorMac = "AA:BB:CC:DD:EE:FF"
    )
    HombreCamionTheme {
        MonitorSetupWizardContent(
            monitorUiState = sampleUiState,
            onMountTireClick = {},
            onFinish = {}
        )
    }
}
