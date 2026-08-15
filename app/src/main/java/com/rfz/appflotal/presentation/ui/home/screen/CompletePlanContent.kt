package com.rfz.appflotal.presentation.ui.home.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rfz.appflotal.core.util.screens.HombreCamionScreens
import com.rfz.appflotal.data.NetworkStatus
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.forums.navigation.ForumsGraph
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.CompletePlanScreen
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.viewmodel.CompletePlanViewModel
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType

@Composable
fun CompletePlanContent(
    paymentPlan: PaymentPlanType,
    userName: String,
    plates: String,
    wifiStatus: NetworkStatus,
    modifier: Modifier = Modifier,
    onShowMonitorDialog: (Boolean) -> Unit,
    onNavigate: (route: Any) -> Unit,
    completePlanViewModel: CompletePlanViewModel = viewModel()
) {
    val state by completePlanViewModel.uiState.collectAsState()

    CompletePlanScreen(
        state = state.copy(
            userName = userName,
            vehiclePlate = plates,
            paymentPlanType = paymentPlan
        ),
        onNotificationsClick = { /* TODO: Implement notifications logic */ },
        onVehicleDetailClick = { /* TODO: Implement vehicle detail navigation */ },
        onAlertsSeeAllClick = { /* TODO: Implement alerts navigation */ },
        onSectionClick = { section -> onNavigate(section.route) },
        onBlogSeeAllClick = { onNavigate(ForumsGraph) },
        onNavItemClick = { index -> completePlanViewModel.onNavItemClick(index) },
        onMapClick = { onNavigate(HombreCamionScreens.MAPA_VIAL.name) },
        onWeatherClick = { onNavigate(HombreCamionScreens.WEATHER.name) }
    )
}

@Preview(showBackground = true)
@Composable
fun CompletePlanContentPreview() {
    HombreCamionTheme {
        CompletePlanContent(
            paymentPlan = PaymentPlanType.Complete,
            userName = "Juan Perez",
            plates = "ABC-123",
            wifiStatus = NetworkStatus.Connected,
            onShowMonitorDialog = {},
            onNavigate = {}
        )
    }
}
