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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.rfz.appflotal.presentation.ui.services.model.toMoney
import kotlinx.coroutines.flow.flowOf
import androidx.paging.LoadState as PagingLoadState
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
import com.rfz.appflotal.presentation.ui.alerts.screens.PageNavigator
import com.rfz.appflotal.presentation.ui.services.components.ConfirmDeleteDialog
import com.rfz.appflotal.presentation.ui.services.components.InfoPair
import com.rfz.appflotal.presentation.ui.services.components.ServiceSectionHeader
import com.rfz.appflotal.presentation.ui.services.model.ServiceUi
import com.rfz.appflotal.presentation.ui.services.model.VehicleHeaderUi
import com.rfz.appflotal.presentation.ui.services.model.sampleServices
import com.rfz.appflotal.presentation.ui.services.model.sampleVehicleHeader

/**
 * Lista de servicios del vehículo (flujo "solo servicios").
 *  - Tarjeta por servicio; tocar = editar.
 *  - FAB "Nuevo servicio".
 *  - Estados vacío / carga / error / offline y borrado con confirmación.
 */
@Composable
fun ServicesScreen(
    vehicle: VehicleHeaderUi?,
    // Página actual (Paging 3, ServiceViewModel.pagedServices), ya filtrada por [query].
    services: LazyPagingItems<ServiceUi>,
    query: String,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    onNewService: () -> Unit,
    onEditService: (Int) -> Unit,
    onDeleteService: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    isOffline: Boolean = false,
    onRetry: () -> Unit = {},
    currentPage: Int = 1,        // base 1, como PageNavigator
    pageCount: Int = 1,
    onPageSelected: (Int) -> Unit = {}
) {
    var toDelete by remember { mutableStateOf<ServiceUi?>(null) }
    val isEmpty = services.itemCount == 0 &&
            services.loadState.refresh is PagingLoadState.NotLoading
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SimpleTopBar(
                title = stringResource(R.string.srv_title),
                onBack = onBack,
                showBackButton = true,
                actions = {
                    // Igual que en Combustible: la acción va en la barra superior y no
                    // en un FAB, para no tapar el paginador. Sin conexión se oculta.
                    if (!isOffline) {
                        IconButton(onClick = onNewService) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.srv_form_nuevo_title),
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
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
    
                if (vehicle != null) {
                    item { VehicleHeaderCard(vehicle) }
                }
    
                item {
                    ServiceSectionHeader(
                        icon = Icons.Outlined.Build,
                        title = stringResource(R.string.srv_detalle_servicios_section),
                        modifier = Modifier.padding(top = Dimens.PaddingSmall)
                    )
                }
    
                item {
                    OutlinedTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        placeholder = { Text(stringResource(R.string.srv_buscar_servicio)) },
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
    
                items(
                    count = services.itemCount,
                    key = services.itemKey { it.id },
                    contentType = services.itemContentType { "service" }
                ) { index ->
                    // Sin placeholders el elemento siempre está cargado; el null es solo por tipo.
                    val service = services[index] ?: return@items
                    ServiceCard(
                        service = service,
                        onClick = { onEditService(service.id) },
                        onDelete = { toDelete = service }
                    )
                }
    
                when {
                    isLoading -> item { CenteredLoader() }
                    errorMessage != null -> item { ErrorState(onRetry) }
                    isEmpty -> item { EmptyState() }
                }
            }
    
            // Fijo al pie, fuera de la lista (mismo paginador que Alertas y Combustible).
            PageNavigator(
                hasData = services.itemCount > 0,
                currentPage = currentPage,
                hasNextPage = currentPage < pageCount,
                isLoading = isLoading,
                onPageSelected = { page ->
                    onPageSelected(page)
                    // Al cambiar de página se regresa al inicio de la lista.
                    scope.launch { listState.scrollToItem(0) }
                }
            )
        }
    }

    toDelete?.let { service ->
        ConfirmDeleteDialog(
            title = stringResource(R.string.srv_confirmar_eliminar_titulo),
            message = stringResource(R.string.srv_confirmar_eliminar_servicio_msg),
            onConfirm = { toDelete = null; onDeleteService(service.id) },
            onDismiss = { toDelete = null }
        )
    }
}

/** Cabecera con los datos del vehículo. También la usa el historial de cargas de combustible. */
@Composable
fun VehicleHeaderCard(vehicle: VehicleHeaderUi, modifier: Modifier = Modifier) {
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
                    value = "${vehicle.economicNumber} · ${vehicle.description}",
                    modifier = Modifier.weight(1f)
                )
                InfoPair(
                    label = stringResource(R.string.srv_odometro_label),
                    value = "%,d".format(vehicle.odometer.toIntOrNull() ?: 0),
                    alignment = Alignment.End,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ServiceCard(
    service: ServiceUi,
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
                    text = "Servicio #${service.id}",
                    style = MaterialTheme.typography.titleMedium,
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

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = service.description,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = service.date,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.size(Dimens.PaddingSmall))
            }

            Row {
                Text(
                    text = stringResource(R.string.srv_tipo_label),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = ": ${service.type}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row {
                Text(
                    text = stringResource(R.string.srv_proveedor_label),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = ": ${service.provider}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.size(Dimens.PaddingMedium))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoPair(
                    label = stringResource(R.string.srv_cantidad_label),
                    value = service.quantity.toString(),
                    modifier = Modifier.weight(1f)
                )
                InfoPair(
                    label = stringResource(R.string.srv_costo_unitario_label),
                    value = service.price.toMoney(),
                    modifier = Modifier.weight(1f)
                )
                InfoPair(
                    label = stringResource(R.string.srv_total_label),
                    value = service.total.toMoney(),
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
            text = stringResource(R.string.srv_sin_servicios),
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
            services = flowOf(PagingData.from(sampleServices)).collectAsLazyPagingItems(),
            query = "",
            onQueryChange = {},
            onBack = {},
            onNewService = {},
            onEditService = {},
            onDeleteService = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ServicesScreenEmptyPreview() {
    HombreCamionTheme {
        ServicesScreen(
            vehicle = sampleVehicleHeader,
            services = flowOf(PagingData.empty<ServiceUi>()).collectAsLazyPagingItems(),
            query = "",
            onQueryChange = {},
            onBack = {},
            onNewService = {},
            onEditService = {},
            onDeleteService = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ServiceCardPreview() {
    HombreCamionTheme {
        ServiceCard(
            service = sampleServices.first(),
            onClick = {},
            onDelete = {}
        )
    }
}
