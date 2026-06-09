package com.rfz.appflotal.presentation.ui.couponbook.screen.main

import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rfz.appflotal.data.model.couponbook.Coupon
import com.rfz.appflotal.data.model.couponbook.ValidatedVoucher
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
    val context = LocalContext.current

    LaunchedEffect(validateState) {
        if (!areCoupons && validateState is LoadState.Error) {
            Toast.makeText(context, validateState.message, Toast.LENGTH_SHORT).show()
            onResetValidateState()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CouponBookListScreen(
            coupons = coupons,
            selectedFilter = selectedFilter,
            filterOptions = filterOptions,
            onFilterBy = onFilterBy,
            onCouponClick = { code ->
                if (areCoupons) onGettingVoucher(code)
                else onCouponClick(code)
            },
            modifier = modifier
        )

        if (!areCoupons) {
            when (validateState) {
                LoadState.Loading -> {
                    LoadingDialog()
                }

                else -> Unit
            }
        }
    }
}

@Composable
fun CouponBookListScreen(
    coupons: List<Coupon>,
    selectedFilter: CouponFilterOptions,
    filterOptions: List<CouponFilterOptions>,
    onFilterBy: (CouponFilterOptions) -> Unit,
    onCouponClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.padding(
            horizontal = Dimens.PaddingMedium,
            vertical = Dimens.PaddingSmall
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingExtraSmall)
        ) {
            filterOptions.forEach { option ->
                CouponChip(
                    text = stringResource(option.text),
                    selected = option == selectedFilter,
                    onChangeValue = { onFilterBy(option) },
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
        ) {
            items(coupons.size) { index ->
                NearestCuponCard(
                    coupon = coupons[index],
                    onClick = { onCouponClick(coupons[index].fldCode) }
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
        shape = RoundedCornerShape(Dimens.PaddingLarge),
        onClick = { onChangeValue() },
        label = {
            Text(text)
        },
        selected = selected,
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CouponBookListScreenPreview() {
    HombreCamionTheme {
        CouponBookListScreen(
            coupons = emptyList(),
            filterOptions = listOf(CouponFilterOptions.ALL, CouponFilterOptions.VALID),
            onFilterBy = {},
            selectedFilter = CouponFilterOptions.VALID,
            modifier = Modifier.safeContentPadding(),
            onCouponClick = {},
        )
    }
}
