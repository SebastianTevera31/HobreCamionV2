package com.rfz.appflotal.presentation.ui.reportes.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.rfz.appflotal.presentation.ui.reportes.MenuReportesView
import com.rfz.appflotal.presentation.ui.reportes.rendimiento.Co2EmissionsRoute
import com.rfz.appflotal.presentation.ui.reportes.rendimiento.MenuRendimientoScreen
import com.rfz.appflotal.presentation.ui.reportes.rendimiento.RendimientoRuedaScreen
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
                onBack = { navController.popBackStack() }
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
                onBack = { navController.popBackStack() }
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

            MenuRendimientoScreen(
                wheels = state.tireList,
                onLoadData = {
                    viewModel.loadData()
                },
                loadState = state.menuLoadState,
                onWheelSelected = {
                    viewModel.getCpkReport(it)
                    navController.navigate(CpkDetail)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<CpkDetail> { backstackEntry ->
            val parentEntry = remember(backstackEntry) {
                try {
                    navController.getBackStackEntry<ReportGraph>()
                } catch (_: Exception) {
                    backstackEntry
                }
            }
            val viewModel: ReportViewModel = hiltViewModel(parentEntry)
            val state by viewModel.uiState.collectAsState()
            val context = LocalContext.current

            RendimientoRuedaScreen(
                onBack = {
                    viewModel.resetReportState()
                    viewModel.resetExportState()
                    navController.popBackStack()
                },
                loadState = state.reportLoadState,
                report = state.cpkReport,
                tire = state.tireInfo,
                tirePosition = state.selectedTire?.positionTire ?: "",
                exportLoadState = state.exportPdfState,
                onExportPdf = { uri -> viewModel.updatePdfUri(uri) },
                pdfUri = state.pdfUri,
                onShareImage = { uri ->
                    if (uri != null) {
                        viewModel.sharePdfReport(context, uri)
                    }
                },
            )
        }

        composable<CO2Emissions> { backstackEntry ->
            val parentEntry = remember(backstackEntry) {
                try {
                    navController.getBackStackEntry<ReportGraph>()
                } catch (_: Exception) {
                    backstackEntry
                }
            }
            val viewModel: ReportViewModel = hiltViewModel(parentEntry)
            val state by viewModel.uiState.collectAsState()

            Co2EmissionsRoute()
        }
    }
}