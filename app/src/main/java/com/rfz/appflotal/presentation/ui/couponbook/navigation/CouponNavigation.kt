package com.rfz.appflotal.presentation.ui.couponbook.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.rfz.appflotal.core.util.screens.NavScreens
import com.rfz.appflotal.presentation.ui.couponbook.CouponBookViewModel
import com.rfz.appflotal.presentation.ui.couponbook.screen.main.CouponBookScreen
import com.rfz.appflotal.presentation.ui.forums.components.scaffold.ForumModuleScaffold
import com.rfz.appflotal.presentation.ui.forums.components.scaffold.ForumSearchConfig
import com.rfz.appflotal.presentation.ui.forums.components.scaffold.ForumTopBarConfig
import com.rfz.appflotal.presentation.ui.forums.navigation.ForumsGraph
import com.rfz.appflotal.presentation.ui.forums.navigation.SavedCommentsNav

fun NavGraphBuilder.couponGraph(
    navController: NavHostController
) {
    navigation<CouponGraph>(
        startDestination = CouponMenu
    ) {
        composable<CouponMenu> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry<ForumsGraph>()
                } catch (_: Exception) {
                    backStackEntry
                }
            }
            val viewModel: CouponBookViewModel = hiltViewModel(parentEntry)
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.clearFilterSearch()
                viewModel.getInitialData(forceRefresh = true)
            }

            ForumModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = "Cuponera",
                    showBackButton = true,
                    showMenuButton = true,
                    searchConfig = ForumSearchConfig(
                        value = state.searchQuery,
                        placeholder = "Buscar talleres, llantas, refacciones",
                        onValueChange = {
                            viewModel.onSearchChanged()
                        }
                    ),
                    onBackClick = {
                        navController.navigate(NavScreens.HOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onMenuClick = {
                        navController.navigate(SavedCommentsNav)
                    }
                )
            ) {
                CouponBookScreen()
            }
        }

        composable<GettingCoupon> {

        }
    }
}