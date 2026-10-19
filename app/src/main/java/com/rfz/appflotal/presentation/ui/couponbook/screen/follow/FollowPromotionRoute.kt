package com.rfz.appflotal.presentation.ui.couponbook.screen.follow

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.catalog.TopTenAccessoryResponse
import com.rfz.appflotal.data.model.promotions.AccessoryVehicleType
import com.rfz.appflotal.data.model.promotions.FollowedAccessory
import com.rfz.appflotal.data.repository.promotions.FollowFilter
import com.rfz.appflotal.data.repository.promotions.FollowRow
import com.rfz.appflotal.data.repository.promotions.FollowRowStatus
import com.rfz.appflotal.data.repository.promotions.FollowValidation
import com.rfz.appflotal.data.repository.promotions.MAX_ACCESSORY_NAME_LENGTH
import com.rfz.appflotal.data.repository.promotions.MIN_ACCESSORY_NAME_LENGTH
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.utils.LoadState

@Composable
fun FollowPromotionRoute(
    viewModel: FollowPromotionViewModel,
    state: FollowPromotionUiState,
    onOpenAccessory: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    FollowPromotionScreen(
        state = state,
        onTabSelected = viewModel::onTabSelected,
        onFilterSelected = viewModel::onFilterSelected,
        onFollowSuggestion = viewModel::onFollowSuggestion,
        onOpenAccessory = onOpenAccessory,
        onRetrySuggestions = viewModel::loadSuggestions,
        onShowAddSheet = viewModel::onShowAddSheet,
        onDismissAddSheet = viewModel::onDismissAddSheet,
        onNewAccessoryTextChanged = viewModel::onNewAccessoryTextChanged,
        onAddCustomAccessory = viewModel::onAddCustomAccessory,
        onUndoUnfollow = viewModel::onUndoUnfollow,
        onMessageShown = viewModel::onMessageShown,
        modifier = modifier
    )
}

@Composable
fun FollowPromotionScreen(
    state: FollowPromotionUiState,
    onTabSelected: (AccessoryVehicleType) -> Unit,
    onFilterSelected: (FollowFilter) -> Unit,
    onFollowSuggestion: (TopTenAccessoryResponse) -> Unit,
    onOpenAccessory: (Long) -> Unit,
    onRetrySuggestions: () -> Unit,
    onShowAddSheet: () -> Unit,
    onDismissAddSheet: () -> Unit,
    onNewAccessoryTextChanged: (String) -> Unit,
    onAddCustomAccessory: () -> Unit,
    onUndoUnfollow: () -> Unit,
    onMessageShown: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = AccessoryVehicleType.entries
    val sections = state.sections
    val snackbarHostState = remember { SnackbarHostState() }

    FollowMessageEffect(
        message = state.message,
        maxFollowed = state.maxFollowed,
        snackbarHostState = snackbarHostState,
        onUndoUnfollow = onUndoUnfollow,
        onMessageShown = onMessageShown
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
            ) {
                item {
                    FilterBar(
                        filter = state.filter,
                        followedInTabCount = state.followedInTabCount,
                        followedCount = state.followed.size,
                        maxFollowed = state.maxFollowed,
                        onFilterSelected = onFilterSelected
                    )
                }

                item {
                    SectionTitle(
                        text = stringResource(R.string.tus_busquedas),
                        modifier = Modifier.padding(top = Dimens.PaddingSmall)
                    )
                }

                if (sections.custom.isNotEmpty()) {
                    item {
                        RowsCard(
                            rows = sections.custom,
                            onFollow = onFollowSuggestion,
                            onOpenAccessory = onOpenAccessory
                        )
                    }
                }

                item {
                    AddSearchButton(onClick = onShowAddSheet)
                }

                item {
                    SectionTitle(text = stringResource(R.string.accesorios_sugeridos))
                }

                when {
                    sections.topTen.isNotEmpty() -> item {
                        RowsCard(
                            rows = sections.topTen,
                            onFollow = onFollowSuggestion,
                            onOpenAccessory = onOpenAccessory
                        )
                    }

                    state.suggestionsState is LoadState.Error -> item {
                        SuggestionsError(onRetry = onRetrySuggestions)
                    }

                    state.suggestionsState !is LoadState.Success -> item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimens.PaddingMedium),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    else -> item {
                        InfoText(
                            text = stringResource(
                                if (state.filter == FollowFilter.FOLLOWING) {
                                    R.string.sin_sugeridos_seguidos
                                } else {
                                    R.string.sin_sugeridos_categoria
                                }
                            )
                        )
                    }
                }

                // Si el catálogo falló pero hay seguidos, se muestran arriba y se ofrece reintentar.
                if (sections.topTen.isNotEmpty() && state.suggestionsState is LoadState.Error) {
                    item { SuggestionsError(onRetry = onRetrySuggestions) }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )
    }

    if (state.isAddSheetVisible) {
        AddSearchSheet(
            vehicleType = state.selectedType,
            text = state.newAccessoryText,
            error = state.addError?.toMessage(state.maxFollowed),
            onTextChanged = onNewAccessoryTextChanged,
            onConfirm = onAddCustomAccessory,
            onDismiss = onDismissAddSheet
        )
    }
}

@Composable
private fun FollowMessageEffect(
    message: FollowMessage?,
    maxFollowed: Int,
    snackbarHostState: SnackbarHostState,
    onUndoUnfollow: () -> Unit,
    onMessageShown: () -> Unit
) {
    val text = when (message) {
        null -> null
        is FollowMessage.Error -> message.error.toMessage(maxFollowed)
        is FollowMessage.Unfollowed ->
            stringResource(R.string.dejaste_de_seguir, message.accessoryName)
    }
    val undoLabel = stringResource(R.string.deshacer)

    // Se limpia al terminar: limpiarlo antes cambiaría la llave y cancelaría el snackbar.
    LaunchedEffect(message) {
        if (message == null || text == null) return@LaunchedEffect
        val isUnfollow = message is FollowMessage.Unfollowed
        val result = snackbarHostState.showSnackbar(
            message = text,
            actionLabel = if (isUnfollow) undoLabel else null,
            duration = SnackbarDuration.Short
        )
        if (isUnfollow && result == SnackbarResult.ActionPerformed) onUndoUnfollow()
        onMessageShown()
    }
}

internal fun AccessoryVehicleType.labelRes(): Int = when (this) {
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
private fun FilterBar(
    filter: FollowFilter,
    followedInTabCount: Int,
    followedCount: Int,
    maxFollowed: Int,
    onFilterSelected: (FollowFilter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
    ) {
        FilterChip(
            selected = filter == FollowFilter.ALL,
            onClick = { onFilterSelected(FollowFilter.ALL) },
            label = { Text(text = stringResource(R.string.filtro_todos)) }
        )
        FilterChip(
            selected = filter == FollowFilter.FOLLOWING,
            onClick = { onFilterSelected(FollowFilter.FOLLOWING) },
            label = { Text(text = stringResource(R.string.filtro_siguiendo, followedInTabCount)) }
        )
        Text(
            text = stringResource(R.string.contador_seguidos, followedCount, maxFollowed),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
    )
}

/** Lista compacta en una sola tarjeta con divisores, en lugar de una tarjeta por fila. */
@Composable
private fun RowsCard(
    rows: List<FollowRow>,
    onFollow: (TopTenAccessoryResponse) -> Unit,
    onOpenAccessory: (Long) -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        rows.forEachIndexed { index, row ->
            if (index > 0) HorizontalDivider()
            FollowRowItem(
                row = row,
                onFollow = onFollow,
                onOpenAccessory = onOpenAccessory
            )
        }
    }
}

@Composable
private fun FollowRowItem(
    row: FollowRow,
    onFollow: (TopTenAccessoryResponse) -> Unit,
    onOpenAccessory: (Long) -> Unit
) {
    val followed = row.followed
    val rowModifier = if (followed != null) {
        Modifier.clickable { onOpenAccessory(followed.id) }
    } else {
        Modifier
    }

    Row(
        modifier = rowModifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.PaddingMedium, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
    ) {
        PositionBadge(position = row.position)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = row.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            RowStatusText(status = row.status)
        }

        val catalogAccessory = row.catalogAccessory
        if (followed != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else if (catalogAccessory != null) {
            OutlinedButton(onClick = { onFollow(catalogAccessory) }) {
                Text(text = stringResource(R.string.seguir_accesorio))
            }
        }
    }
}

@Composable
private fun PositionBadge(position: Int?) {
    Surface(
        modifier = Modifier.size(32.dp),
        shape = RoundedCornerShape(10.dp),
        color = if (position != null) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (position != null) {
                Text(
                    text = position.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RowStatusText(status: FollowRowStatus) {
    val (text, color) = when (status) {
        FollowRowStatus.NotFollowed ->
            stringResource(R.string.estado_sin_seguir) to MaterialTheme.colorScheme.onSurfaceVariant

        FollowRowStatus.Searching ->
            stringResource(R.string.buscando_promociones) to MaterialTheme.colorScheme.onSurfaceVariant

        FollowRowStatus.NoDeals ->
            stringResource(R.string.sin_promociones_accesorio) to MaterialTheme.colorScheme.onSurfaceVariant

        is FollowRowStatus.Deals -> pluralStringResource(
            R.plurals.estado_ofertas,
            status.count,
            status.count,
            status.bestPercentOff
        ) to MaterialTheme.colorScheme.primary
    }
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun AddSearchButton(onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = stringResource(R.string.agregar_busqueda),
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
private fun SuggestionsError(onRetry: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InfoText(
            text = stringResource(R.string.error_cargar_sugeridos),
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onRetry) {
            Text(text = stringResource(R.string.reintentar))
        }
    }
}

@Composable
private fun InfoText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddSearchSheet(
    vehicleType: AccessoryVehicleType,
    text: String,
    error: String?,
    onTextChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.PaddingLarge)
                .padding(bottom = Dimens.PaddingLarge),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
        ) {
            Text(
                text = stringResource(
                    R.string.agregar_busqueda_titulo,
                    stringResource(vehicleType.labelRes())
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = text,
                onValueChange = { value ->
                    // Margen sobre el máximo para que la validación muestre el error en vez de cortar en silencio.
                    if (value.length <= MAX_ACCESSORY_NAME_LENGTH + 10) onTextChanged(value)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text(text = stringResource(R.string.agregar_accesorio_hint)) },
                isError = error != null,
                supportingText = {
                    Text(
                        text = error ?: stringResource(
                            R.string.agregar_busqueda_ayuda,
                            MIN_ACCESSORY_NAME_LENGTH,
                            MAX_ACCESSORY_NAME_LENGTH
                        )
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onConfirm() })
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall, Alignment.End)
            ) {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(R.string.cancelar_busqueda))
                }
                Button(onClick = onConfirm) {
                    Text(text = stringResource(R.string.seguir_accesorio))
                }
            }
        }
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
                        lastSyncedAt = null,
                        promotions = emptyList()
                    )
                ),
                topTen = listOf(
                    TopTenAccessoryResponse(
                        idAccessory = 1,
                        vehicleType = "TRUCK",
                        position = 1,
                        name = "Dash cam",
                        description = "Graba incidentes durante el recorrido.",
                        active = true
                    ),
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
            onFilterSelected = {},
            onFollowSuggestion = {},
            onOpenAccessory = {},
            onRetrySuggestions = {},
            onShowAddSheet = {},
            onDismissAddSheet = {},
            onNewAccessoryTextChanged = {},
            onAddCustomAccessory = {},
            onUndoUnfollow = {},
            onMessageShown = {}
        )
    }
}
