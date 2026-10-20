package com.rfz.appflotal.presentation.ui.couponbook.navigation
import com.rfz.appflotal.presentation.navigation.popBackStackSafely

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.screens.NavScreens
import com.rfz.appflotal.presentation.ui.couponbook.CouponBookViewModel
import com.rfz.appflotal.presentation.ui.couponbook.screen.follow.FollowPromotionRoute
import com.rfz.appflotal.presentation.ui.couponbook.screen.follow.FollowPromotionViewModel
import com.rfz.appflotal.presentation.ui.couponbook.screen.follow.FollowedAccessoryDetailScreen
import com.rfz.appflotal.presentation.ui.couponbook.screen.follow.FollowedAccessoryDetailUiState
import com.rfz.appflotal.presentation.ui.couponbook.screen.follow.FollowedAccessoryDetailViewModel
import com.rfz.appflotal.presentation.ui.couponbook.screen.info.CouponBookInfo
import com.rfz.appflotal.presentation.ui.couponbook.screen.main.CouponBookListRoute
import com.rfz.appflotal.presentation.ui.couponbook.screen.main.CouponBookRoute
import com.rfz.appflotal.presentation.ui.couponbook.screen.promotions.PromotionDetailScreen
import com.rfz.appflotal.presentation.ui.couponbook.screen.promotions.PromotionListRoute
import com.rfz.appflotal.presentation.ui.couponbook.screen.redeem.RedeemCoupon
import com.rfz.appflotal.presentation.ui.forums.components.scaffold.ModernModuleScaffold
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
                when (state.validateState) {
                    is LoadState.Success -> {
                        navController.navigate(RedeemCoupon(state.selectedCoupon?.fldCode ?: ""))
                        viewModel.resetValidateState()
                    }

                    is LoadState.Error -> {
                        navController.navigate(CouponInfo)
                        viewModel.resetValidateState()
                    }

                    else -> {}
                }
            }

            ModernModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = stringResource(R.string.cuponera),
                    showBackButton = true,
                    showMenuButton = false,
                    onBackClick = {
                        navController.navigate(NavScreens.HOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onMenuClick = {
                        navController.navigate(SavedCommentsNav) {
                            launchSingleTop = true
                        }
                    },
                    searchConfig = ForumSearchConfig(
                        value = state.searchQuery,
                        placeholder = stringResource(R.string.buscar_cupones_promociones),
                        onValueChange = { query ->
                            viewModel.onMainSearchChanged(query)
                        }
                    )
                )
            ) { paddingValues ->
                CouponBookRoute(
                    nearbyCoupons = state.filteredCoupons,
                    myCoupons = state.filteredVouchers,
                    searchQuery = state.searchQuery,
                    promotions = state.promotions,
                    promotionsState = state.promotionsState,
                    onSeeAllCoupons = {
                        navController.navigate(CouponList(true))
                    },
                    onSeeAllVouchers = {
                        navController.navigate(CouponList(false))
                    },
                    onSeeAllPromotions = {
                        navController.navigate(PromotionList)
                    },
                    onCouponClick = { id ->
                        viewModel.selectCoupon(id)
                        navController.navigate(CouponInfo)
                    },
                    onVoucherClick = { id ->
                        viewModel.selectCoupon(id)
                        viewModel.validateVoucher(id)
                    },
                    onPromotionClick = { productUrl ->
                        viewModel.selectPromotion(productUrl)
                        navController.navigate(PromotionDetail)
                    },
                    onSeeFollowing = {
                        navController.navigate(FollowPromotions)
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

            LaunchedEffect(Unit) {
                viewModel.clearFilterSearch()
            }

            LaunchedEffect(state.validateState) {
                when (state.validateState) {
                    is LoadState.Success -> {
                        navController.navigate(RedeemCoupon(state.selectedCoupon?.fldCode ?: ""))
                        viewModel.resetValidateState()
                    }

                    is LoadState.Error -> {
                        navController.navigate(CouponInfo)
                        viewModel.resetValidateState()
                    }

                    else -> {}
                }
            }

            ModernModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = if (args.areCoupons) stringResource(R.string.cupones) else stringResource(R.string.mis_vouchers),
                    showBackButton = true,
                    showMenuButton = false,
                    onBackClick = {
                        navController.popBackStackSafely()
                    },
                    searchConfig = ForumSearchConfig(
                        value = state.searchQuery,
                        placeholder = stringResource(R.string.buscar),
                        onValueChange = { query ->
                            viewModel.onSearchChanged(query)
                        }
                    )
                )
            ) { paddingValue ->
                CouponBookListRoute(
                    coupons = if (args.areCoupons) state.filteredCoupons else state.filteredVouchers,
                    selectedFilter = state.selectedFilter,
                    filterOptions = state.filterOptions,
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
                    navController.popBackStackSafely()
                    viewModel.resetAcquireState()
                }
            }

            state.selectedCoupon?.let { coupon ->
                CouponBookInfo(
                    coupon = coupon,
                    modifier = Modifier.safeContentPadding(),
                    onBack = {
                        navController.popBackStackSafely()
                    },
                    onGettingVoucher = { code ->
                        viewModel.acquireVoucher(code.toIntOrNull() ?: 0)
                    },
                    gettingCouponState = state.acquireState
                )
            }
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
            state.selectedCoupon?.let { coupon ->
                RedeemCoupon(
                    coupon = coupon,
                    onBack = {
                        navController.popBackStackSafely()
                    }
                )
            }
        }

        composable<PromotionList> { backStackEntry ->
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
                viewModel.loadPromotionStores()
                viewModel.goToPromotionsPage(1)
            }

            ModernModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = stringResource(R.string.promociones_descuentos),
                    showBackButton = true,
                    showMenuButton = false,
                    onBackClick = {
                        navController.popBackStackSafely()
                    },
                    searchConfig = ForumSearchConfig(
                        value = state.promotionsSearchQuery,
                        placeholder = stringResource(R.string.buscar),
                        onValueChange = { query ->
                            viewModel.onPromotionsSearchChanged(query)
                        }
                    )
                )
            ) { paddingValue ->
                PromotionListRoute(
                    promotionsState = state.promotionsState,
                    promotions = state.promotions,
                    currentPage = state.promotionsPage,
                    hasNextPage = state.promotionsHasNextPage,
                    stores = state.promotionStores,
                    selectedStore = state.selectedPromotionStore,
                    onStoreSelected = { store ->
                        viewModel.onPromotionStoreSelected(store)
                    },
                    onPromotionClick = { productUrl ->
                        viewModel.selectPromotion(productUrl)
                        navController.navigate(PromotionDetail)
                    },
                    onPageSelected = { page ->
                        viewModel.goToPromotionsPage(page)
                    },
                    onRetry = {
                        viewModel.goToPromotionsPage(state.promotionsPage.coerceAtLeast(1))
                    },
                    modifier = Modifier.padding(paddingValue)
                )
            }
        }

        composable<PromotionDetail> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry<CouponGraph>()
                } catch (_: Exception) {
                    backStackEntry
                }
            }

            val viewModel: CouponBookViewModel = hiltViewModel(parentEntry)
            val state by viewModel.uiState.collectAsState()

            state.selectedPromotion?.let { promotion ->
                PromotionDetailScreen(
                    discount = promotion,
                    onBack = {
                        navController.popBackStackSafely()
                    }
                )
            }
        }

        composable<FollowPromotions> {
            val followViewModel: FollowPromotionViewModel = hiltViewModel()
            val state by followViewModel.uiState.collectAsState()

            ModernModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = stringResource(R.string.siguiendo),
                    showBackButton = true,
                    showMenuButton = false,
                    onBackClick = {
                        navController.popBackStackSafely()
                    }
                )
            ) { paddingValue ->
                FollowPromotionRoute(
                    viewModel = followViewModel,
                    state = state,
                    onOpenAccessory = { id ->
                        navController.navigate(FollowedAccessoryDetail(id))
                    },
                    modifier = Modifier.padding(paddingValue)
                )
            }
        }

        composable<FollowedAccessoryDetail> { backStackEntry ->
            val graphEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry<CouponGraph>()
                } catch (_: Exception) {
                    backStackEntry
                }
            }
            // Dejar de seguir pasa por el ViewModel de la pantalla principal, que muestra "Deshacer".
            val followEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry<FollowPromotions>()
                } catch (_: Exception) {
                    backStackEntry
                }
            }

            // El detalle de promoción lee la selección del ViewModel compartido del grafo.
            val couponViewModel: CouponBookViewModel = hiltViewModel(graphEntry)
            val followViewModel: FollowPromotionViewModel = hiltViewModel(followEntry)
            val detailViewModel: FollowedAccessoryDetailViewModel = hiltViewModel()
            val state by detailViewModel.uiState.collectAsState()
            val refreshStatus by detailViewModel.refreshStatus.collectAsState()

            ModernModuleScaffold(
                topBarConfig = ForumTopBarConfig(
                    title = (state as? FollowedAccessoryDetailUiState.Found)?.accessory?.name
                        ?: stringResource(R.string.siguiendo),
                    showBackButton = true,
                    showMenuButton = false,
                    onBackClick = {
                        navController.popBackStackSafely()
                    }
                )
            ) { paddingValue ->
                FollowedAccessoryDetailScreen(
                    state = state,
                    refreshStatus = refreshStatus,
                    onUnfollow = { accessory ->
                        followViewModel.onUnfollow(accessory)
                        navController.popBackStackSafely()
                    },
                    onRefresh = detailViewModel::onRefresh,
                    onPromotionClick = { discount ->
                        couponViewModel.selectPromotion(discount)
                        navController.navigate(PromotionDetail)
                    },
                    onBack = {
                        navController.popBackStackSafely()
                    },
                    modifier = Modifier.padding(paddingValue)
                )
            }
        }
    }
}
