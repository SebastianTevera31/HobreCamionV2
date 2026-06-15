package com.rfz.appflotal.presentation.ui.reportes.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.rfz.appflotal.presentation.ui.reportes.MenuReportesView
import com.rfz.appflotal.presentation.ui.reportes.viewmodel.ReportViewModel

fun NavGraphBuilder.reportGraph(
    navController: NavHostController
) {
    navigation<ReportGraph>(
        startDestination = ReportMenu
    ) {
        composable<ReportMenu> { backstackEntry ->
            val parentEntry = remember(backstackEntry) {
                try {
                    navController.getBackStackEntry<ReportGraph>()
                } catch (_: Exception) {
                    backstackEntry
                }
            }

            val viewModel: ReportViewModel = hiltViewModel(parentEntry)
            val state by viewModel.uiState.collectAsState()

            MenuReportesView(
                onNavigate = {
                    navController.navigate(it)
                },
            )
        }

        composable<FuelConsumption> { backstackEntry ->
            val parentEntry = remember(backstackEntry) {
                try {
                    navController.getBackStackEntry<ReportGraph>()
                } catch (_: Exception) {
                    backstackEntry
                }
            }

            val viewModel: ReportViewModel = hiltViewModel(parentEntry)
            val state by viewModel.uiState.collectAsState()

            MenuReportesView(
                onNavigate = {
                    navController.navigate(it)
                },
            )
        }

        composable<CpkReport> { backstackEntry ->
            val parentEntry = remember(backstackEntry) {
                try {
                    navController.getBackStackEntry<ReportGraph>()
                } catch (_: Exception) {
                    backstackEntry
                }
            }
            val viewModel: ReportViewModel = hiltViewModel(parentEntry)
            val state by viewModel.uiState.collectAsState()
        }
    }
}