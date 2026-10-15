package com.rfz.appflotal.presentation.ui.couponbook.screen.follow

import android.icu.text.RelativeDateTimeFormatter
import android.icu.util.ULocale
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.AppLocale
import com.rfz.appflotal.data.model.promotions.AccessoryVehicleType
import com.rfz.appflotal.data.model.promotions.FollowedAccessory
import com.rfz.appflotal.data.model.promotions.FollowedPromotion
import com.rfz.appflotal.data.model.promotions.StoreDiscount
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.couponbook.screen.promotions.PromotionCard
import java.util.concurrent.TimeUnit

@Composable
fun FollowedAccessoryDetailScreen(
    state: FollowedAccessoryDetailUiState,
    onUnfollow: (FollowedAccessory) -> Unit,
    onRefresh: () -> Unit,
    onPromotionClick: (StoreDiscount) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (state) {
            FollowedAccessoryDetailUiState.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )

            FollowedAccessoryDetailUiState.NotFound -> NotFollowedAnymore(
                onBack = onBack,
                modifier = Modifier.align(Alignment.Center)
            )

            is FollowedAccessoryDetailUiState.Found -> AccessoryPromotions(
                accessory = state.accessory,
                onUnfollow = { onUnfollow(state.accessory) },
                onRefresh = onRefresh,
                onPromotionClick = onPromotionClick
            )
        }
    }
}

@Composable
private fun AccessoryPromotions(
    accessory: FollowedAccessory,
    onUnfollow: () -> Unit,
    onRefresh: () -> Unit,
    onPromotionClick: (StoreDiscount) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Dimens.PaddingMedium),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
    ) {
        item {
            AccessoryHeader(
                accessory = accessory,
                onUnfollow = onUnfollow,
                onRefresh = onRefresh
            )
        }

        if (accessory.promotions.isEmpty()) {
            item {
                Text(
                    text = stringResource(
                        if (accessory.pendingFirstSync) {
                            R.string.buscando_promociones
                        } else {
                            R.string.sin_promociones_accesorio
                        }
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(
                items = accessory.promotions,
                key = { promotion -> promotion.discount.productUrl }
            ) { promotion ->
                PromotionCard(
                    discount = promotion.discount,
                    isNew = promotion.isNew,
                    onClick = { onPromotionClick(promotion.discount) }
                )
            }

            item {
                Text(
                    text = stringResource(R.string.ordenadas_mayor_descuento),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun AccessoryHeader(
    accessory: FollowedAccessory,
    onUnfollow: () -> Unit,
    onRefresh: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)) {
        accessory.description?.let { description ->
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = accessory.lastSyncedAt?.let {
                    stringResource(R.string.actualizado_hace, relativeTime(it))
                } ?: stringResource(R.string.aun_sin_actualizar),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
            TextButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = stringResource(R.string.buscar_promociones_ahora),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        FilledTonalButton(onClick = onUnfollow) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = stringResource(R.string.siguiendo_accesorio),
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        Text(
            text = stringResource(R.string.toca_para_dejar_de_seguir),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun NotFollowedAnymore(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(Dimens.PaddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
    ) {
        Text(
            text = stringResource(R.string.ya_no_sigues_accesorio),
            style = MaterialTheme.typography.bodyLarge
        )
        Button(onClick = onBack) {
            Text(text = stringResource(R.string.volver_a_siguiendo))
        }
    }
}

/**
 * "hace 3 horas" / "3 hours ago" en el idioma de la app. DateUtils usaría el idioma del
 * sistema, que puede ser distinto.
 */
private fun relativeTime(timestamp: Long, now: Long = System.currentTimeMillis()): String {
    val formatter = RelativeDateTimeFormatter.getInstance(
        ULocale.forLocale(AppLocale.currentLocale.value)
    )
    val elapsed = (now - timestamp).coerceAtLeast(0)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsed)
    val hours = TimeUnit.MILLISECONDS.toHours(elapsed)
    val days = TimeUnit.MILLISECONDS.toDays(elapsed)
    return when {
        minutes < 1 -> formatter.format(
            RelativeDateTimeFormatter.Direction.PLAIN,
            RelativeDateTimeFormatter.AbsoluteUnit.NOW
        )

        hours < 1 -> formatter.format(
            minutes.toDouble(),
            RelativeDateTimeFormatter.Direction.LAST,
            RelativeDateTimeFormatter.RelativeUnit.MINUTES
        )

        days < 1 -> formatter.format(
            hours.toDouble(),
            RelativeDateTimeFormatter.Direction.LAST,
            RelativeDateTimeFormatter.RelativeUnit.HOURS
        )

        else -> formatter.format(
            days.toDouble(),
            RelativeDateTimeFormatter.Direction.LAST,
            RelativeDateTimeFormatter.RelativeUnit.DAYS
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FollowedAccessoryDetailScreenPreview() {
    HombreCamionTheme {
        FollowedAccessoryDetailScreen(
            state = FollowedAccessoryDetailUiState.Found(
                FollowedAccessory(
                    id = 1,
                    catalogAccessoryId = 1,
                    vehicleType = AccessoryVehicleType.TRUCK,
                    name = "Dash cam",
                    description = "Graba incidentes durante el recorrido.",
                    pendingFirstSync = false,
                    lastSyncedAt = null,
                    promotions = listOf(
                        FollowedPromotion(
                            discount = StoreDiscount(
                                store = "Amazon",
                                title = "Garmin Dash Cam Mini 2",
                                sku = null,
                                regularPrice = 139.99,
                                finalPrice = 89.99,
                                percentOff = 35.0,
                                currency = "USD",
                                productUrl = "https://example.com/1",
                                imageUrl = null
                            ),
                            isNew = true
                        )
                    )
                )
            ),
            onUnfollow = {},
            onRefresh = {},
            onPromotionClick = {},
            onBack = {}
        )
    }
}
