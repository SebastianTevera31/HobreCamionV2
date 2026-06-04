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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.core.util.Commons
import com.rfz.appflotal.data.model.couponbook.Coupons
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun CouponBookInfo(
    coupons: Coupons,
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

//                IconButton(
//                    onClick = {},
//                    colors = IconButtonDefaults.iconButtonColors(MaterialTheme.colorScheme.onTertiary),
//                    modifier = Modifier.clip(RoundedCornerShape(Dimens.PaddingSmall))
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Share,
//                        contentDescription = null,
//                        tint = Color.Black
//                    )
//                }
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
                    Text(
                        text = "LLANTERA NORTE",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = coupons.fldTitle,
                        color = Color.Black,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Text(
                    text = coupons.fldDescription,
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
                            Text(text = "VIGENCIA", style = MaterialTheme.typography.bodySmall)
                            Text(
                                text = "Hasta ${Commons.formatToLongDate(coupons.fldEndDate)}",
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
                        text = "Terminos y conidiciones",
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
                            text = "No acumulable con otras promociones.",
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Button(
                onClick = {
                    onGettingCoupon(coupons.fldCode)
                }, modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(text = "Obtener cupon")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CouponBookInfoPreview() {
    HombreCamionTheme {
        CouponBookInfo(
            coupons = Coupons(
                fldCode = "KFKFKFKF-3KK",
                fldTitle = "2×1 en alineación y balanceo",
                fldDescription = "Obtén un 2x1 en el servicio de alineación y balanceo para tu vehículo. Válido en todas nuestras sucursales.",
                fldDiscountType = 1,
                fldDiscountValue = "50%",
                fldStartDate = "2024-05-01",
                fldEndDate = "2024-05-28",
                fldStatus = 5
            ),
            onBack = {},
            onGettingCoupon = {}
        )
    }
}
