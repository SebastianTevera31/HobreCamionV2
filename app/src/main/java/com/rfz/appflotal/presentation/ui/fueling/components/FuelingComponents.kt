package com.rfz.appflotal.presentation.ui.fueling.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.Commons
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.alerts.screens.BaseFilterField
import com.rfz.appflotal.presentation.ui.alerts.screens.DateFilterField
import com.rfz.appflotal.presentation.ui.alerts.screens.FilterChipSummary
import com.rfz.appflotal.presentation.ui.fueling.model.FuelingUi
import com.rfz.appflotal.presentation.ui.fueling.model.sampleFuelTypes
import com.rfz.appflotal.presentation.ui.fueling.model.sampleFuelings
import com.rfz.appflotal.presentation.ui.services.components.InfoPair
import com.rfz.appflotal.presentation.ui.services.model.CatalogItemUi

/** Tarjeta de una carga del historial (solo lectura). */
@Composable
fun FuelingCard(fueling: FuelingUi, modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(Dimens.PaddingMedium)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.LocalGasStation,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = fueling.fuelType,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = fueling.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            if (fueling.station.isNotBlank()) {
                Spacer(modifier = Modifier.size(Dimens.PaddingSmall))
                Row {
                    Text(
                        text = stringResource(R.string.fue_estacion_label),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = ": ${fueling.station}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.size(Dimens.PaddingMedium))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoPair(
                    label = stringResource(R.string.fue_cantidad),
                    value = fueling.units,
                    modifier = Modifier.weight(1f)
                )
                InfoPair(
                    label = stringResource(R.string.fue_odometro),
                    value = fueling.odometer,
                    modifier = Modifier.weight(1f)
                )
                InfoPair(
                    label = stringResource(R.string.fue_monto),
                    value = fueling.amount,
                    alignment = Alignment.End,
                    valueColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
            }

            fueling.mapsUrl?.let { url ->
                Spacer(modifier = Modifier.size(Dimens.PaddingSmall))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { uriHandler.openUri(url) }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(Dimens.PaddingExtraSmall))
                    Text(
                        text = stringResource(R.string.ver_mapa),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
    }
}

/** Selector de tipo de combustible para el filtro; null = todos. */
@Composable
fun FuelTypeFilterField(
    selectedId: Int?,
    fuelTypes: List<CatalogItemUi>,
    onSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val allLabel = stringResource(R.string.todos)
    val selectedName = fuelTypes.firstOrNull { it.id == selectedId }?.name ?: allLabel

    Box(modifier = modifier) {
        BaseFilterField(
            label = stringResource(R.string.fue_tipo_combustible),
            value = selectedName,
            icon = Icons.Outlined.LocalGasStation,
            onClick = { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            DropdownMenuItem(
                text = { Text(allLabel) },
                onClick = {
                    onSelected(null)
                    expanded = false
                }
            )
            fuelTypes.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.name) },
                    onClick = {
                        onSelected(type.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Tarjeta plegable de filtros (rango de fechas y tipo de combustible), como la del
 * historial de alertas. Las fechas se manejan en dd/MM/yyyy, formato de [DateFilterField].
 */
@Composable
fun FuelingFilterCard(
    appliedStartDate: String,
    appliedEndDate: String,
    appliedFuelTypeId: Int?,
    fuelTypes: List<CatalogItemUi>,
    isOffline: Boolean,
    onApplyFilters: (startDate: String, endDate: String, fuelTypeId: Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var startDate by remember(appliedStartDate) { mutableStateOf(appliedStartDate) }
    var endDate by remember(appliedEndDate) { mutableStateOf(appliedEndDate) }
    var fuelTypeId by remember(appliedFuelTypeId) { mutableStateOf(appliedFuelTypeId) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier.padding(Dimens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
                    Text(
                        text = stringResource(R.string.filtros),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = stringResource(
                            if (expanded) R.string.colapsar else R.string.expandir
                        ),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (expanded) {
                FuelTypeFilterField(
                    selectedId = fuelTypeId,
                    fuelTypes = fuelTypes,
                    onSelected = { fuelTypeId = it }
                )
                DateFilterField(
                    label = R.string.desde_label,
                    selectedDate = startDate,
                    onDateSelected = { startDate = it }
                )
                DateFilterField(
                    label = R.string.hasta_label,
                    selectedDate = endDate,
                    onDateSelected = { endDate = it }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.PaddingSmall),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
                ) {
                    OutlinedButton(
                        onClick = {
                            startDate = ""
                            endDate = ""
                            fuelTypeId = null
                            onApplyFilters("", "", null)
                            expanded = false
                        },
                        enabled = startDate.isNotEmpty() || endDate.isNotEmpty() || fuelTypeId != null,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = stringResource(R.string.limpiar))
                    }
                    Button(
                        onClick = {
                            onApplyFilters(startDate, endDate, fuelTypeId)
                            expanded = false
                        },
                        enabled = !isOffline,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.aplicar_filtros),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                val allLabel = stringResource(R.string.todas)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
                ) {
                    FilterChipSummary(
                        label = stringResource(R.string.desde_label),
                        value = Commons.toDisplayDate(appliedStartDate).ifEmpty { allLabel },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChipSummary(
                        label = stringResource(R.string.hasta_label),
                        value = Commons.toDisplayDate(appliedEndDate).ifEmpty { allLabel },
                        modifier = Modifier.weight(1f)
                    )
                }
                FilterChipSummary(
                    label = stringResource(R.string.fue_tipo_combustible),
                    value = fuelTypes.firstOrNull { it.id == appliedFuelTypeId }?.name
                        ?: stringResource(R.string.todos),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FuelingCardPreview() {
    HombreCamionTheme {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)) {
            sampleFuelings.forEach { FuelingCard(it) }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FuelingFilterCardPreview() {
    HombreCamionTheme {
        FuelingFilterCard(
            appliedStartDate = "01/01/2026",
            appliedEndDate = "",
            appliedFuelTypeId = 1,
            fuelTypes = sampleFuelTypes,
            isOffline = false,
            onApplyFilters = { _, _, _ -> }
        )
    }
}
