package com.rfz.appflotal.presentation.ui.home.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rfz.appflotal.core.util.screens.HombreCamionScreens
import com.rfz.appflotal.data.NetworkStatus
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.forums.navigation.ForumsGraph
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.CompletePlanScreen
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.viewmodel.CompletePlanViewModel
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import com.rfz.appflotal.presentation.ui.reportes.navigation.ReportGraph

@Composable
fun CompletePlanContent(
    paymentPlan: PaymentPlanType,
    userName: String,
    plates: String,
    vehicleType: String,
    wifiStatus: NetworkStatus,
    modifier: Modifier = Modifier,
    // El main se dibuja bajo la barra superior del Scaffold; el indicador de recarga
    // se desplaza esta distancia para quedar visible.
    indicatorTopPadding: Dp = 0.dp,
    onShowMonitorDialog: (Boolean) -> Unit,
    onNavigate: (route: Any) -> Unit,
    viewModel: CompletePlanViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getInitialData()
    }

    CompletePlanScreen(
        state = state.copy(
            vehicleType = vehicleType,
            userName = userName,
            vehiclePlate = plates,
            paymentPlanType = paymentPlan,
            isOffline = wifiStatus != NetworkStatus.Connected
        ),
        onNotificationsClick = { onNavigate(HombreCamionScreens.NOTIFICATIONS.name) },
        onVehicleDetailClick = { onNavigate(ReportGraph) },
        onAlertsSeeAllClick = { onNavigate(HombreCamionScreens.ALERTS.name) },
        onSectionClick = { section -> onNavigate(section.route) },
        onBlogSeeAllClick = { onNavigate(ForumsGraph) },
        onMapClick = { onNavigate(HombreCamionScreens.MAPA_VIAL.name) },
        onWeatherClick = { onNavigate(HombreCamionScreens.WEATHER.name) },
        onRefresh = viewModel::refresh,
        indicatorTopPadding = indicatorTopPadding
    )
}

@Preview(showBackground = true)
@Composable
fun CompletePlanContentPreview() {
    HombreCamionTheme {
        CompletePlanContent(
            paymentPlan = PaymentPlanType.Complete,
            userName = "Juan Perez",
            vehicleType = "Camioneta",
            plates = "ABC-123",
            wifiStatus = NetworkStatus.Connected,
            onShowMonitorDialog = {},
            onNavigate = {}
        )
    }
}
