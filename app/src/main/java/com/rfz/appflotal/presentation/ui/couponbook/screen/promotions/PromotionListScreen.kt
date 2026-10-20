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
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.promotions.PromotionCategory
import com.rfz.appflotal.data.model.promotions.StoreDiscount
import com.rfz.appflotal.presentation.commons.ErrorView
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.couponbook.screen.main.CouponChip
import com.rfz.appflotal.presentation.ui.utils.LoadState

@Composable
fun PromotionListRoute(
    promotionsState: LoadState<Unit>,
    promotions: List<StoreDiscount>,
    currentPage: Int,
    hasNextPage: Boolean,
    selectedCategory: PromotionCategory,
    onCategorySelected: (PromotionCategory) -> Unit,
    stores: List<String>,
    selectedStore: String,
    onStoreSelected: (String) -> Unit,
    onPromotionClick: (String) -> Unit,
    onPageSelected: (Int) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        PromotionCategoryTabs(
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected
        )

        // Fuera del when para que el filtro siga visible mientras se recarga la lista.
        if (stores.size > 1) {
            PromotionStoreFilter(
                stores = stores,
                selectedStore = selectedStore,
                onStoreSelected = onStoreSelected
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
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
                        modifier = Modifier.fillMaxSize(),
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
                        onPageSelected = onPageSelected
                    )
                }

                else -> Unit
            }
        }
    }
}

/** Desplazables: "Amarre de carga" no cabe junto a las otras en pantallas angostas. */
@Composable
private fun PromotionCategoryTabs(
    selectedCategory: PromotionCategory,
    onCategorySelected: (PromotionCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = PromotionCategory.entries
    PrimaryScrollableTabRow(
        selectedTabIndex = categories.indexOf(selectedCategory),
        modifier = modifier,
        edgePadding = Dimens.PaddingMedium
    ) {
        categories.forEach { category ->
            Tab(
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
                text = { Text(text = stringResource(category.labelRes())) }
            )
        }
    }
}

internal fun PromotionCategory.labelRes(): Int = when (this) {
    PromotionCategory.ACCESSORIES -> R.string.categoria_accesorios
    PromotionCategory.TIRES -> R.string.categoria_llantas
    PromotionCategory.CARGO_SECUREMENT -> R.string.categoria_amarre_carga
}

@Composable
private fun PromotionStoreFilter(
    stores: List<String>,
    selectedStore: String,
    onStoreSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Dimens.PaddingSmall),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingExtraSmall)
    ) {
        Text(
            text = stringResource(R.string.filtrar_por),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = Dimens.PaddingMedium)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingExtraSmall),
            contentPadding = PaddingValues(horizontal = Dimens.PaddingMedium)
        ) {
            // "" representa "todas las tiendas", igual que el parámetro store del endpoint.
            item(key = "all_stores") {
                CouponChip(
                    text = stringResource(R.string.todas),
                    selected = selectedStore.isEmpty(),
                    onChangeValue = { onStoreSelected("") }
                )
            }
            items(items = stores, key = { store -> "store_$store" }) { store ->
                CouponChip(
                    text = store,
                    selected = store == selectedStore,
                    onChangeValue = { onStoreSelected(store) }
                )
            }
        }
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

@Preview(showBackground = true)
@Composable
private fun PromotionListScreenPreview() {
    HombreCamionTheme {
        PromotionListScreen(
            promotions = listOf(
                StoreDiscount(
                    store = "Tienda Camiones",
                    title = "Llanta 295/80R22.5 Toda Posición Heavy Duty",
                    sku = "LL-29580225",
                    regularPrice = 4500.0,
                    finalPrice = 3600.0,
                    percentOff = 20.0,
                    currency = "MXN",
                    productUrl = "https://example.com/product1",
                    imageUrl = null
                ),
                StoreDiscount(
                    store = "Refaccionaria Flotal",
                    title = "Aceite Sintético para Motor Diesel 15W-40 19L",
                    sku = "AC-15W40-19L",
                    regularPrice = 1800.0,
                    finalPrice = 1350.0,
                    percentOff = 25.0,
                    currency = "MXN",
                    productUrl = "https://example.com/product2",
                    imageUrl = null
                )
            ),
            currentPage = 1,
            hasNextPage = true,
            onPromotionClick = {},
            onPageSelected = {}
        )
    }
}

