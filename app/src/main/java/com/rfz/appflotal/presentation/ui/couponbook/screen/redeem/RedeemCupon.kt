package com.rfz.appflotal.presentation.ui.couponbook.screen.redeem

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.outlined.Adjust
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.Commons
import com.rfz.appflotal.data.model.couponbook.Coupon
import com.rfz.appflotal.data.model.couponbook.VoucherDiscountType
import com.rfz.appflotal.data.model.couponbook.VoucherDiscountType.DINERO
import com.rfz.appflotal.data.model.couponbook.VoucherDiscountType.PORCENTAJE
import com.rfz.appflotal.data.model.couponbook.VoucherStatusType
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.couponbook.QrCodeImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RedeemCoupon(
    coupon: Coupon,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isValid = coupon.fldStatus == VoucherStatusType.VALIDO

    MaximizeBrightnessWhileVisible(
        enabled = isValid
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    MerchantBadge()
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(
                                R.string.regresar
                            ),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = Color.Unspecified,
                    navigationIconContentColor = Color.Unspecified,
                    titleContentColor = Color.Unspecified,
                    actionIconContentColor = Color.Unspecified
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(
                        alpha = 0.30f
                    )
                )
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = Dimens.ScreenHorizontalPadding,
                    vertical = Dimens.PaddingMedium
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                Dimens.SectionSpacing
            )
        ) {
            RedeemQrCard(
                coupon = coupon,
                isValid = isValid
            )

            CouponRedeemDetails(
                coupon = coupon
            )

            if (isValid) {
                BrightnessInformationCard()
            }

            Text(
                text = if (isValid) {
                    stringResource(R.string.folio_valido_un_solo_uso)
                } else {
                    stringResource(R.string.cupon_no_disponible_para_canje)
                },
                modifier = Modifier.padding(
                    vertical = Dimens.PaddingMedium
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MerchantBadge(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Adjust,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = stringResource(
                    R.string.llantera_norte_placeholder
                ),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun RedeemQrCard(
    coupon: Coupon,
    isValid: Boolean,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.CardPaddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                Dimens.ComponentSpacing
            )
        ) {
            CouponRedeemStatusBadge(
                status = coupon.fldStatus
            )

            if (isValid) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.82f)
                            .widthIn(max = 260.dp)
                            .aspectRatio(1f),
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White,
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        QrCodeImage(
                            content = coupon.fldCode,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.folio).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = coupon.fldCode,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 1.5.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                UnavailableVoucherState()
            }
        }
    }
}

@Composable
private fun CouponRedeemStatusBadge(
    status: VoucherStatusType,
    modifier: Modifier = Modifier
) {
    val textResource = when (status) {
        VoucherStatusType.VALIDO -> R.string.vigente
        VoucherStatusType.RECLAMADO -> R.string.reclamado
        VoucherStatusType.INACTIVO -> R.string.inactivo
        VoucherStatusType.NO_VALIDO -> R.string.no_valido
        VoucherStatusType.EXPIRADO -> R.string.expirado
    }

    val containerColor = when (status) {
        VoucherStatusType.VALIDO ->
            MaterialTheme.colorScheme.tertiaryContainer

        VoucherStatusType.RECLAMADO ->
            MaterialTheme.colorScheme.secondaryContainer

        VoucherStatusType.NO_VALIDO ->
            MaterialTheme.colorScheme.errorContainer

        VoucherStatusType.INACTIVO,
        VoucherStatusType.EXPIRADO ->
            MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = when (status) {
        VoucherStatusType.VALIDO ->
            MaterialTheme.colorScheme.onTertiaryContainer

        VoucherStatusType.RECLAMADO ->
            MaterialTheme.colorScheme.onSecondaryContainer

        VoucherStatusType.NO_VALIDO ->
            MaterialTheme.colorScheme.onErrorContainer

        VoucherStatusType.INACTIVO,
        VoucherStatusType.EXPIRADO ->
            MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = containerColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 6.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Circle,
                contentDescription = null,
                modifier = Modifier.size(7.dp),
                tint = contentColor
            )

            Text(
                text = stringResource(textResource).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun UnavailableVoucherState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(
            vertical = Dimens.PaddingLarge
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            Dimens.PaddingSmall
        )
    ) {
        Surface(
            modifier = Modifier.size(72.dp),
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.QrCode2,
                    contentDescription = null,
                    modifier = Modifier.size(38.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text(
            text = stringResource(
                R.string.cupon_no_disponible
            ),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(
                R.string.cupon_no_disponible_descripcion
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CouponRedeemDetails(
    coupon: Coupon,
    modifier: Modifier = Modifier
) {
    val discount = when (coupon.fldDiscountType) {
        PORCENTAJE -> "${coupon.fldDiscountValue}%"
        DINERO -> "$${coupon.fldDiscountValue}"
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            Dimens.ElementSpacing
        ),
    ) {
        OutlinedCard(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.PaddingMedium),
                verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = coupon.fldTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Descuento de $discount",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = coupon.fldDescription,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 24.sp
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                Dimens.PaddingSmall
            )
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = stringResource(
                    R.string.vence_el,
                    Commons.formatToLongDate(coupon.fldEndDate)
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BrightnessInformationCard(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(Dimens.PaddingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                Dimens.PaddingMedium
            )
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LightMode,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = stringResource(
                        R.string.brillo_maximo_automatico
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(
                        R.string.brillo_maximo_descripcion
                    ),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun MaximizeBrightnessWhileVisible(
    enabled: Boolean
) {
    val context = LocalContext.current
    val activity = remember(context) {
        context.findActivity()
    }

    val view = LocalView.current

    DisposableEffect(activity, view, enabled) {
        if (!enabled) {
            return@DisposableEffect onDispose {}
        }

        val previousKeepScreenOn = view.keepScreenOn
        val window = activity?.window
        val previousBrightness = window?.attributes?.screenBrightness

        view.keepScreenOn = true

        if (window != null) {
            val attributes = window.attributes
            attributes.screenBrightness =
                WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
            window.attributes = attributes
        }

        onDispose {
            view.keepScreenOn = previousKeepScreenOn

            if (window != null && previousBrightness != null) {
                val attributes = window.attributes
                attributes.screenBrightness = previousBrightness
                window.attributes = attributes
            }
        }
    }
}

private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RedeemCouponPreview() {
    HombreCamionTheme {
        RedeemCoupon(
            coupon = Coupon(
                fldCode = "KFKFKFKF-3KK",
                fldTitle = "2×1 en alineación y balanceo",
                fldDescription = "Your private life make me leave me out",
                fldDiscountType = VoucherDiscountType.PORCENTAJE,
                fldDiscountValue = "50",
                fldStartDate = "2024-05-01",
                fldEndDate = "2026-05-28",
                fldStatus = VoucherStatusType.VALIDO
            ),
            onBack = {}
        )
    }
}
