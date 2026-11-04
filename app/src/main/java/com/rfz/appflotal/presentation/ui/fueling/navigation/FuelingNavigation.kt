package com.rfz.appflotal.presentation.ui.fueling.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.navigation.popBackStackSafely
import com.rfz.appflotal.presentation.ui.fueling.FuelingFormScreen
import com.rfz.appflotal.presentation.ui.fueling.FuelingHistoryScreen
import com.rfz.appflotal.presentation.ui.fueling.model.FuelingSaveState
import com.rfz.appflotal.presentation.ui.fueling.viewmodel.FuelingViewModel

/**
 * Grafo del módulo de cargas de combustible (solo plan Complete: se entra desde las
 * secciones del home Complete).
 *
 * Se registra en el NavHost con:  fuelingGraph(navController)
 * Historial y formulario comparten un FuelingViewModel scopeado al grafo.
 */
fun NavGraphBuilder.fuelingGraph(navController: NavHostController) {
    navigation<FuelingGraph>(startDestination = FuelingHistory) {

        // 1) Historial ----------------------------------------------------------
        composable<FuelingHistory> { entry ->
            val viewModel = entry.sharedFuelingViewModel(navController)
            val state by viewModel.uiState.collectAsState()

            FuelingHistoryScreen(
                vehicle = state.vehicle,
                fuelings = state.fuelings,
                fuelTypes = state.fuelTypes,
                currentPage = state.currentPage,
                hasNextPage = state.hasNextPage,
                startDate = state.startDate,
                endDate = state.endDate,
                fuelTypeId = state.fuelTypeId,
                isLoading = state.isLoading,
                errorMessage = state.errorMessage,
                isOffline = state.isOffline,
                onBack = { navController.popBackStackSafely() },
                onNewFueling = { navController.navigate(FuelingForm) { launchSingleTop = true } },
                onApplyFilters = { start, end, fuelTypeId ->
                    viewModel.applyFilter(start, end, fuelTypeId)
                },
                onPageSelected = { page -> viewModel.goToPage(page) },
                onRetry = { viewModel.retry() }
            )
        }

        // 2) Alta de carga --------------------------------------------------------
        composable<FuelingForm> { entry ->
            val viewModel = entry.sharedFuelingViewModel(navController)
            val state by viewModel.uiState.collectAsState()

            val context = LocalContext.current
            val savedMessage = stringResource(R.string.fue_guardada)
            val errorMessage = stringResource(R.string.fue_error_guardar)
            LaunchedEffect(state.saveState) {
                when (state.saveState) {
                    FuelingSaveState.Saved -> {
                        Toast.makeText(context, savedMessage, Toast.LENGTH_SHORT).show()
                        viewModel.consumeSaveState()
                        navController.popBackStackSafely()
                    }

                    FuelingSaveState.Failed -> {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                        viewModel.consumeSaveState()
                    }

                    else -> Unit
                }
            }

            FuelingFormScreen(
                fuelTypes = state.fuelTypes,
                lastOdometer = state.lastOdometer,
                fuelTypesFailed = state.fuelTypesFailed,
                onRetryFuelTypes = { viewModel.loadFuelTypes() },
                isSaving = state.saveState == FuelingSaveState.Saving,
                isOffline = state.isOffline,
                onBack = { navController.popBackStackSafely() },
                onSubmit = { data -> viewModel.saveFueling(data) }
            )
        }
    }
}

/** FuelingViewModel scopeado al grafo para que ambas pantallas compartan estado. */
@Composable
private fun NavBackStackEntry.sharedFuelingViewModel(
    navController: NavHostController
): FuelingViewModel {
    val parentEntry = remember(this) {
        runCatching { navController.getBackStackEntry<FuelingGraph>() }.getOrDefault(this)
    }
    return hiltViewModel(parentEntry)
}
