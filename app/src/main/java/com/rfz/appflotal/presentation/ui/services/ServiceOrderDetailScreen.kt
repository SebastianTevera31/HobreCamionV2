package com.rfz.appflotal.presentation.ui.services

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.services.components.ConfirmDeleteDialog
import com.rfz.appflotal.presentation.ui.services.components.InfoPair
import com.rfz.appflotal.presentation.ui.services.components.ServiceOrderActionsSheet
import com.rfz.appflotal.presentation.ui.services.components.ServiceSectionHeader
import com.rfz.appflotal.presentation.ui.services.components.StatusBadge
import com.rfz.appflotal.presentation.ui.services.model.ServiceItemUi
import com.rfz.appflotal.presentation.ui.services.model.ServiceOrderUi
import com.rfz.appflotal.presentation.ui.services.model.sampleServiceItems
import com.rfz.appflotal.presentation.ui.services.model.sampleServiceOrders

/**
 * Detalle de una orden: fechas + estado, lista de servicios y total.
 *
 * Mejoras de UX sobre el mockup:
 *  - Servicios en tarjetas con proveedor / tipo de ocurrencia visibles.
 *  - Total de la orden agregado (no existía en el mockup).
 *  - Acciones de orden en el menú de la barra; borrado de servicio con confirmación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceOrderDetailScreen(
    order: ServiceOrderUi,
    items: List<ServiceItemUi>,
    onBack: () -> Unit,
    onEditOrder: () -> Unit,
    onAddService: () -> Unit,
    onEditService: (Int) -> Unit,
    onDeleteService: (Int) -> Unit,
    onDeleteOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showActions by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<ServiceItemUi?>(null) }
    var confirmDeleteOrder by remember { mutableStateOf(false) }

    val orderTotal = remember(items) { items.sumOf { it.total } }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.srv_detalle_orden_title),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.padding(start = 8.dp)) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.regresar),
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showActions = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.srv_ordenes_section),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
                    .shadow(4.dp)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddService,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.srv_accion_agregar_servicio)) }
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
            item { OrderSummaryCard(order) }

            item {
                ServiceSectionHeader(
                    icon = Icons.Outlined.Build,
                    title = stringResource(R.string.srv_detalle_servicios_section),
                    modifier = Modifier.padding(top = Dimens.PaddingSmall)
                )
            }

            if (items.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.PaddingLarge),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.srv_detalle_sin_servicios),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(items, key = { it.id }) { item ->
                    ServiceItemCard(
                        item = item,
                        onClick = { onEditService(item.id) },
                        onDelete = { itemToDelete = item }
                    )
                }
                item { OrderTotalRow(total = orderTotal) }
            }
        }
    }

    if (showActions) {
        ServiceOrderActionsSheet(
            folio = order.folio,
            showAddServices = false,
            onEditOrder = { showActions = false; onEditOrder() },
            onAddServices = { showActions = false; onAddService() },
            onDeleteOrder = {
                showActions = false
                confirmDeleteOrder = true
            },
            onDismiss = { showActions = false }
        )
    }

    itemToDelete?.let { item ->
        ConfirmDeleteDialog(
            title = stringResource(R.string.srv_confirmar_eliminar_titulo),
            message = stringResource(R.string.srv_confirmar_eliminar_servicio_msg),
            onConfirm = { itemToDelete = null; onDeleteService(item.id) },
            onDismiss = { itemToDelete = null }
        )
    }

    if (confirmDeleteOrder) {
        ConfirmDeleteDialog(
            title = stringResource(R.string.srv_confirmar_eliminar_titulo),
            message = stringResource(R.string.srv_confirmar_eliminar_orden_msg, order.folio),
            onConfirm = { confirmDeleteOrder = false; onDeleteOrder() },
            onDismiss = { confirmDeleteOrder = false }
        )
    }
}

@Composable
private fun OrderSummaryCard(order: ServiceOrderUi, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                Text(
                    text = stringResource(R.string.srv_orden_folio, order.folio),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                StatusBadge(order.status)
            }
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
                    alignment = Alignment.End,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ServiceItemCard(
    item: ServiceItemUi,
    onClick: () -> Unit,
    onDelete: () -> Unit,
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
                Text(
                    text = item.serviceName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Outlined.DeleteOutline,
                        contentDescription = stringResource(R.string.srv_accion_eliminar_servicio),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Text(
                text = "${item.provider} · ${item.occurrenceType}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.size(Dimens.PaddingMedium))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoPair(
                    label = stringResource(R.string.srv_cantidad_label),
                    value = formatQuantity(item.quantity),
                    modifier = Modifier.weight(1f)
                )
                InfoPair(
                    label = stringResource(R.string.srv_costo_unitario_label),
                    value = formatMoney(item.unitCost),
                    modifier = Modifier.weight(1f)
                )
                InfoPair(
                    label = stringResource(R.string.srv_total_label),
                    value = formatMoney(item.total),
                    alignment = Alignment.End,
                    valueColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun OrderTotalRow(total: Double, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.PaddingSmall, vertical = Dimens.PaddingSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.srv_orden_total),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = formatMoney(total),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

private fun formatMoney(value: Double): String = "$%,.2f".format(value)

private fun formatQuantity(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ServiceOrderDetailPreview() {
    HombreCamionTheme {
        ServiceOrderDetailScreen(
            order = sampleServiceOrders.first(),
            items = sampleServiceItems,
            onBack = {},
            onEditOrder = {},
            onAddService = {},
            onEditService = {},
            onDeleteService = {},
            onDeleteOrder = {}
        )
    }
}
