package com.rfz.appflotal.presentation.ui.couponbook.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.FireTruck
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun CouponBookRoute(modifier: Modifier = Modifier) {
    CouponBookScreen(modifier)
}

@Composable
fun CouponBookScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        LazyRow(verticalAlignment = Alignment.CenterVertically) {
            item {
                NearestCuponCard()
            }
        }
    }
}

@Composable
fun NearestCuponCard(modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
                .fillMaxWidth()
                .padding(Dimens.PaddingMedium)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(Dimens.PaddingSmall))
                        .background(MaterialTheme.colorScheme.tertiary),
                    contentAlignment = Alignment.Center
                ) {
                    Image(imageVector = Icons.Default.FireTruck, contentDescription = null)
                }

                Column {
                    Text(
                        text = "Llantera Norte",
                        color = Color.DarkGray,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "2x1 alineacion + balance",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(onClick = {}) {
                Image(
                    imageVector = Icons.AutoMirrored.Default.ArrowRight,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun CuponCard(modifier: Modifier = Modifier) {
    Card(modifier = modifier.width(320.dp)) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(Dimens.PaddingMedium)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(Dimens.PaddingSmall))
                    .background(MaterialTheme.colorScheme.tertiary),
                contentAlignment = Alignment.Center
            ) {
                Image(imageVector = Icons.Default.FireTruck, contentDescription = null)
            }

            SuggestionChip(
                onClick = {},
                label = {
                    Text(
                        text = "-50%",
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                },
                colors = SuggestionChipDefaults.suggestionChipColors(MaterialTheme.colorScheme.tertiaryContainer),
                modifier = Modifier.clip(RoundedCornerShape(Dimens.PaddingExtraLarge))
            )

            Text(
                text = "Llantera Norte",
                color = Color.DarkGray,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "2x1 alineacion + balance",
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NearestCuponCardPreview() {
    HombreCamionTheme {
        NearestCuponCard()
    }
}

@Preview(showBackground = true)
@Composable
fun CuponCardPreview() {
    HombreCamionTheme {
        CuponCard()
    }
}