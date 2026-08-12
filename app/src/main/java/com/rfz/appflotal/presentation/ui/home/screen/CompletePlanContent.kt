package com.rfz.appflotal.presentation.ui.home.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rfz.appflotal.data.NetworkStatus
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
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
        onWeatherMapClick = { /* TODO: Implement weather map navigation */ },
        onSectionClick = { section ->
            // El usuario puede mapear las secciones a rutas aquí
            // Ejemplo: onNavigate(section.route)
        },
        onBlogSeeAllClick = { /* TODO: Implement blog navigation */ },
        onNavItemClick = { index ->
            // El usuario puede manejar la navegación del bottom bar aquí
        }
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
