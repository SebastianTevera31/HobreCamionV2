package com.rfz.appflotal.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.rfz.appflotal.presentation.ui.services.ServiceFormMode
import com.rfz.appflotal.presentation.ui.services.ServiceFormScreen
import com.rfz.appflotal.presentation.ui.services.ServicesScreen
import com.rfz.appflotal.presentation.ui.services.viewmodel.ServiceViewModel

/**
 * Grafo de navegación del módulo de servicios (flujo "solo servicios").
 *
 * Se registra en el NavHost con:  serviceGraph(navController)
 * y se entra a él con:            navController.navigate(ServiceGraph)
 *
 * Las dos pantallas comparten un mismo ServiceViewModel scopeado al grafo, así la
 * lista refleja los cambios tras guardar sin recargar de cero.
 */
fun NavGraphBuilder.serviceGraph(navController: NavHostController) {
    navigation<ServiceGraph>(startDestination = ServiceList) {

        // 1) Lista de servicios ------------------------------------------------
        composable<ServiceList> { entry ->
            val viewModel = entry.sharedServiceViewModel(navController)
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(Unit) { viewModel.load() }

            ServicesScreen(
                vehicle = state.vehicle,
                services = state.services,
                isLoading = state.isLoading,
                errorMessage = state.errorMessage,
                isOffline = state.isOffline,
                onBack = { navController.popBackStackSafely() },
                onNewService = { navController.navigate(ServiceFormRoute()) },
                onEditService = { serviceId ->
                    navController.navigate(ServiceFormRoute(serviceId))
                },
                onDeleteService = { serviceId -> viewModel.deleteService(serviceId) },
                onRetry = { viewModel.retry() }
            )
        }

        // 2) Formulario de servicio (alta / edición) ---------------------------
        composable<ServiceFormRoute> { entry ->
            val viewModel = entry.sharedServiceViewModel(navController)
            val state by viewModel.uiState.collectAsState()
            val args = entry.toRoute<ServiceFormRoute>()

            val mode =
                if (args.serviceId == null) ServiceFormMode.CREATE else ServiceFormMode.EDIT

            ServiceFormScreen(
                mode = mode,
                serviceTypes = state.serviceTypes,
                initial = args.serviceId?.let { viewModel.serviceById(it) },
                onBack = { navController.popBackStackSafely() },
                onSubmit = { data ->
                    viewModel.saveService(args.serviceId, data)
                    navController.popBackStackSafely()
                }
            )
        }
    }
}

/**
 * Devuelve el ServiceViewModel scopeado al grafo (no a la pantalla), de modo que
 * ambas pantallas del módulo comparten la misma instancia y su estado.
 */
@Composable
private fun NavBackStackEntry.sharedServiceViewModel(
    navController: NavHostController
): ServiceViewModel {
    val parentEntry = remember(this) {
        runCatching { navController.getBackStackEntry<ServiceGraph>() }.getOrDefault(this)
    }
    return hiltViewModel(parentEntry)
}
