package com.rfz.appflotal.presentation.ui.couponbook.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.FireTruck
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.Commons
import com.rfz.appflotal.data.model.couponbook.Coupon
import com.rfz.appflotal.data.model.couponbook.VoucherDiscountType
import com.rfz.appflotal.data.model.couponbook.VoucherDiscountType.DINERO
import com.rfz.appflotal.data.model.couponbook.VoucherDiscountType.PORCENTAJE
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
                showRetryButton = true,
                onRetry = onLoadData
            )
        }

        LoadState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is LoadState.Success -> {
            CouponBookScreen(
                nearbyCoupons = nearbyCoupons,
                myCoupons = myCoupons,
                onSeeAllCoupons = onSeeAllCoupons,
                onSeeAllVouchers = onSeeAllVouchers,
                onCouponClick = onCouponClick,
                onVoucherClick = onVoucherClick,
                modifier = modifier
            )
        }

        else -> Unit
    }
}

@Composable
fun CouponBookScreen(
    nearbyCoupons: List<Coupon>,
    myCoupons: List<Coupon>,
    onSeeAllCoupons: () -> Unit,
    onSeeAllVouchers: () -> Unit,
    onCouponClick: (String) -> Unit,
    onVoucherClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            horizontal = Dimens.PaddingMedium,
            vertical = Dimens.PaddingMedium
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
    ) {
        item {
            CouponBookHeader()
        }

        item {
            SectionHeader(
                title = stringResource(R.string.cupones_cercanos),
                onSeeAllClick = onSeeAllCoupons
            )
        }

        item {
            if (nearbyCoupons.isEmpty()) {
                EmptyCouponCard(
                    message = stringResource(R.string.no_hay_cupones_cercanos)
                )
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
                    contentPadding = PaddingValues(end = Dimens.PaddingMedium)
                ) {
                    items(
                        items = nearbyCoupons,
                        key = { coupon -> coupon.fldCode }
                    ) { coupon ->
                        CuponCard(
                            coupon = coupon,
                            onClick = { onCouponClick(coupon.fldCode) }
                        )
                    }
                }
            }
        }

        item {
            SectionHeader(
                title = stringResource(R.string.mis_cupones),
                onSeeAllClick = onSeeAllVouchers
            )
        }

        if (myCoupons.isEmpty()) {
            item {
                EmptyCouponCard(
                    message = stringResource(R.string.no_hay_cupones_guardados)
                )
            }
        } else {
            items(
                items = myCoupons,
                key = { coupon -> coupon.fldCode }
            ) { coupon ->
                NearestCuponCard(
                    coupon = coupon,
                    onClick = { onVoucherClick(coupon.fldCode) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(Dimens.PaddingSmall))
        }
    }
}

@Composable
private fun CouponBookHeader(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier.padding(Dimens.PaddingLarge),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.FireTruck,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.cuponera_titulo),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = stringResource(R.string.cuponera_subtitulo),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        TextButton(onClick = onSeeAllClick) {
            Text(
                text = stringResource(R.string.ver_todos),
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowRight,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun CuponCard(
    coupon: Coupon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val discount = when (coupon.fldDiscountType) {
        PORCENTAJE -> "${coupon.fldDiscountValue}%"
        DINERO -> "$${coupon.fldDiscountValue}"
    }

    ElevatedCard(
        modifier = modifier
            .width(272.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimens.PaddingSmall),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FireTruck,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )

                DiscountBadge(
                    discount = discount.ifBlank {
                        stringResource(R.string.promo_label)
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                )
            }

            Column(
                modifier = Modifier.padding(
                    start = 4.dp,
                    end = 4.dp,
                    bottom = 6.dp
                ),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = stringResource(R.string.llantera_norte_placeholder),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = coupon.fldTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = stringResource(
                        R.string.coupon_expires,
                        Commons.formatToLongDate(coupon.fldEndDate)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun DiscountBadge(
    discount: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
    ) {
        Text(
            text = discount,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun NearestCuponCard(
    coupon: Coupon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isValid = coupon.fldStatus == VoucherStatusType.VALIDO

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (isValid) 2.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.PaddingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
        ) {
            Surface(
                modifier = Modifier.size(58.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (isValid) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.FireTruck,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = if (isValid) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                VoucherStatusBadge(status = coupon.fldStatus)

                Text(
                    text = stringResource(R.string.llantera_norte_placeholder),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = coupon.fldTitle,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = stringResource(
                        R.string.coupon_expires,
                        Commons.formatToLongDate(coupon.fldEndDate)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isValid) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun VoucherStatusBadge(
    status: VoucherStatusType,
    modifier: Modifier = Modifier
) {
    val label = when (status) {
        VoucherStatusType.VALIDO -> stringResource(R.string.disponible)
        VoucherStatusType.RECLAMADO -> stringResource(R.string.reclamado)
        VoucherStatusType.INACTIVO -> stringResource(R.string.inactivo)
        VoucherStatusType.NO_VALIDO -> stringResource(R.string.no_valido)
        VoucherStatusType.EXPIRADO -> stringResource(R.string.expirado)
    }

    val containerColor = when (status) {
        VoucherStatusType.VALIDO ->
            MaterialTheme.colorScheme.primaryContainer

        VoucherStatusType.RECLAMADO ->
            MaterialTheme.colorScheme.secondaryContainer

        VoucherStatusType.INACTIVO,
        VoucherStatusType.EXPIRADO ->
            MaterialTheme.colorScheme.surfaceVariant

        VoucherStatusType.NO_VALIDO ->
            MaterialTheme.colorScheme.errorContainer
    }

    val contentColor = when (status) {
        VoucherStatusType.VALIDO ->
            MaterialTheme.colorScheme.onPrimaryContainer

        VoucherStatusType.RECLAMADO ->
            MaterialTheme.colorScheme.onSecondaryContainer

        VoucherStatusType.INACTIVO,
        VoucherStatusType.EXPIRADO ->
            MaterialTheme.colorScheme.onSurfaceVariant

        VoucherStatusType.NO_VALIDO ->
            MaterialTheme.colorScheme.onErrorContainer
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = containerColor,
        contentColor = contentColor
    ) {
        Text(
            text = label.uppercase(),
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp
            ),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EmptyCouponCard(
    message: String,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.PaddingLarge),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.FireTruck,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
            onCouponClick = { _ -> },
            onVoucherClick = { _ -> },
            onSeeAllVouchers = {},
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
                fldDiscountType = VoucherDiscountType.PORCENTAJE,
                fldDiscountValue = "10%",
                fldStartDate = "2023-01-01",
                fldEndDate = "2023-12-31",
                fldStatus = VoucherStatusType.INACTIVO
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CuponCardPreview() {
    HombreCamionTheme {
        CuponCard(
            coupon = Coupon(
                fldCode = "CODE123",
                fldTitle = "2x1 en Alineación",
                fldDescription = "Obtén un 2x1 en el servicio de alineación y balanceo.",
                fldDiscountType = VoucherDiscountType.PORCENTAJE,
                fldDiscountValue = "50",
                fldStartDate = "2024-01-01",
                fldEndDate = "2024-12-31",
                fldStatus = VoucherStatusType.VALIDO
            ),
            onClick = {}
        )
    }
}