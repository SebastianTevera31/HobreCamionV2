package com.rfz.appflotal.presentation.ui.couponbook.screen.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.FireTruck
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.couponbook.Coupon
import com.rfz.appflotal.data.model.couponbook.ValidatedVoucher
import com.rfz.appflotal.data.model.couponbook.VoucherStatusType
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.components.LoadingDialog
import com.rfz.appflotal.presentation.ui.couponbook.CouponFilterOptions
import com.rfz.appflotal.presentation.ui.utils.LoadState

@Composable
fun CouponBookListRoute(
    areCoupons: Boolean,
    coupons: List<Coupon>,
    selectedFilter: CouponFilterOptions,
    filterOptions: List<CouponFilterOptions>,
    onFilterBy: (CouponFilterOptions) -> Unit,
    onCouponClick: (String) -> Unit,
    onGettingVoucher: (String) -> Unit,
    validateState: LoadState<ValidatedVoucher>,
    onResetValidateState: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(areCoupons, validateState) {
        if (!areCoupons && validateState is LoadState.Error) {
            snackbarHostState.showSnackbar(
                message = validateState.message,
                withDismissAction = true
            )

            onResetValidateState()
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState
                )
            }
        ) { innerPadding ->
            CouponBookListScreen(
                areCoupons = areCoupons,
                coupons = coupons,
                selectedFilter = selectedFilter,
                filterOptions = filterOptions,
                onFilterBy = onFilterBy,
                onCouponClick = { coupon ->
                    if (areCoupons) {
                        onGettingVoucher(coupon)
                    } else {
                        onCouponClick(coupon)
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }

        if (!areCoupons && validateState is LoadState.Loading) {
            LoadingDialog()
        }
    }
}

@Composable
fun CouponBookListScreen(
    areCoupons: Boolean,
    coupons: List<Coupon>,
    selectedFilter: CouponFilterOptions,
    filterOptions: List<CouponFilterOptions>,
    onFilterBy: (CouponFilterOptions) -> Unit,
    onCouponClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(
                horizontal = Dimens.PaddingMedium,
                vertical = Dimens.PaddingMedium
            ),
        verticalArrangement = Arrangement.spacedBy(
            Dimens.PaddingMedium
        )
    ) {
        CouponListHeader(
            areCoupons = areCoupons,
            numberOfCoupons = coupons.size
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(
                Dimens.PaddingExtraSmall
            )
        ) {
            Text(
                text = stringResource(R.string.filtrar_por),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    Dimens.PaddingExtraSmall
                ),
                contentPadding = PaddingValues(
                    end = Dimens.PaddingMedium
                )
            ) {
                items(
                    items = filterOptions,
                    key = { option -> option.text }
                ) { option ->
                    CouponChip(
                        text = stringResource(option.text),
                        selected = option == selectedFilter,
                        onChangeValue = {
                            onFilterBy(option)
                        }
                    )
                }
            }
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant
        )

        if (coupons.isEmpty()) {
            CouponListEmptyState(
                areCoupons = areCoupons,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(
                    Dimens.PaddingSmall
                ),
                contentPadding = PaddingValues(
                    bottom = Dimens.PaddingMedium
                )
            ) {
                items(
                    items = coupons,
                    key = { coupon -> coupon.fldCode }
                ) { coupon ->
                    NearestCuponCard(
                        coupon = coupon,
                        onClick = {
                            onCouponClick(coupon.fldCode)
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun CouponListHeader(
    areCoupons: Boolean,
    numberOfCoupons: Int,
    modifier: Modifier = Modifier
) {
    val title = if (areCoupons) {
        stringResource(R.string.cupones_disponibles)
    } else {
        stringResource(R.string.mis_cupones)
    }

    val subtitle = if (areCoupons) {
        stringResource(R.string.cupones_disponibles_descripcion)
    } else {
        stringResource(R.string.mis_cupones_descripcion)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        CouponCounter(
            numberOfCoupons = numberOfCoupons
        )
    }
}


@Composable
private fun CouponCounter(
    numberOfCoupons: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Text(
            text = numberOfCoupons.toString(),
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 7.dp
            ),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CouponListEmptyState(
    areCoupons: Boolean,
    modifier: Modifier = Modifier
) {
    val title = if (areCoupons) {
        stringResource(R.string.sin_cupones_disponibles)
    } else {
        stringResource(R.string.sin_cupones_con_este_filtro)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.PaddingLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    Dimens.PaddingSmall
                )
            ) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FireTruck,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(
                        R.string.sin_cupones_descripcion
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CouponChip(
    text: String,
    selected: Boolean,
    onChangeValue: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        modifier = modifier,
        selected = selected,
        onClick = onChangeValue,
        shape = RoundedCornerShape(50),
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Medium
                }
            )
        },
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = null,
                    modifier = Modifier.size(
                        FilterChipDefaults.IconSize
                    )
                )
            }
        } else {
            null
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
        )
    )
}

@Preview(showBackground = true, name = "Available Coupons")
@Composable
fun CouponBookListScreenPreview() {
    HombreCamionTheme {
        CouponBookListScreen(
            areCoupons = true,
            coupons = listOf(
                Coupon(
                    fldCode = "CODE1",
                    fldTitle = "Cupón 1",
                    fldDescription = "Descripción del cupón 1",
                    fldDiscountType = 1,
                    fldDiscountValue = "10%",
                    fldStartDate = "2023-01-01",
                    fldEndDate = "2023-12-31",
                    fldStatus = VoucherStatusType.VALIDO
                ),
                Coupon(
                    fldCode = "CODE2",
                    fldTitle = "Cupón 2",
                    fldDescription = "Descripción del cupón 2",
                    fldDiscountType = 0,
                    fldDiscountValue = "$50",
                    fldStartDate = "2023-01-01",
                    fldEndDate = "2023-12-31",
                    fldStatus = VoucherStatusType.VALIDO
                )
            ),
            selectedFilter = CouponFilterOptions.ALL,
            filterOptions = CouponFilterOptions.entries,
            onFilterBy = {},
            onCouponClick = { _ -> }
        )
    }
}

@Preview(showBackground = true, name = "My Coupons")
@Composable
fun MyCouponBookListScreenPreview() {
    HombreCamionTheme {
        CouponBookListScreen(
            areCoupons = false,
            coupons = listOf(
                Coupon(
                    fldCode = "CODE3",
                    fldTitle = "Mi Cupón 1",
                    fldDescription = "Descripción de mi cupón 1",
                    fldDiscountType = 1,
                    fldDiscountValue = "15%",
                    fldStartDate = "2023-01-01",
                    fldEndDate = "2023-12-31",
                    fldStatus = VoucherStatusType.VALIDO
                )
            ),
            selectedFilter = CouponFilterOptions.ALL,
            filterOptions = CouponFilterOptions.entries,
            onFilterBy = {},
            onCouponClick = { _ -> }
        )
    }
}
