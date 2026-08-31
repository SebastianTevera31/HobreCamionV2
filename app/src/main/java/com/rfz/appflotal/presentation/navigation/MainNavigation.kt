package com.rfz.appflotal.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rfz.appflotal.core.util.screens.HombreCamionScreens
import com.rfz.appflotal.core.util.screens.NavScreens
import com.rfz.appflotal.data.network.service.HombreCamionService
import com.rfz.appflotal.presentation.ui.alerts.AlertsScreen
import com.rfz.appflotal.presentation.ui.home.screen.HomeScreen
import com.rfz.appflotal.presentation.ui.home.viewmodel.HomeViewModel
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import com.rfz.appflotal.presentation.ui.inicio.ui.arePermissionsGranted
import com.rfz.appflotal.presentation.ui.inicio.ui.getRequiredPermissions
import com.rfz.appflotal.presentation.ui.inicio.ui.isServiceRunning
import com.rfz.appflotal.presentation.ui.monitor.screen.MonitorScreen
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorViewModel
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.RegisterMonitorViewModel
import com.rfz.appflotal.presentation.ui.registrollantasscreen.screens.MenuTireScreen
import com.rfz.appflotal.presentation.ui.updateuserscreen.screen.UpdateUserScreen
import com.rfz.appflotal.presentation.ui.updateuserscreen.viewmodel.UpdateUserViewModel
import com.rfz.appflotal.presentation.ui.vialstatus.view.VialStatusScreen
import com.rfz.appflotal.presentation.ui.vialstatus.viewmodel.VialStatusViewModel
import com.rfz.appflotal.presentation.ui.weather.view.WeatherRoute
import com.rfz.appflotal.presentation.ui.weather.viewmodel.WeatherViewModel

fun NavGraphBuilder.mainNavigation(
    navController: NavController,
    homeViewModel: HomeViewModel,
    monitorViewModel: MonitorViewModel,
    registerMonitorViewModel: RegisterMonitorViewModel,
    updateUserViewModel: UpdateUserViewModel
) {
    composable(NavScreens.HOME) {
        val context = navController.context
        LaunchedEffect(Unit) {
            if (arePermissionsGranted(context, getRequiredPermissions())) {
                if (!isServiceRunning(context, HombreCamionService::class.java)) {
                    HombreCamionService.startService(context)
                }
            }
        }

        HomeScreen(
            navController = navController,
            homeViewModel = homeViewModel,
            registerMonitorViewModel = registerMonitorViewModel,
            onInspectClick = { tire, temp, pressure ->
                val route = "${NavScreens.INSPECCION}/$tire?temp=$temp&pressure=$pressure"
                navController.navigate(route) { launchSingleTop = true }
            },
            onAssemblyClick = { tire ->
                navController.navigate("${NavScreens.MONTAJE}/$tire") { launchSingleTop = true }
            },
            onDisassemblyClick = { tire, temp, pressure ->
                val route = "${NavScreens.DESMONTAJE}/$tire?temp=$temp&pressure=$pressure"
                navController.navigate(route) { launchSingleTop = true }
            },
            updateUserData = { selectedLanguage ->
                updateUserViewModel.fetchUserData(selectedLanguage)
            },
            monitorViewModel = monitorViewModel
        )
    }

    composable(HombreCamionScreens.MONITOR.name) {
        val context = navController.context
        LaunchedEffect(Unit) {
            if (arePermissionsGranted(context, getRequiredPermissions())) {
                if (!isServiceRunning(context, HombreCamionService::class.java)) {
                    HombreCamionService.startService(context)
                }
            }
        }

        MonitorScreen(
            monitorViewModel = monitorViewModel,
            registerMonitorViewModel = registerMonitorViewModel,
            navigateUp = { navController.navigateUp() },
            paymentPlan = PaymentPlanType.Complete,
            onDialogCancel = { monitorId ->
                registerMonitorViewModel.stopScan()
                if (monitorId != 0) {
                    monitorViewModel.showMonitorDialog(false)
                } else {
                    navController.navigateUp()
                }
            },
            modifier = Modifier.fillMaxSize(),
            onInspectClick = { tire, temp, pressure ->
                val route = "${NavScreens.INSPECCION}/$tire?temp=$temp&pressure=$pressure"
                navController.navigate(route) { launchSingleTop = true }
            },
            onAssemblyClick = { tire ->
                navController.navigate("${NavScreens.MONTAJE}/$tire") { launchSingleTop = true }
            },
            onDisassemblyClick = { tire, temp, pressure ->
                val route = "${NavScreens.DESMONTAJE}/$tire?temp=$temp&pressure=$pressure"
                navController.navigate(route) { launchSingleTop = true }
            })
    }

    composable(route = NavScreens.INFORMACION_USUARIO) {
        UpdateUserScreen(
            updateUserViewModel = updateUserViewModel,
        ) {
            navController.popBackStack()
        }
    }

    composable(route = HombreCamionScreens.MAPA_VIAL.name) {
        val vialStatusViewModel: VialStatusViewModel = hiltViewModel()
        VialStatusScreen(
            onBack = { navController.popBackStack() },
            viewModel = vialStatusViewModel
        )
    }

    composable(route = HombreCamionScreens.WEATHER.name) {
        val watherViewModel: WeatherViewModel = hiltViewModel()
        WeatherRoute(
            viewModel = watherViewModel,
            onNavigateToMap = {
                navController.navigate(HombreCamionScreens.MAPA_VIAL.name)
            },
            onBack = { navController.popBackStack() }
        )
    }

    composable(route = HombreCamionScreens.ALERTS.name) {
        AlertsScreen(
            onBack = { navController.popBackStack() },
        )
    }

    composable(route = HombreCamionScreens.REGISTER_TIRES.name) {
        MenuTireScreen(
            onNavigate = { route -> navController.navigate(route) },
            onBack = { navController.popBackStack() },
        )
    }
}
