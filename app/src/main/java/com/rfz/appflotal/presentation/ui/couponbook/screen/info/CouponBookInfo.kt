package com.rfz.appflotal.presentation.ui.couponbook.screen.info

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FireTruck
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.Commons
import com.rfz.appflotal.data.model.couponbook.Coupon
import com.rfz.appflotal.data.model.couponbook.VoucherStatusType
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.components.LoadingDialog
import com.rfz.appflotal.presentation.ui.utils.LoadState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CouponBookInfo(
    gettingCouponState: LoadState<Unit>,
    coupon: Coupon,
    onBack: () -> Unit,
    onGettingVoucher: (code: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isLoading = gettingCouponState is LoadState.Loading
    val isValid = coupon.fldStatus == VoucherStatusType.VALIDO
    val text = stringResource(R.string.cupon_adquirido)

    LaunchedEffect(gettingCouponState) {
        when (gettingCouponState) {
            is LoadState.Error -> {
                Toast.makeText(
                    context,
                    gettingCouponState.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            is LoadState.Success -> {
                Toast.makeText(
                    context,
                    text,
                    Toast.LENGTH_SHORT
                ).show()

                onBack()
            }

            else -> Unit
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.detalle_del_cupon),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.regresar)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            if (isValid) {
                CouponActionBar(
                    isLoading = isLoading,
                    onClick = {
                        onGettingVoucher(coupon.fldCode)
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            CouponHero(
                coupon = coupon
            )

            Column(
                modifier = Modifier.padding(Dimens.PaddingMedium),
                verticalArrangement = Arrangement.spacedBy(Dimens.PaddingLarge)
            ) {
                CouponMainInformation(
                    coupon = coupon
                )

                CouponValidityCard(
                    endDate = coupon.fldEndDate
                )

                CouponTermsCard()
            }
        }
    }

    if (isLoading) {
        LoadingDialog()
    }
}

@Composable
private fun CouponHero(
    coupon: Coupon,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.FireTruck,
            contentDescription = null,
            modifier = Modifier.size(92.dp),
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )

        CouponDiscountBadge(
            discount = coupon.fldDiscountValue.ifBlank {
                stringResource(R.string.promo_label)
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(Dimens.PaddingMedium)
        )
    }
}

@Composable
private fun CouponMainInformation(
    coupon: Coupon,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CouponStatusBadge(
            status = coupon.fldStatus
        )

        Text(
            text = stringResource(
                R.string.llantera_norte_placeholder
            ).uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = coupon.fldTitle,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = coupon.fldDescription,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 24.sp
        )
    }
}

@Composable
private fun CouponValidityCard(
    endDate: String,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
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
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = stringResource(R.string.vigencia).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = stringResource(
                        R.string.hasta_fecha,
                        Commons.formatToLongDate(endDate)
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun CouponTermsCard(
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
        ) {
            Text(
                text = stringResource(R.string.terminos_y_condiciones_label),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant
            )

            CouponBulletPoint(
                text = stringResource(R.string.no_acumulable_promociones)
            )
        }
    }
}

@Composable
private fun CouponBulletPoint(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(7.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
        )

        Text(
            text = text,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CouponActionBar(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp
    ) {
        Button(
            onClick = onClick,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(Dimens.PaddingMedium)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = stringResource(R.string.obtener_cupon),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CouponDiscountBadge(
    discount: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        shadowElevation = 2.dp
    ) {
        Text(
            text = discount,
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 8.dp
            ),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CouponStatusBadge(
    status: VoucherStatusType,
    modifier: Modifier = Modifier
) {
    val textResource = when (status) {
        VoucherStatusType.VALIDO -> R.string.disponible
        VoucherStatusType.RECLAMADO -> R.string.reclamado
        VoucherStatusType.INACTIVO -> R.string.inactivo
        VoucherStatusType.NO_VALIDO -> R.string.no_valido
        VoucherStatusType.EXPIRADO -> R.string.expirado
    }

    val containerColor = when (status) {
        VoucherStatusType.VALIDO ->
            MaterialTheme.colorScheme.primaryContainer

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
            MaterialTheme.colorScheme.onPrimaryContainer

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
        shape = RoundedCornerShape(50),
        color = containerColor,
        contentColor = contentColor
    ) {
        Text(
            text = stringResource(textResource).uppercase(),
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CouponBookInfoPreview() {
    HombreCamionTheme {
        CouponBookInfo(
            coupon = Coupon(
                fldCode = "KFKFKFKF-3KK",
                fldTitle = "2×1 en alineación y balanceo",
                fldDescription = "Obtén un 2x1 en el servicio de alineación y balanceo para tu vehículo. Válido en todas nuestras sucursales.",
                fldDiscountType = 1,
                fldDiscountValue = "50%",
                fldStartDate = "2024-05-01",
                fldEndDate = "2024-05-28",
                fldStatus = VoucherStatusType.VALIDO
            ),
            onBack = {},
            onGettingVoucher = {},
            gettingCouponState = LoadState.Idle
        )
    }
}
