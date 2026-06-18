package com.rfz.appflotal.presentation.ui.reportes.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.rfz.appflotal.presentation.ui.reportes.ui.MenuReportesView
import com.rfz.appflotal.presentation.ui.reportes.ui.co2.CO2ReportViewModel
import com.rfz.appflotal.presentation.ui.reportes.ui.co2.Co2EmissionReportRoute
import com.rfz.appflotal.presentation.ui.reportes.ui.cpk.CpkReportViewModel
import com.rfz.appflotal.presentation.ui.reportes.ui.cpk.MenuRendimientoScreen
import com.rfz.appflotal.presentation.ui.reportes.ui.cpk.RendimientoRuedaScreen
import com.rfz.appflotal.presentation.ui.reportes.ui.fuel.FuelConsumptionReportRoute
import com.rfz.appflotal.presentation.ui.reportes.ui.fuel.FuelReportViewModel

fun NavGraphBuilder.reportGraph(
    navController: NavHostController
) {
    navigation<ReportGraph>(
        startDestination = ReportMenu
    ) {
        composable<ReportMenu> {
            MenuReportesView(
                onNavigate = {
                    navController.navigate(it)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<FuelConsumption> {
            val viewModel: FuelReportViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.loadData()
            }

            FuelConsumptionReportRoute(
                onBack = { navController.popBackStack() },
                reports = state.reports,
                screenState = state.loadState
            )
        }

        composable<CpkReport> {
            val viewModel: CpkReportViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            val context = LocalContext.current

            MenuRendimientoScreen(
                wheels = state.tireList,
                reports = state.allReports,
                tires = state.detailTireList,
                onLoadData = {
                    viewModel.loadData()
                },
                loadState = state.menuLoadState,
                exportLoadState = state.exportPdfState,
                pdfUri = state.pdfUri,
                onExportPdf = { uri -> viewModel.updatePdfUri(uri) },
                onSharePdf = { uri ->
                    viewModel.sharePdfReport(context, uri)
                },
                onClearPdfState = {
                    viewModel.resetExportState()
                },
                onWheelSelected = {
                    viewModel.selectTire(it)
                    navController.navigate(CpkDetail)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<CpkDetail> {
            val viewModel: CpkReportViewModel = hiltViewModel(
                navController.getBackStackEntry<CpkReport>()
            )
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
                onClearPdfState = {
                    viewModel.resetExportState()
                }
            )
        }

        composable<CO2Emissions> {
            val viewModel: CO2ReportViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.loadData()
            }

            Co2EmissionReportRoute(
                screenState = state.loadState,
                reports = state.reports,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
