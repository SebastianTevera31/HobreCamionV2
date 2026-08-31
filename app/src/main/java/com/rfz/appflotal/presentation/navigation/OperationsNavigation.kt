package com.rfz.appflotal.presentation.navigation

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rfz.appflotal.core.util.screens.HombreCamionScreens
import com.rfz.appflotal.core.util.screens.NavScreens
import com.rfz.appflotal.presentation.ui.assembly.screen.AssemblyTireScreen
import com.rfz.appflotal.presentation.ui.assembly.viewmodel.AssemblyTireViewModel
import com.rfz.appflotal.presentation.ui.cambiodestino.screen.CambioDestinoScreen
import com.rfz.appflotal.presentation.ui.cambiodestino.viewmodel.CambioDestinoViewModel
import com.rfz.appflotal.presentation.ui.dissassembly.screen.DisassemblyTireScreen
import com.rfz.appflotal.presentation.ui.dissassembly.viewmodel.DisassemblyViewModel
import com.rfz.appflotal.presentation.ui.home.screen.ShareFeedbackScreen
import com.rfz.appflotal.presentation.ui.home.viewmodel.HomeViewModel
import com.rfz.appflotal.presentation.ui.inspection.screens.InspectionRoute
import com.rfz.appflotal.presentation.ui.inspection.viewmodel.InspectionViewModel
import com.rfz.appflotal.presentation.ui.repararrenovar.screen.RepararRenovarScreen
import com.rfz.appflotal.presentation.ui.repararrenovar.viewmodel.RepararRenovarViewModel
import com.rfz.appflotal.presentation.ui.scrap.screens.TireWastePileScreen
import com.rfz.appflotal.presentation.ui.scrap.viewmodel.TireWasteViewModel

fun NavGraphBuilder.operationsNavigation(
    navController: NavController,
    homeViewModel: HomeViewModel
) {
    composable(
        route = "${NavScreens.INSPECCION}/{tire}?temp={temp}&pressure={pressure}",
        arguments = listOf(navArgument("tire") {
            type = NavType.StringType
        }, navArgument("temp") {
            type = NavType.FloatType; defaultValue = 0
        }, navArgument("pressure") {
            type = NavType.FloatType; defaultValue = 0
        })
    ) { backStackEntry ->
        val inspectionViewModel: InspectionViewModel = hiltViewModel()
        val tire = backStackEntry.arguments?.getString("tire") ?: ""
        val temp = backStackEntry.arguments?.getFloat("temp") ?: 0.0
        val pressure = backStackEntry.arguments?.getFloat("pressure") ?: 0.0

        InspectionRoute(
            tire = tire,
            temperature = temp.toFloat(),
            pressure = pressure.toFloat(),
            onBack = {
                navController.navigate(HombreCamionScreens.MONITOR.name) {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(HombreCamionScreens.MONITOR.name)
                }
            },
            onFinish = { tire ->
                navController.navigate(HombreCamionScreens.MONITOR.name) {
                    launchSingleTop = true
                    popUpTo(HombreCamionScreens.MONITOR.name)
                }
            },
            viewModel = inspectionViewModel
        )
    }

    composable(
        route = "${NavScreens.MONTAJE}/{tire}", arguments = listOf(
            navArgument("tire") {
                type = NavType.StringType
            })
    ) { backStackEntry ->
        val assemblyTireViewModel: AssemblyTireViewModel = hiltViewModel()
        val positionTire = backStackEntry.arguments?.getString("tire") ?: ""
        AssemblyTireScreen(
            positionTire = positionTire,
            viewModel = assemblyTireViewModel,
            onBack = {
                navController.popBackStack()
            })
    }

    composable(
        route = "${NavScreens.DESMONTAJE}/{tire}?temp={temp}&pressure={pressure}",
        arguments = listOf(navArgument("tire") {
            type = NavType.StringType
        }, navArgument("temp") {
            type = NavType.FloatType; defaultValue = 0
        }, navArgument("pressure") {
            type = NavType.FloatType; defaultValue = 0
        })
    ) { backStackEntry ->
        val disassemblyTireViewModel: DisassemblyViewModel = hiltViewModel()
        val tire = backStackEntry.arguments?.getString("tire") ?: ""
        val temp = backStackEntry.arguments?.getFloat("temp") ?: 0.0
        val pressure = backStackEntry.arguments?.getFloat("pressure") ?: 0.0
        DisassemblyTireScreen(
            positionTire = tire,
            initialTemperature = temp.toFloat(),
            initialPressure = pressure.toFloat(),
            viewModel = disassemblyTireViewModel,
            onBack = {
                navController.popBackStack()
            },
            onFinish = {
                navController.navigate(HombreCamionScreens.MONITOR.name) {
                    launchSingleTop = true
                    popUpTo(HombreCamionScreens.MONITOR.name)
                }
            }
        )
    }

    composable(route = NavScreens.DESECHO) {
        val tireWasteViewModel: TireWasteViewModel = hiltViewModel()
        TireWastePileScreen(
            onBack = { navController.popBackStack() },
            viewModel = tireWasteViewModel,
        )
    }

    composable(route = NavScreens.REPARARRENOVAR) {
        val repararRenovarViewModel: RepararRenovarViewModel = hiltViewModel()
        RepararRenovarScreen(
            onBack = { navController.popBackStack() },
            viewModel = repararRenovarViewModel,
        )
    }

    composable(route = NavScreens.CAMBIO_DESTINO) {
        val cambioDestinoViewModel: CambioDestinoViewModel = hiltViewModel()
        CambioDestinoScreen(
            onBack = { navController.popBackStack() },
            viewModel = cambioDestinoViewModel,
        )
    }

    composable(route = NavScreens.COMENTARIOS) {
        val msgOperationState = homeViewModel.messageOperationState.collectAsState()
        ShareFeedbackScreen(
            onShare = { feedback ->
                homeViewModel.onSendFeedback(
                    feedback
                )
            },
            onBack = { navController.popBackStack() },
            messageOperationState = msgOperationState.value,
        )
    }
}
