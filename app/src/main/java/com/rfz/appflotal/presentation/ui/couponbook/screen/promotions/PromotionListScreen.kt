package com.rfz.appflotal.presentation.ui.couponbook.screen.promotions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.promotions.StoreDiscount
import com.rfz.appflotal.presentation.commons.ErrorView
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.ui.utils.LoadState

@Composable
fun PromotionListRoute(
    promotionsState: LoadState<Unit>,
    promotions: List<StoreDiscount>,
    currentPage: Int,
    hasNextPage: Boolean,
    onPromotionClick: (String) -> Unit,
    onPageSelected: (Int) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (promotionsState) {
        is LoadState.Error -> {
            ErrorView(
                errorMessage = promotionsState.message,
                showRetryButton = true,
                onRetry = onRetry
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
            PromotionListScreen(
                promotions = promotions,
                currentPage = currentPage,
                hasNextPage = hasNextPage,
                onPromotionClick = onPromotionClick,
                onPageSelected = onPageSelected,
                modifier = modifier
            )
        }

        else -> Unit
    }
}

@Composable
fun PromotionListScreen(
    promotions: List<StoreDiscount>,
    currentPage: Int,
    hasNextPage: Boolean,
    onPromotionClick: (String) -> Unit,
    onPageSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (promotions.isEmpty()) {
            PromotionListEmptyState(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(Dimens.PaddingMedium)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    horizontal = Dimens.PaddingMedium,
                    vertical = Dimens.PaddingMedium
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
            ) {
                items(
                    items = promotions,
                    key = { discount -> discount.productUrl }
                ) { discount ->
                    PromotionCard(
                        discount = discount,
                        onClick = { onPromotionClick(discount.productUrl) }
                    )
                }
            }
        }

        PromotionsPageNavigator(
            hasData = promotions.isNotEmpty(),
            currentPage = currentPage,
            hasNextPage = hasNextPage,
            onPageSelected = onPageSelected
        )
    }
}

@Composable
private fun PromotionsPageNavigator(
    hasData: Boolean,
    currentPage: Int,
    hasNextPage: Boolean,
    onPageSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Sin total de páginas confiable en la UI: solo mostramos la anterior,
    // la actual y la siguiente (si existe), en vez de una lista completa 1..N.
    val startPage = (currentPage - 1).coerceAtLeast(1)
    val endPage = if (hasNextPage) currentPage + 1 else currentPage.coerceAtLeast(1)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.PaddingMedium),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onPageSelected(currentPage - 1) },
            enabled = currentPage > 1 && hasData
        ) {
            Icon(
                Icons.Default.ChevronLeft,
                contentDescription = stringResource(R.string.pagina_anterior)
            )
        }

        (startPage..endPage).forEach { page ->
            PromotionsPageChip(
                page = page,
                isSelected = page == currentPage,
                enabled = hasData,
                onClick = { onPageSelected(page) }
            )
            Spacer(modifier = Modifier.width(Dimens.PaddingExtraSmall))
        }

        IconButton(
            onClick = { onPageSelected(currentPage + 1) },
            enabled = hasNextPage && hasData
        ) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = stringResource(R.string.pagina_siguiente)
            )
        }
    }
}

@Composable
private fun PromotionsPageChip(
    page: Int,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor =
        if (isSelected && enabled) MaterialTheme.colorScheme.primary else Color.Transparent
    val contentColor =
        if (isSelected && enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                RoundedCornerShape(8.dp)
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = page.toString(), color = contentColor, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PromotionListEmptyState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.PaddingLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
            ) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LocalOffer,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.sin_promociones_disponibles),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(R.string.sin_promociones_descripcion),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
