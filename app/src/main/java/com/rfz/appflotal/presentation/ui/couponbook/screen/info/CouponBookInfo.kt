package com.rfz.appflotal.presentation.ui.couponbook.screen.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FireTruck
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.Commons
import com.rfz.appflotal.data.model.couponbook.Coupon
import com.rfz.appflotal.data.model.couponbook.VoucherStatusType
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun CouponBookInfo(
    coupon: Coupon,
    onBack: () -> Unit,
    onGettingCoupon: (code: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(MaterialTheme.colorScheme.tertiary),
                contentAlignment = Alignment.Center
            ) {
                Image(imageVector = Icons.Default.FireTruck, contentDescription = null)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.PaddingSmall),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = Dimens.PaddingMedium,
                    vertical = Dimens.PaddingSmall
                ),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    Dimens.PaddingMedium
                )
            ) {
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

                            Text(
                                text = stringResource(textRes),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(Dimens.PaddingSmall)
                            )
                        }
                    }

                    Text(
                        text = stringResource(R.string.llantera_norte_placeholder).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = coupon.fldTitle,
                        color = Color.Black,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Text(
                    text = coupon.fldDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        Dimens.PaddingSmall
                    )
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.tertiaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(Dimens.PaddingMedium)) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.padding(Dimens.PaddingSmall))
                            Text(text = stringResource(R.string.vigencia), style = MaterialTheme.typography.bodySmall)
                            Text(
                                text = stringResource(R.string.hasta_fecha, Commons.formatToLongDate(coupon.fldEndDate)),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
//                    Card(
//                        modifier = Modifier.weight(1f),
//                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.tertiaryContainer)
//                    ) {
//                        Column(modifier = Modifier.padding(Dimens.PaddingMedium)) {
//                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
//                            Spacer(modifier = Modifier.padding(Dimens.PaddingSmall))
//                            Text(text = "UBICACION", style = MaterialTheme.typography.bodySmall)
//                            Text(text = "3 surcusales", fontWeight = FontWeight.Bold)
//                        }
//                    }
                }

                Column {
                    Text(
                        text = stringResource(R.string.terminos_y_condiciones_label),
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                        )
                        Text(
                            text = stringResource(R.string.no_acumulable_promociones),
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            if (coupon.fldStatus == VoucherStatusType.VALIDO) {
                Button(
                    onClick = {
                        onGettingCoupon(coupon.fldCode)
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Text(text = stringResource(R.string.obtener_cupon))
                }
            }
        }
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
                fldStatus = VoucherStatusType.EXPIRADO
            ),
            onBack = {},
            onGettingCoupon = {}
        )
    }
}
