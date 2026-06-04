package com.rfz.appflotal.presentation.ui.couponbook.screen.redeem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.outlined.Adjust
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rfz.appflotal.core.util.Commons
import com.rfz.appflotal.data.model.couponbook.Coupons
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.couponbook.QrCodeImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RedeemCoupon(coupon: Coupons, modifier: Modifier = Modifier, onBack: () -> Unit) {
    Column {
        CenterAlignedTopAppBar(
            title = {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    border = borderStroke()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Adjust,
                            contentDescription = null,
                            tint = Color(0xFF3F7EE8),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Llantera Norte",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1C1E)
                            )
                        )
                    }
                }
            },
            navigationIcon = {
                Box {
                    IconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.iconButtonColors(MaterialTheme.colorScheme.onTertiary),
                        modifier = Modifier.clip(RoundedCornerShape(Dimens.PaddingSmall))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                }
            }
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF2F8FF))
                .padding(Dimens.ScreenHorizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.SectionSpacing)
        ) {
            // Main QR Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.CardPaddingLarge),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimens.ComponentSpacing)
                ) {
                    // Vigente Badge
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondary
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Circle,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(8.dp)
                            )
                            Text(
                                text = "VIGENTE",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                    }

                    // QR Placeholder
                    Surface(
                        modifier = Modifier
                            .size(240.dp)
                            .padding(8.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFFF2F8FF)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            QrCodeImage(
                                content = coupon.fldCode,
                                modifier = Modifier.size(260.dp)
                            )
                        }
                    }

                    // Folio Code
                    Text(
                        text = coupon.fldCode,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = Color(0xFF1A1C1E)
                        )
                    )
                }
            }

            // Promo Details
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.ElementSpacing)
            ) {
                Text(
                    text = coupon.fldTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E)
                    ),
                    textAlign = TextAlign.Center
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = Color(0xFF6F7785),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Vence el ${Commons.formatToLongDate(coupon.fldEndDate)}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF6F7785)
                        )
                    )
                }
            }

            // Brightness Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.secondary
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LightMode,
                        contentDescription = null,
                        tint = Color(0xFF3F7EE8),
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "Brillo al máximo automáticamente",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        )
                        Text(
                            text = "para facilitar el escaneo del QR",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer
            Text(
                text = "Folio válido para un solo uso",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(bottom = Dimens.PaddingLarge)
            )
        }
    }

}

@Composable
private fun borderStroke() = BorderStroke(
    width = 1.dp,
    color = Color(0xFFD0E4FF)
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RedeemCouponPreview() {
    HombreCamionTheme {
        RedeemCoupon(
            coupon = Coupons(
                fldCode = "KFKFKFKF-3KK",
                fldTitle = "2×1 en alineación y balanceo",
                fldDescription = "",
                fldDiscountType = 1,
                fldDiscountValue = "",
                fldStartDate = "2024-05-01",
                fldEndDate = "2026-05-28"
            ),
            onBack = {}
        )
    }
}
