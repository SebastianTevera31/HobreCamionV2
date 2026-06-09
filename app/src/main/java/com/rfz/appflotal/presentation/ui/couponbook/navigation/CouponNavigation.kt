package com.rfz.appflotal.presentation.ui.couponbook.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.rfz.appflotal.presentation.ui.couponbook.screen.redeem.RedeemCoupon
import com.rfz.appflotal.presentation.ui.forums.components.scaffold.ForumModuleScaffold
import com.rfz.appflotal.presentation.ui.forums.components.scaffold.ForumSearchConfig
import com.rfz.appflotal.presentation.ui.forums.components.scaffold.ForumTopBarConfig
import com.rfz.appflotal.presentation.ui.forums.navigation.SavedCommentsNav
import com.rfz.appflotal.presentation.ui.utils.LoadState

fun NavGraphBuilder.couponGraph(
    navController: NavHostController
) {
    navigation<CouponGraph>(
        startDestination = CouponMenu
    ) {
        composable<CouponMenu> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry<CouponGraph>()
                } catch (_: Exception) {
                    backStackEntry
                }
            }
            val viewModel: CouponBookViewModel = hiltViewModel(parentEntry)
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.clearFilterSearch()
                viewModel.getInitialData()
            }

            LaunchedEffect(state.validateState) {
                if (state.validateState is LoadState.Success) {
                    navController.navigate(RedeemCoupon(state.selectedCoupon?.fldCode ?: ""))
                    viewModel.resetValidateState()
                }
            }

            ForumModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = "Cuponera",
                    showBackButton = true,
                    showMenuButton = false,
                    searchConfig = ForumSearchConfig(
                        value = state.searchQuery,
                        placeholder = "Buscar...",
                        onValueChange = { query ->
                            viewModel.onSearchChanged(query)
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
                    nearbyCoupons = state.filteredCoupons,
                    myCoupons = state.filteredVouchers,
                    onSeeAllCoupons = {
                        navController.navigate(CouponList(true))
                    },
                    onSeeAllVouchers = {
                        navController.navigate(CouponList(false))
                    },
                    onCouponClick = { id ->
                        viewModel.selectCoupon(id)
                        navController.navigate(CouponInfo)
                    },
                    onVoucherClick = { id ->
                        viewModel.selectCoupon(id)
                        viewModel.validateVoucher(id)
                    },
                    modifier = Modifier.padding(paddingValues),
                    screenStatus = state.loadingScreen,
                    onLoadData = {
                        viewModel.getInitialData()
                    }
                )
            }
        }

        composable<CouponList> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry<CouponGraph>()
                } catch (_: Exception) {
                    backStackEntry
                }
            }

            val viewModel: CouponBookViewModel = hiltViewModel(parentEntry)
            val state by viewModel.uiState.collectAsState()
            val args = backStackEntry.toRoute<CouponList>()

            LaunchedEffect(state.validateState) {
                if (state.validateState is LoadState.Success) {
                    navController.navigate(RedeemCoupon(state.selectedCoupon?.fldCode ?: ""))
                    viewModel.resetValidateState()
                }
            }

            ForumModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = if (args.areCoupons) "Cupones" else "Mis Vouchers",
                    showBackButton = true,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            ) { paddingValue ->
                CouponBookListRoute(
                    coupons = if (args.areCoupons) state.filteredCoupons else state.filteredVouchers,
                    selectedFilter = state.selectedFilter,
                    filterOptions = state.filterOptions,
                    selectedCoupon = state.selectedCoupon,
                    onFilterBy = { option ->
                        viewModel.selectFilterOption(option)
                    },
                    onCouponClick = { id ->
                        viewModel.selectCoupon(id)
                        viewModel.validateVoucher(id)
                    },
                    onGettingVoucher = { code ->
                        viewModel.selectCoupon(code)
                        navController.navigate(CouponInfo)
                    },
                    onRedeem = { code ->
                        navController.navigate(RedeemCoupon(code))
                    },
                    modifier = Modifier.padding(paddingValue),
                    areCoupons = args.areCoupons,
                    validateState = state.validateState,
                    onResetValidateState = { viewModel.resetValidateState() }
                )
            }
        }

        composable<CouponInfo> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry<CouponGraph>()
                } catch (_: Exception) {
                    backStackEntry
                }
            }
            val viewModel: CouponBookViewModel = hiltViewModel(parentEntry)
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(state.acquireState) {
                if (state.acquireState is LoadState.Success) {
                    navController.popBackStack()
                    viewModel.resetAcquireState()
                }
            }

            CouponBookInfo(
                coupon = state.selectedCoupon!!,
                modifier = Modifier.safeContentPadding(),
                onBack = {
                    navController.popBackStack()
                },
                onGettingVoucher = { code ->
                    viewModel.acquireVoucher(code.toIntOrNull() ?: 0)
                },
                gettingCouponState = state.acquireState
            )
        }

        composable<RedeemCoupon> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry<CouponGraph>()
                } catch (_: Exception) {
                    backStackEntry
                }
            }
            val viewModel: CouponBookViewModel = hiltViewModel(parentEntry)
            val state by viewModel.uiState.collectAsState()
            RedeemCoupon(
                coupon = state.selectedCoupon!!,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
