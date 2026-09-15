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
import com.rfz.appflotal.presentation.ui.services.NewServiceOrderScreen
import com.rfz.appflotal.presentation.ui.services.ServiceFormMode
import com.rfz.appflotal.presentation.ui.services.ServiceFormScreen
import com.rfz.appflotal.presentation.ui.services.ServiceOrderDetailScreen
import com.rfz.appflotal.presentation.ui.services.ServicesScreen
import com.rfz.appflotal.presentation.ui.services.viewmodel.ServiceViewModel

/**
 * Grafo de navegación del módulo de servicios.
 *
 * Se registra en el NavHost con:  serviceGraph(navController)
 * y se entra a él con:            navController.navigate(ServiceGraph)
 *
 * Todas las pantallas comparten un mismo ServiceViewModel (ver [sharedServiceViewModel]),
 * scopeado al grafo. Las pantallas son "tontas": reciben datos + callbacks y no conocen
 * el NavController; aquí es donde cada callback se traduce en una navegación concreta.
 */
fun NavGraphBuilder.serviceGraph(navController: NavHostController) {
    navigation<ServiceGraph>(startDestination = ServiceList) {

        // 1) Lista de órdenes ---------------------------------------------------
        composable<ServiceList> { entry ->
            val viewModel = entry.sharedServiceViewModel(navController)
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.init()
            }

            ServicesScreen(
                vehicle = state.vehicle,
                orders = state.orders,
                isLoading = state.isLoading,
                errorMessage = state.errorMessage,
                isOffline = state.isOffline,
                onBack = { navController.popBackStackSafely() },
                onOpenOrder = { orderId ->
                    navController.navigate(ServiceOrderDetailRoute(orderId))
                },
                onNewOrder = { navController.navigate(ServiceOrderFormRoute()) },
                onEditOrder = { orderId ->
                    navController.navigate(ServiceOrderFormRoute(orderId))
                },
                onAddServices = { orderId ->
                    navController.navigate(ServiceFormRoute(orderId))
                },
                onDeleteOrder = { orderId -> viewModel.deleteOrder(orderId) },
                onRetry = { viewModel.retry() }
            )
        }

        // 2) Detalle de una orden ----------------------------------------------
        composable<ServiceOrderDetailRoute> { entry ->
            val viewModel = entry.sharedServiceViewModel(navController)
            val args = entry.toRoute<ServiceOrderDetailRoute>()
            val order = viewModel.orderById(args.orderId)

            // Si la orden ya no existe (p. ej. se eliminó), volvemos atrás.
            if (order == null) {
                LaunchedEffect(Unit) { navController.popBackStackSafely() }
                return@composable
            }

            ServiceOrderDetailScreen(
                order = order,
                items = viewModel.itemsOfOrder(args.orderId),
                onBack = { navController.popBackStackSafely() },
                onEditOrder = {
                    navController.navigate(ServiceOrderFormRoute(args.orderId))
                },
                onAddService = {
                    navController.navigate(ServiceFormRoute(args.orderId))
                },
                onEditService = { serviceId ->
                    navController.navigate(ServiceFormRoute(args.orderId, serviceId))
                },
                onDeleteService = { serviceId -> viewModel.deleteService(serviceId) },
                onDeleteOrder = {
                    viewModel.deleteOrder(args.orderId)
                    navController.popBackStackSafely()
                }
            )
        }

        // 3) Formulario de servicio (alta / edición) ---------------------------
        composable<ServiceFormRoute> { entry ->
            val viewModel = entry.sharedServiceViewModel(navController)
            val state by viewModel.uiState.collectAsState()
            val args = entry.toRoute<ServiceFormRoute>()

            val mode =
                if (args.serviceId == null) ServiceFormMode.CREATE else ServiceFormMode.EDIT

            ServiceFormScreen(
                mode = mode,
                services = state.serviceCatalog,
                providers = state.providers,
                occurrenceTypes = state.occurrenceTypes,
                initial = args.serviceId?.let { viewModel.itemById(it) },
                onBack = { navController.popBackStackSafely() },
                onSubmit = { data ->
                    viewModel.saveService(args.orderId, args.serviceId, data)
                    navController.popBackStackSafely()
                }
            )
        }

        // 4) Formulario de orden (crear / modificar) ---------------------------
        composable<ServiceOrderFormRoute> { entry ->
            val viewModel = entry.sharedServiceViewModel(navController)
            val args = entry.toRoute<ServiceOrderFormRoute>()
            val vehicles = viewModel.vehiclesForPicker()

            NewServiceOrderScreen(
                vehicles = vehicles,
                preselectedVehicleId = vehicles.firstOrNull()?.id,
                onBack = { navController.popBackStackSafely() },
                onSubmit = { data ->
                    viewModel.saveOrder(args.orderId, data)
                    navController.popBackStackSafely()
                }
            )
        }
    }
}

/**
 * Devuelve el ServiceViewModel scopeado al grafo (no a la pantalla), de modo que
 * todas las pantallas del módulo comparten la misma instancia y su estado.
 *
 * Se obtiene el BackStackEntry del propio grafo (ServiceGraph) y se pasa a
 * hiltViewModel(); si por algún motivo no está en la pila, cae al entry actual.
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
