package com.rfz.appflotal.presentation.ui.couponbook.screen.main

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.couponbook.CouponFilterOptions

@Composable
fun CouponBookListRoute(
    selectedFilter: CouponFilterOptions,
    filterOptions: List<CouponFilterOptions>,
    onFilterBy: (CouponFilterOptions) -> Unit,
    onCouponClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    CouponBookListScreen(
        selectedFilter = selectedFilter,
        filterOptions = filterOptions,
        onFilterBy = onFilterBy,
        onCouponClick = onCouponClick,
        modifier = modifier
    )
}

@Composable
fun CouponBookListScreen(
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

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            item {
                NearestCuponCard(onClick = { onCouponClick("1") })
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
            filterOptions = listOf(CouponFilterOptions.ALL, CouponFilterOptions.VALID),
            onFilterBy = {},
            onCouponClick = {},
            selectedFilter = CouponFilterOptions.VALID,
            modifier = Modifier.safeContentPadding()
        )
    }
}