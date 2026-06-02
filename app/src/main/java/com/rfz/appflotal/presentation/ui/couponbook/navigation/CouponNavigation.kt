package com.rfz.appflotal.presentation.ui.couponbook.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.rfz.appflotal.core.util.screens.NavScreens
import com.rfz.appflotal.presentation.ui.couponbook.CouponBookViewModel
import com.rfz.appflotal.presentation.ui.couponbook.screen.info.CouponBookInfo
import com.rfz.appflotal.presentation.ui.couponbook.screen.main.CouponBookListRoute
import com.rfz.appflotal.presentation.ui.couponbook.screen.main.CouponBookRoute
import com.rfz.appflotal.presentation.ui.couponbook.screen.redeem.RedeemCupon
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
                    showMenuButton = false,
                    searchConfig = ForumSearchConfig(
                        value = state.searchQuery,
                        placeholder = "Buscar...",
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
            ) { paddingValues ->
                CouponBookRoute(
                    onVerTodosClick = {
                        navController.navigate(CouponList)
                    },
                    onCouponClick = { id ->
                        navController.navigate(CouponInfo(id))
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }

        composable<CouponList> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry<ForumsGraph>()
                } catch (_: Exception) {
                    backStackEntry
                }
            }
            val viewModel: CouponBookViewModel = hiltViewModel(parentEntry)
            val state by viewModel.uiState.collectAsState()

            ForumModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = "Cupones",
                    showBackButton = true,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            ) { paddingValue ->
                CouponBookListRoute(
                    selectedFilter = state.selectedFilter,
                    filterOptions = state.filterOptions,
                    onFilterBy = { option ->
                        viewModel.selectFilterOption(option)
                    },
                    onCouponClick = { id ->
                        navController.navigate(CouponInfo(id))
                    },
                    modifier = Modifier.padding(paddingValue)
                )
            }
        }

        composable<CouponInfo> { backStackEntry ->
            val route: CouponInfo = backStackEntry.toRoute()
            CouponBookInfo(
                modifier = Modifier.safeContentPadding(),
                onBack = {
                    navController.popBackStack()
                },
                onRedeem = {
                    navController.navigate(RedeemCoupon)
                }
            )
        }

        composable<RedeemCoupon> { backStackEntry ->
            RedeemCupon()
        }
    }
}
