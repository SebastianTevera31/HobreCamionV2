package com.rfz.appflotal.presentation.ui.couponbook.screen.follow

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.catalog.TopTenAccessoryResponse
import com.rfz.appflotal.data.model.promotions.AccessoryVehicleType
import com.rfz.appflotal.data.model.promotions.FollowedAccessory
import com.rfz.appflotal.data.model.promotions.StoreDiscount
import com.rfz.appflotal.data.repository.promotions.FollowValidation
import com.rfz.appflotal.data.repository.promotions.MAX_ACCESSORY_NAME_LENGTH
import com.rfz.appflotal.data.repository.promotions.MIN_ACCESSORY_NAME_LENGTH
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.couponbook.screen.promotions.PromotionCard
import com.rfz.appflotal.presentation.ui.utils.LoadState

@Composable
fun FollowPromotionRoute(
    viewModel: FollowPromotionViewModel,
    state: FollowPromotionUiState,
    onPromotionClick: (StoreDiscount) -> Unit,
    modifier: Modifier = Modifier
) {
    FollowPromotionScreen(
        state = state,
        onTabSelected = viewModel::onTabSelected,
        onNewAccessoryTextChanged = viewModel::onNewAccessoryTextChanged,
        onAddCustomAccessory = viewModel::onAddCustomAccessory,
        onFollowSuggestion = viewModel::onFollowSuggestion,
        onUnfollow = viewModel::onUnfollow,
        onRetrySuggestions = viewModel::loadSuggestions,
        onRefreshPromotions = viewModel::onRefreshPromotions,
        onPromotionClick = onPromotionClick,
        modifier = modifier
    )
}

@Composable
fun FollowPromotionScreen(
    state: FollowPromotionUiState,
    onTabSelected: (AccessoryVehicleType) -> Unit,
    onNewAccessoryTextChanged: (String) -> Unit,
    onAddCustomAccessory: () -> Unit,
    onFollowSuggestion: (TopTenAccessoryResponse) -> Unit,
    onUnfollow: (Long) -> Unit,
    onRetrySuggestions: () -> Unit,
    onRefreshPromotions: () -> Unit,
    onPromotionClick: (StoreDiscount) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = AccessoryVehicleType.entries

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        PrimaryTabRow(selectedTabIndex = tabs.indexOf(state.selectedType)) {
            tabs.forEach { type ->
                Tab(
                    selected = type == state.selectedType,
                    onClick = { onTabSelected(type) },
                    text = { Text(text = stringResource(type.labelRes())) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Dimens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
        ) {
            item {
                FollowedCounter(
                    followedCount = state.followed.size,
                    maxFollowed = state.maxFollowed,
                    onRefreshPromotions = onRefreshPromotions,
                    canRefresh = state.followed.isNotEmpty()
                )
            }

            item {
                AddAccessoryField(
                    text = state.newAccessoryText,
                    error = state.followError?.toMessage(state.maxFollowed),
                    enabled = !state.isLimitReached,
                    onTextChanged = onNewAccessoryTextChanged,
                    onAdd = onAddCustomAccessory
                )
            }

            item {
                SectionTitle(text = stringResource(R.string.mis_accesorios))
            }

            if (state.followedForTab.isEmpty()) {
                item {
                    InfoCard(text = stringResource(R.string.sin_accesorios_seguidos))
                }
            } else {
                items(
                    items = state.followedForTab,
                    key = { accessory -> "followed-${accessory.id}" }
                ) { accessory ->
                    FollowedAccessoryItem(
                        accessory = accessory,
                        onUnfollow = { onUnfollow(accessory.id) },
                        onPromotionClick = onPromotionClick
                    )
                }
            }

            item {
                SectionTitle(text = stringResource(R.string.accesorios_sugeridos))
            }

            when (state.suggestionsState) {
                is LoadState.Error -> item {
                    SuggestionsError(onRetry = onRetrySuggestions)
                }

                LoadState.Loading, LoadState.Idle -> item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                else -> {
                    if (state.suggestionsForTab.isEmpty()) {
                        item {
                            InfoCard(text = stringResource(R.string.todos_sugeridos_seguidos))
                        }
                    } else {
                        items(
                            items = state.suggestionsForTab,
                            key = { accessory -> "suggested-${accessory.idAccessory}" }
                        ) { accessory ->
                            SuggestedAccessoryItem(
                                accessory = accessory,
                                followEnabled = !state.isLimitReached,
                                onFollow = { onFollowSuggestion(accessory) }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun AccessoryVehicleType.labelRes(): Int = when (this) {
    AccessoryVehicleType.TRUCK -> R.string.tipo_accesorio_truck
    AccessoryVehicleType.RV -> R.string.tipo_accesorio_rv
}

@Composable
private fun FollowError.toMessage(maxFollowed: Int): String? = when (this) {
    FollowError.SaveFailed -> stringResource(R.string.error_guardar_accesorio)
    is FollowError.Invalid -> when (reason) {
        FollowValidation.VALID -> null
        FollowValidation.TOO_SHORT ->
            stringResource(R.string.error_accesorio_corto, MIN_ACCESSORY_NAME_LENGTH)

        FollowValidation.TOO_LONG ->
            stringResource(R.string.error_accesorio_largo, MAX_ACCESSORY_NAME_LENGTH)

        FollowValidation.DUPLICATE -> stringResource(R.string.error_accesorio_duplicado)
        FollowValidation.LIMIT_REACHED ->
            stringResource(R.string.error_limite_accesorios, maxFollowed)
    }
}

@Composable
private fun FollowedCounter(
    followedCount: Int,
    maxFollowed: Int,
    canRefresh: Boolean,
    onRefreshPromotions: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.accesorios_seguidos_contador, followedCount, maxFollowed),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        TextButton(onClick = onRefreshPromotions, enabled = canRefresh) {
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
}

@Composable
private fun AddAccessoryField(
    text: String,
    error: String?,
    enabled: Boolean,
    onTextChanged: (String) -> Unit,
    onAdd: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { value ->
                // Margen sobre el máximo para que la validación muestre el error en vez de cortar en silencio.
                if (value.length <= MAX_ACCESSORY_NAME_LENGTH + 10) onTextChanged(value)
            },
            modifier = Modifier.weight(1f),
            enabled = enabled,
            singleLine = true,
            label = { Text(text = stringResource(R.string.agregar_accesorio_hint)) },
            isError = error != null,
            supportingText = error?.let { message -> { Text(text = message) } },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onAdd() })
        )

        Button(
            onClick = onAdd,
            enabled = enabled && text.isNotBlank(),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = stringResource(R.string.agregar_accesorio),
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun FollowedAccessoryItem(
    accessory: FollowedAccessory,
    onUnfollow: () -> Unit,
    onPromotionClick: (StoreDiscount) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = accessory.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                accessory.description?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onUnfollow) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.dejar_de_seguir_accesorio, accessory.name)
                )
            }
        }

        when {
            accessory.promotions.isNotEmpty() -> accessory.promotions.forEach { discount ->
                PromotionCard(
                    discount = discount,
                    onClick = { onPromotionClick(discount) }
                )
            }

            accessory.pendingFirstSync -> InfoCard(text = stringResource(R.string.buscando_promociones))
            else -> InfoCard(text = stringResource(R.string.sin_promociones_accesorio))
        }
    }
}

@Composable
private fun SuggestedAccessoryItem(
    accessory: TopTenAccessoryResponse,
    followEnabled: Boolean,
    onFollow: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
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
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = accessory.position.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = accessory.name.orEmpty(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                accessory.description?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedButton(onClick = onFollow, enabled = followEnabled) {
                Text(text = stringResource(R.string.seguir_accesorio))
            }
        }
    }
}

@Composable
private fun SuggestionsError(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InfoCard(text = stringResource(R.string.error_cargar_sugeridos))
        TextButton(onClick = onRetry) {
            Text(text = stringResource(R.string.reintentar))
        }
    }
}

@Composable
private fun InfoCard(text: String) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(Dimens.PaddingMedium)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FollowPromotionScreenPreview() {
    HombreCamionTheme {
        FollowPromotionScreen(
            state = FollowPromotionUiState(
                followed = listOf(
                    FollowedAccessory(
                        id = 1,
                        catalogAccessoryId = 1,
                        vehicleType = AccessoryVehicleType.TRUCK,
                        name = "Dash cam",
                        description = "Graba incidentes durante el recorrido.",
                        pendingFirstSync = true,
                        promotions = emptyList()
                    )
                ),
                topTen = listOf(
                    TopTenAccessoryResponse(
                        idAccessory = 2,
                        vehicleType = "TRUCK",
                        position = 2,
                        name = "Truck GPS navigator",
                        description = "Permite planear rutas según las dimensiones y el peso del vehículo.",
                        active = true
                    )
                ),
                suggestionsState = LoadState.Success(Unit)
            ),
            onTabSelected = {},
            onNewAccessoryTextChanged = {},
            onAddCustomAccessory = {},
            onFollowSuggestion = {},
            onUnfollow = {},
            onRetrySuggestions = {},
            onRefreshPromotions = {},
            onPromotionClick = {}
        )
    }
}
