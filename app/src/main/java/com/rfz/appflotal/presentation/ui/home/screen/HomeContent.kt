package com.rfz.appflotal.presentation.ui.home.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
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
    onNavigate: (route: Any) -> Unit,
    onBack: () -> Unit,
    onInspectClick: (tire: String, temperature: Float, pressure: Float) -> Unit,
    onAssemblyClick: (tire: String) -> Unit,
    onDisassemblyClick: (tire: String, temperature: Float, pressure: Float) -> Unit,
    onGetTireDataByDate: (position: String, date: String) -> Unit,
    onCleanFilteredTire: () -> Unit,
    onSwitchPressureUnit: () -> Unit,
    onSwitchTempUnit: () -> Unit,
    onGetLastedSensorData: () -> Unit,
    onUpdateSelectedTire: (String) -> Unit,
    onGetSensorDataByWheel: (String) -> Unit,
    plates: String,
    userName: String,
    paddingValues: PaddingValues,
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
                modifier = Modifier.padding(top = paddingValues.calculateTopPadding())
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
            onInspectClick = { _, _, _ -> },
            onAssemblyClick = {},
            onDisassemblyClick = { _, _, _ -> },
            onGetTireDataByDate = { _, _ -> },
            onCleanFilteredTire = {},
            onSwitchPressureUnit = {},
            onSwitchTempUnit = {},
            onGetLastedSensorData = {},
            onUpdateSelectedTire = {},
            onGetSensorDataByWheel = {},
            plates = "ABC-123",
            userName = "Juan Perez",
            paddingValues = PaddingValues()
        )
    }
}
