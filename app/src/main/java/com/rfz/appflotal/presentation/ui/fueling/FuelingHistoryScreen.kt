package com.rfz.appflotal.presentation.ui.fueling

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.commons.RequiresInternetNotice
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.alerts.screens.PageNavigator
import com.rfz.appflotal.presentation.ui.fueling.components.FuelingCard
import com.rfz.appflotal.presentation.ui.fueling.components.FuelingFilterCard
import com.rfz.appflotal.presentation.ui.fueling.model.FuelingUi
import com.rfz.appflotal.presentation.ui.fueling.model.sampleFuelTypes
import com.rfz.appflotal.presentation.ui.fueling.model.sampleFuelings
import com.rfz.appflotal.presentation.ui.services.VehicleHeaderCard
import com.rfz.appflotal.presentation.ui.services.components.ServiceSectionHeader
import com.rfz.appflotal.presentation.ui.services.model.CatalogItemUi
import com.rfz.appflotal.presentation.ui.services.model.VehicleHeaderUi
import com.rfz.appflotal.presentation.ui.services.model.sampleVehicleHeader

/**
 * Historial de cargas de combustible del vehículo (solo lectura, paginado en servidor).
 *  - Filtros por rango de fechas y tipo de combustible.
 *  - Acción "Registrar carga" (+) en la barra superior; no usa FAB para no tapar el paginador.
 *  - Estados vacío / carga / error / offline.
 */
@Composable
fun FuelingHistoryScreen(
    vehicle: VehicleHeaderUi?,
    fuelings: List<FuelingUi>,
    fuelTypes: List<CatalogItemUi>,
    currentPage: Int,
    hasNextPage: Boolean,
    startDate: String,
    endDate: String,
    fuelTypeId: Int?,
    onBack: () -> Unit,
    onNewFueling: () -> Unit,
    onApplyFilters: (startDate: String, endDate: String, fuelTypeId: Int?) -> Unit,
    onPageSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    isOffline: Boolean = false,
    onRetry: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SimpleTopBar(
                title = stringResource(R.string.fue_title),
                onBack = onBack,
                showBackButton = true,
                actions = {
                    // Sin conexión no se puede registrar: se oculta la acción.
                    if (!isOffline) {
                        IconButton(onClick = onNewFueling) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.fue_nueva_carga),
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
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(Dimens.PaddingMedium),
                verticalArrangement = Arrangement.spacedBy(Dimens.ListItemSpacing)
            ) {
                if (isOffline) {
                    item {
                        RequiresInternetNotice(
                            message = stringResource(R.string.fue_requiere_internet),
                            modifier = Modifier.padding(bottom = Dimens.PaddingSmall)
                        )
                    }
                }

                if (vehicle != null) {
                    item { VehicleHeaderCard(vehicle) }
                }

                item {
                    FuelingFilterCard(
                        appliedStartDate = startDate,
                        appliedEndDate = endDate,
                        appliedFuelTypeId = fuelTypeId,
                        fuelTypes = fuelTypes,
                        isOffline = isOffline,
                        onApplyFilters = onApplyFilters
                    )
                }

                item {
                    ServiceSectionHeader(
                        icon = Icons.Outlined.LocalGasStation,
                        title = stringResource(R.string.fue_section_historial),
                        modifier = Modifier.padding(top = Dimens.PaddingSmall)
                    )
                }

                // El historial no trae id: la posición en la página basta como llave.
                itemsIndexed(fuelings, key = { index, _ -> "$currentPage-$index" }) { _, fueling ->
                    FuelingCard(fueling)
                }

                when {
                    isLoading -> item { CenteredLoader() }
                    errorMessage != null -> item { ErrorState(onRetry) }
                    fuelings.isEmpty() -> item { EmptyState() }
                }
            }

            PageNavigator(
                hasData = fuelings.isNotEmpty(),
                currentPage = currentPage,
                hasNextPage = hasNextPage && !isOffline,
                isLoading = isLoading,
                onPageSelected = onPageSelected
            )
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
            text = stringResource(R.string.fue_sin_cargas),
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
            text = stringResource(R.string.fue_error_cargar),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        TextButton(onClick = onRetry) { Text(stringResource(R.string.reintentar)) }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FuelingHistoryScreenPreview() {
    HombreCamionTheme {
        FuelingHistoryScreen(
            vehicle = sampleVehicleHeader,
            fuelings = sampleFuelings,
            fuelTypes = sampleFuelTypes,
            currentPage = 1,
            hasNextPage = true,
            startDate = "",
            endDate = "",
            fuelTypeId = null,
            onBack = {},
            onNewFueling = {},
            onApplyFilters = { _, _, _ -> },
            onPageSelected = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FuelingHistoryScreenEmptyPreview() {
    HombreCamionTheme {
        FuelingHistoryScreen(
            vehicle = sampleVehicleHeader,
            fuelings = emptyList(),
            fuelTypes = sampleFuelTypes,
            currentPage = 1,
            hasNextPage = false,
            startDate = "",
            endDate = "",
            fuelTypeId = null,
            onBack = {},
            onNewFueling = {},
            onApplyFilters = { _, _, _ -> },
            onPageSelected = {}
        )
    }
}
