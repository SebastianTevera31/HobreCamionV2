package com.rfz.appflotal.presentation.ui.couponbook.screen.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.FireTruck
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.Commons
import com.rfz.appflotal.data.model.couponbook.Coupon
import com.rfz.appflotal.data.model.couponbook.VoucherStatusType
import com.rfz.appflotal.presentation.commons.ErrorView
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.utils.LoadState

@Composable
fun CouponBookRoute(
    screenStatus: LoadState<Unit>,
    onLoadData: () -> Unit,
    nearbyCoupons: List<Coupon>,
    myCoupons: List<Coupon>,
    onSeeAllCoupons: () -> Unit,
    onSeeAllVouchers: () -> Unit,
    onCouponClick: (String) -> Unit,
    onVoucherClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (screenStatus) {
        is LoadState.Error -> {
            ErrorView(
                errorMessage = screenStatus.message,
                showRetryButton = true
            ) {
                onLoadData()
            }
        }

        LoadState.Loading -> {
            Box(modifier = modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        is LoadState.Success -> {
            CouponBookScreen(
                nearbyCoupons = nearbyCoupons,
                myCoupons = myCoupons,
                onSeeAllCoupons = onSeeAllCoupons,
                onCouponClick = onCouponClick,
                onVoucherClick = onVoucherClick,
                onSeeAlVoucher = onSeeAllVouchers,
                modifier = modifier
            )
        }

        else -> {}
    }
}

@Composable
fun CouponBookScreen(
    nearbyCoupons: List<Coupon>,
    myCoupons: List<Coupon>,
    onSeeAllCoupons: () -> Unit,
    onSeeAlVoucher: () -> Unit,
    onCouponClick: (String) -> Unit,
    onVoucherClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.PaddingMedium)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.cupones_cercanos),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            TextButton(onClick = onSeeAllCoupons) {
                Text(
                    text = stringResource(R.string.ver_todos),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        if (nearbyCoupons.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(
                    text = stringResource(R.string.no_hay_cupones_cercanos),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
            ) {
                items(nearbyCoupons.size) { index ->
                    CuponCard(
                        coupon = nearbyCoupons[index],
                        onClick = { onCouponClick(nearbyCoupons[index].fldCode) }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.mis_cupones),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )

            TextButton(onClick = onSeeAlVoucher) {
                Text(
                    text = stringResource(R.string.ver_todos),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }


        if (myCoupons.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(
                    text = stringResource(R.string.no_hay_cupones_cercanos),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp),
                verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
            ) {
                items(myCoupons.size) { index ->
                    NearestCuponCard(
                        coupon = myCoupons[index],
                        onClick = { onVoucherClick(myCoupons[index].fldCode) }
                    )
                }
            }
        }
    }
}

@Composable
fun NearestCuponCard(
    coupon: Coupon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(Dimens.PaddingLarge),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.PaddingMedium)
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(Dimens.PaddingSmall))
                        .background(MaterialTheme.colorScheme.onSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Image(imageVector = Icons.Default.FireTruck, contentDescription = null)
                }

                Column {
                    if (coupon.fldStatus != VoucherStatusType.VALIDO) {
                        Card(
                            modifier = Modifier.padding(vertical = Dimens.PaddingSmall),
                            border = CardDefaults.outlinedCardBorder(enabled = true)
                        ) {
                            val textRes = when (coupon.fldStatus) {
                                VoucherStatusType.RECLAMADO -> R.string.reclamado

                                VoucherStatusType.INACTIVO -> R.string.inactivo

                                VoucherStatusType.NO_VALIDO -> R.string.no_valido

                                VoucherStatusType.EXPIRADO -> R.string.expirado

                                else -> R.string.expirado
                            }

                            if (coupon.fldStatus != VoucherStatusType.VALIDO) {
                                Text(
                                    text = stringResource(textRes),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(Dimens.PaddingSmall)
                                )
                            }
                        }
                    }

                    Text(
                        text = stringResource(R.string.llantera_norte_placeholder),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = coupon.fldTitle,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = stringResource(
                            R.string.coupon_expires,
                            Commons.formatToLongDate(coupon.fldEndDate)
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryFixedVariant
                )
            }
        }
    }
}

@Composable
fun CuponCard(
    coupon: Coupon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(240.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(Dimens.PaddingLarge),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.PaddingMedium)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(Dimens.PaddingLarge))
                    .background(MaterialTheme.colorScheme.onSecondary),
                contentAlignment = Alignment.Center
            ) {
                Image(imageVector = Icons.Default.FireTruck, contentDescription = null)
            }

            SuggestionChip(
                onClick = {},
                label = {
                    Text(
                        text = coupon.fldDiscountValue.takeIf { it.isNotEmpty() } ?: stringResource(
                            R.string.promo_label
                        ),
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                },
                shape = RoundedCornerShape(Dimens.PaddingExtraLarge),
                colors = SuggestionChipDefaults.suggestionChipColors(MaterialTheme.colorScheme.tertiaryContainer)
            )

            Text(
                text = stringResource(R.string.llantera_norte_placeholder),
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = coupon.fldTitle,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CouponBookScreenPreview() {
    HombreCamionTheme {
        CouponBookScreen(
            nearbyCoupons = emptyList(),
            myCoupons = emptyList(),
            onSeeAllCoupons = {},
            onCouponClick = {},
            onVoucherClick = {},
            onSeeAlVoucher = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NearestCuponCardPreview() {
    HombreCamionTheme {
        NearestCuponCard(
            coupon = Coupon(
                fldCode = "CODE123",
                fldTitle = "Cupón de Descuento",
                fldDescription = "Descripción del cupón",
                fldDiscountType = 1,
                fldDiscountValue = "10%",
                fldStartDate = "2023-01-01",
                fldEndDate = "2023-12-31",
                fldStatus = VoucherStatusType.INACTIVO
            ),
            onClick = {}
        )
    }
}