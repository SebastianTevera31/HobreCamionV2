package com.rfz.appflotal.presentation.ui.home.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rfz.appflotal.data.NetworkStatus
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import com.rfz.appflotal.presentation.ui.monitor.screen.MonitorScreenContent
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.ListOfTireData
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorUiState
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.RegisterMonitorViewModel
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.TireUiState

@Composable
fun HomeContent(
    uiState: MonitorUiState,
    positionUiState: ApiResult<List<ListOfTireData>?>,
    monitorTireUiState: ApiResult<List<ListOfTireData>?>,
    tireUiState: TireUiState,
    paymentPlan: PaymentPlanType,
    wifiStatus: NetworkStatus,
    onShowMonitorDialog: (Boolean) -> Unit,
    onNavigate: (route: String) -> Unit,
    onBack: () -> Unit,
    onDialogCancel: (mac: Int) -> Unit,
    onInspectClick: (tire: String, temperature: Float, pressure: Float) -> Unit,
    onAssemblyClick: (tire: String) -> Unit,
    onDisassemblyClick: (tire: String, temperature: Float, pressure: Float) -> Unit,
    onGetTireDataByDate: (position: String, date: String) -> Unit,
    onCleanFilteredTire: () -> Unit,
    onSwitchPressureUnit: () -> Unit,
    onSwitchTempUnit: () -> Unit,
    onGetLastedSensorData: () -> Unit,
    onGetBitmapImage: () -> Unit,
    onUpdateSelectedTire: (String) -> Unit,
    onGetSensorDataByWheel: (String) -> Unit,
    plates: String,
    userName: String,
    registerMonitorViewModel: RegisterMonitorViewModel?,
    modifier: Modifier = Modifier
) {
    when (paymentPlan) {
        PaymentPlanType.Complete -> CompletePlanContent(
            paymentPlan = paymentPlan,
            userName = userName,
            plates = plates,
            wifiStatus = wifiStatus,
            onShowMonitorDialog = {
                onShowMonitorDialog(it)
            },
            onNavigate = onNavigate,
            modifier = modifier,
        )

        else -> {
            MonitorScreenContent(
                monitorUiState = uiState,
                positionsUiState = positionUiState,
                monitorTireUiState = monitorTireUiState,
                tireUiState = tireUiState,
                wifiStatus = wifiStatus,
                paymentPlan = paymentPlan,
                onDialogCancel = { mac ->
                    onDialogCancel(mac)
                },
                navigateUp = onBack,
                onInspectClick = { tire, temperature, pressure ->
                    onInspectClick(tire, temperature, pressure)
                },
                onAssemblyClick = { tire ->
                    onAssemblyClick(tire)
                },
                onDisassemblyClick = { tire, temperature, pressure ->
                    onDisassemblyClick(tire, temperature, pressure)
                },
                onShowMonitorDialog = {
                    onShowMonitorDialog(it)
                },
                onGetLastedSensorData = onGetLastedSensorData,
                onGetBitmapImage = onGetBitmapImage,
                onUpdateSelectedTire = { tire ->
                    onUpdateSelectedTire(tire)
                },
                onGetSensorDataByWheel = { tire ->
                    onGetSensorDataByWheel(tire)
                },
                onSwitchPressureUnit = onSwitchPressureUnit,
                onSwitchTempUnit = onSwitchTempUnit,
                onGetTireDataByDate = { position, date ->
                    onGetTireDataByDate(position, date)
                },
                onCleanFilteredTire = onCleanFilteredTire,
                registerMonitorViewModel = registerMonitorViewModel,
                modifier = modifier
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeContentPreview() {
    HombreCamionTheme {
        HomeContent(
            uiState = MonitorUiState(),
            positionUiState = ApiResult.Success(emptyList()),
            monitorTireUiState = ApiResult.Success(emptyList()),
            tireUiState = TireUiState(),
            paymentPlan = PaymentPlanType.Complete,
            wifiStatus = NetworkStatus.Connected,
            onShowMonitorDialog = {},
            onNavigate = {},
            onBack = {},
            onDialogCancel = {},
            onInspectClick = { _, _, _ -> },
            onAssemblyClick = {},
            onDisassemblyClick = { _, _, _ -> },
            onGetTireDataByDate = { _, _ -> },
            onCleanFilteredTire = {},
            onSwitchPressureUnit = {},
            onSwitchTempUnit = {},
            onGetLastedSensorData = {},
            onGetBitmapImage = {},
            onUpdateSelectedTire = {},
            onGetSensorDataByWheel = {},
            plates = "ABC-123",
            userName = "Juan Perez",
            registerMonitorViewModel = null
        )
    }
}
