package com.rfz.appflotal.presentation.ui.services

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
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.commons.RequiresInternetNotice
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.services.components.ConfirmDeleteDialog
import com.rfz.appflotal.presentation.ui.services.components.InfoPair
import com.rfz.appflotal.presentation.ui.services.components.ServiceOrderActionsSheet
import com.rfz.appflotal.presentation.ui.services.components.ServiceSectionHeader
import com.rfz.appflotal.presentation.ui.services.components.StatusBadge
import com.rfz.appflotal.presentation.ui.services.model.ServiceOrderUi
import com.rfz.appflotal.presentation.ui.services.model.VehicleHeaderUi
import com.rfz.appflotal.presentation.ui.services.model.sampleServiceOrders
import com.rfz.appflotal.presentation.ui.services.model.sampleVehicleHeader

/**
 * Pantalla de lista de órdenes de servicio de un vehículo.
 *
 * Mejoras de UX sobre el mockup:
 *  - La tabla apretada se sustituye por tarjetas (consistente con el módulo de alertas).
 *  - Estado por orden con pastilla (Abierta / Finalizada) y fecha de finalizado explícita.
 *  - FAB "Nueva orden" (entrada que faltaba para crear órdenes).
 *  - Estados de vacío / carga / error / offline.
 *  - Confirmación antes de eliminar.
 */
@Composable
fun ServicesScreen(
    vehicle: VehicleHeaderUi?,
    orders: List<ServiceOrderUi>,
    onBack: () -> Unit,
    onOpenOrder: (Int) -> Unit,
    onNewOrder: () -> Unit,
    onEditOrder: (Int) -> Unit,
    onAddServices: (Int) -> Unit,
    onDeleteOrder: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    isOffline: Boolean = false,
    onRetry: () -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    var actionsOrder by remember { mutableStateOf<ServiceOrderUi?>(null) }
    var orderToDelete by remember { mutableStateOf<ServiceOrderUi?>(null) }

    val filtered = remember(orders, query) {
        if (query.isBlank()) orders
        else orders.filter {
            it.folio.contains(query, true) || it.summary.contains(query, true)
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SimpleTopBar(
                title = stringResource(R.string.srv_title),
                onBack = onBack,
                showBackButton = true
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNewOrder,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.srv_nueva_orden)) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(Dimens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.ListItemSpacing)
        ) {
            if (isOffline) {
                item {
                    RequiresInternetNotice(
                        message = stringResource(R.string.srv_requiere_internet),
                        modifier = Modifier.padding(bottom = Dimens.PaddingSmall)
                    )
                }
            }

            item { VehicleHeaderCard(vehicle) }

            item {
                ServiceSectionHeader(
                    icon = Icons.AutoMirrored.Outlined.ListAlt,
                    title = stringResource(R.string.srv_ordenes_section),
                    modifier = Modifier.padding(top = Dimens.PaddingSmall)
                )
            }

            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text(stringResource(R.string.srv_buscar_orden)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            items(filtered, key = { it.id }) { order ->
                ServiceOrderCard(
                    order = order,
                    onClick = { onOpenOrder(order.id) },
                    onActions = { actionsOrder = order }
                )
            }

            when {
                isLoading -> item { CenteredLoader() }
                errorMessage != null -> item { ErrorState(onRetry) }
                filtered.isEmpty() -> item { EmptyState() }
            }
        }
    }

    actionsOrder?.let { order ->
        ServiceOrderActionsSheet(
            folio = order.folio,
            onEditOrder = { actionsOrder = null; onEditOrder(order.id) },
            onAddServices = { actionsOrder = null; onAddServices(order.id) },
            onDeleteOrder = {
                actionsOrder = null
                orderToDelete = order
            },
            onDismiss = { actionsOrder = null }
        )
    }

    orderToDelete?.let { order ->
        ConfirmDeleteDialog(
            title = stringResource(R.string.srv_confirmar_eliminar_titulo),
            message = stringResource(R.string.srv_confirmar_eliminar_orden_msg, order.folio),
            onConfirm = { orderToDelete = null; onDeleteOrder(order.id) },
            onDismiss = { orderToDelete = null }
        )
    }
}

@Composable
private fun VehicleHeaderCard(vehicle: VehicleHeaderUi?, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(Dimens.PaddingMedium)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.DirectionsBus,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
                Text(
                    text = stringResource(R.string.srv_vehiculo_label),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.size(Dimens.PaddingMedium))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoPair(
                    label = stringResource(R.string.srv_tipo_label),
                    value = "${vehicle?.economicNumber} · ${vehicle?.description}",
                    modifier = Modifier.weight(1f)
                )
                InfoPair(
                    label = stringResource(R.string.srv_odometro_label),
                    value = vehicle?.odometer ?: "",
                    alignment = Alignment.End,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ServiceOrderCard(
    order: ServiceOrderUi,
    onClick: () -> Unit,
    onActions: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(Dimens.PaddingMedium)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.srv_orden_folio, order.folio),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
                    StatusBadge(order.status)
                }
                IconButton(onClick = onActions) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.srv_ordenes_section),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = order.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )

            Spacer(modifier = Modifier.size(Dimens.PaddingMedium))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoPair(
                    label = stringResource(R.string.srv_orden_apertura),
                    value = order.openingDate,
                    modifier = Modifier.weight(1f)
                )
                InfoPair(
                    label = stringResource(R.string.srv_orden_finalizado),
                    value = order.closingDate ?: stringResource(R.string.srv_orden_en_proceso),
                    modifier = Modifier.weight(1f)
                )
                InfoPair(
                    label = stringResource(R.string.srv_orden_total),
                    value = order.total,
                    alignment = Alignment.End,
                    valueColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CenteredLoader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.PaddingLarge),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.PaddingLarge),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.srv_sin_ordenes),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorState(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.PaddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
    ) {
        Text(
            text = stringResource(R.string.srv_error_cargar),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        TextButton(onClick = onRetry) { Text(stringResource(R.string.reintentar)) }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ServicesScreenPreview() {
    HombreCamionTheme {
        ServicesScreen(
            vehicle = sampleVehicleHeader,
            orders = sampleServiceOrders,
            onBack = {},
            onOpenOrder = {},
            onNewOrder = {},
            onEditOrder = {},
            onAddServices = {},
            onDeleteOrder = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ServicesScreenEmptyPreview() {
    HombreCamionTheme {
        ServicesScreen(
            vehicle = sampleVehicleHeader,
            orders = emptyList(),
            onBack = {},
            onOpenOrder = {},
            onNewOrder = {},
            onEditOrder = {},
            onAddServices = {},
            onDeleteOrder = {}
        )
    }
}
