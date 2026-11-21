package com.rfz.appflotal.presentation.ui.monitor.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.components.HojaSincronizacionRoute
import com.rfz.appflotal.presentation.ui.monitor.posiciones.viewmodel.PosicionesViewModel
import com.rfz.appflotal.presentation.ui.monitor.sincronizacion.viewmodel.EstadoSincronizacionViewModel
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorViewModel
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.RegisterMonitorViewModel

@Composable
fun MonitorScreen(
    monitorViewModel: MonitorViewModel,
    onDialogCancel: (mac: Int) -> Unit,
    registerMonitorViewModel: RegisterMonitorViewModel,
    navigateUp: () -> Unit,
    onInspectClick: (tire: String, temperature: Float, pressure: Float) -> Unit,
    onAssemblyClick: (tire: String) -> Unit,
    onDisassemblyClick: (tire: String, temperature: Float, pressure: Float) -> Unit,
    onMountTireClick: (position: String) -> Unit,
    paymentPlan: PaymentPlanType,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
    onEditTireClick: (position: String) -> Unit = {},
    sincronizacionViewModel: EstadoSincronizacionViewModel = hiltViewModel(),
    posicionesViewModel: PosicionesViewModel = hiltViewModel(),
) {
    val sincronizacion by sincronizacionViewModel.uiState.collectAsStateWithLifecycle()
    val posiciones by posicionesViewModel.uiState.collectAsStateWithLifecycle()
    val monitorUiState by monitorViewModel.monitorUiState.collectAsState()
    val tireUiState by monitorViewModel.tireUiState.collectAsState()
    val wifiStatus by monitorViewModel.wifiStatus.collectAsState()

    val context = LocalContext.current
    val buttonCancelText =
        if (paymentPlan == PaymentPlanType.Complete || monitorUiState.monitorId != 0) {
            stringResource(R.string.cerrar)
        } else stringResource(R.string.logout)

    LaunchedEffect(monitorUiState.monitorId) {
        monitorViewModel.initMonitorData()
    }

    MonitorScreenContent(
        monitorUiState = monitorUiState,
        positionsUiState = posiciones.ultimas,
        monitorTireUiState = posiciones.historial,
        tireUiState = tireUiState,
        wifiStatus = wifiStatus,
        paymentPlan = paymentPlan,
        navigateUp = navigateUp,
        showBackButton = showBackButton,
        onInspectClick = onInspectClick,
        onAssemblyClick = onAssemblyClick,
        onDisassemblyClick = onDisassemblyClick,
        onShowMonitorDialog = { show -> monitorViewModel.showMonitorDialog(show) },
        onGetLastedSensorData = posicionesViewModel::cargarUltimas,
        onUpdateSelectedTire = { tire -> monitorViewModel.updateSelectedTire(tire) },
        onGetSensorDataByWheel = { wheel -> monitorViewModel.getSensorDataByWheel(wheel) },
        onSwitchPressureUnit = { monitorViewModel.switchPressureUnit() },
        onSwitchTempUnit = { monitorViewModel.switchTemperatureUnit() },
        onGetTireDataByDate = posicionesViewModel::cargarHistorial,
        onCleanFilteredTire = posicionesViewModel::limpiarHistorial,
        setupWizardSlot = if (paymentPlan == PaymentPlanType.Complete) {
            {
                MonitorSetupWizard(
                    monitorUiState = monitorUiState,
                    registerMonitorViewModel = registerMonitorViewModel,
                    paymentPlan = paymentPlan,
                    onMountTireClick = onMountTireClick,
                    onEditTireClick = onEditTireClick,
                    onLinkedSensor = { monitorViewModel.initMonitorData() },
                    onFinish = { monitorViewModel.finishSetupWizard() },
                    // Sin configuración se regresa al Home, donde la única salida es cerrar sesión.
                    onCancelWithoutConfiguration = { onDialogCancel(0) },
                    onSwitchTemperature = { monitorViewModel.switchTemperatureUnit() },
                    onSwitchPressure = { monitorViewModel.switchPressureUnit() },
                    onSwitchOdometer = { monitorViewModel.switchOdometerUnit() },
                )
            }
        } else null,
        // El slot solo se pinta cuando el wizard no está activo; sin monitor el diálogo es
        // obligatorio en todos los planes.
        registerDialogSlot = {
            if (monitorUiState.showDialog) {
                ShowMonitorRegisterDialog(
                    monitorId = monitorUiState.monitorId,
                    cancelButtonText = buttonCancelText,
                    registerMonitorViewModel = registerMonitorViewModel,
                    onDialogCancel = { onDialogCancel(monitorUiState.monitorId) },
                    onSuccessRegister = { _ ->
                        monitorViewModel.initMonitorData()
                    },
                    context = context,
                    paymentPlan = paymentPlan,
                    // Fuera del wizard no se elige el número de llantas; se envía numTires = 0.
                    mountAllTires = false,
                )
            }
        },
        sincronizacion = sincronizacion,
        onVerSincronizacion = sincronizacionViewModel::mostrarHoja,
        onVerAlerta = monitorViewModel::verAlerta,
        modifier = modifier
    )

    if (paymentPlan == PaymentPlanType.Complete) {
        HojaSincronizacionRoute(viewModel = sincronizacionViewModel, uiState = sincronizacion)
    }
}
