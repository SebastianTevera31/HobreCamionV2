package com.rfz.appflotal.presentation.ui.alertas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.BatteryAlert
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.AlertCard
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.AlertStatus
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.AlertUi

@Composable
fun AlertsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = Color(0xFFF8F9FA), // Un fondo suave para que las tarjetas resalten
        topBar = {
            SimpleTopBar(
                title = "Alertas",
                onBack = onBack,
                showBackButton = true,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            FilterAlertComponent(
                modifier = Modifier.padding(top = Dimens.PaddingMedium)
            )
            LazyColumn(
                contentPadding = PaddingValues(Dimens.PaddingMedium),
                verticalArrangement = Arrangement.spacedBy(Dimens.ListItemSpacing),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    Text(
                        text = "Historial de alertas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = Dimens.PaddingSmall)
                    )
                }
                items(sampleAlerts) { alert ->
                    AlertCard(alert)
                }
            }
        }
    }
}

@Composable
fun FilterAlertComponent(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    var selectedWheel by remember { mutableStateOf("Todas") }
    val wheels = listOf("Todas", "Eje 1 Izq", "Eje 1 Der", "Eje 2 Izq", "Eje 2 Der", "Remolque")

    Surface(
        modifier = modifier
            .padding(horizontal = Dimens.PaddingMedium)
            .fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(Dimens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
        ) {
            Text(
                text = "Filtrar por rueda",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedWheel,
                    onValueChange = {},
                    readOnly = true,
                    enabled = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true },
                    trailingIcon = {
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.clickable { expanded = true }
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                        disabledBorderColor = Color.LightGray.copy(alpha = 0.5f),
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                )

                // Este Box transparente cubre el TextField para detectar el click
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { expanded = true }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    wheels.forEach { wheel ->
                        DropdownMenuItem(
                            text = { Text(wheel) },
                            onClick = {
                                selectedWheel = wheel
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

val sampleAlerts = listOf(
    AlertUi(
        icon = Icons.Outlined.Warning,
        title = "Presión Crítica - Eje 1 Izq",
        detailLabel = "Presión:",
        detailValue = "2.1 bar",
        detailExtra = "(mín. 6.5)",
        status = AlertStatus.CRITICA
    ),
    AlertUi(
        icon = Icons.Outlined.Thermostat,
        title = "Alta Temperatura - Eje 2 Der",
        detailLabel = "Temp:",
        detailValue = "95°C",
        status = AlertStatus.CRITICA
    ),
    AlertUi(
        icon = Icons.Outlined.BatteryAlert,
        title = "Batería Baja Sensor",
        detailLabel = "Nivel:",
        detailValue = "15%",
        status = AlertStatus.PENDIENTE
    ),
    AlertUi(
        icon = Icons.Outlined.GpsFixed,
        title = "Desgaste de Piso Bajo",
        detailLabel = "Profundidad:",
        detailValue = "3.5 mm",
        status = AlertStatus.PENDIENTE
    ),
    AlertUi(
        icon = Icons.Outlined.Warning,
        title = "Fuga Rápida Detectada",
        detailLabel = "Pérdida:",
        detailValue = "0.5 bar/min",
        status = AlertStatus.CRITICA
    ),
    AlertUi(
        icon = Icons.Outlined.Timer,
        title = "Inspección Programada",
        detailLabel = "Vence en:",
        detailValue = "2 días",
        status = AlertStatus.PENDIENTE
    ),
    AlertUi(
        icon = Icons.Outlined.Speed,
        title = "Exceso de Velocidad",
        detailLabel = "Máx:",
        detailValue = "110 km/h",
        status = AlertStatus.PENDIENTE
    ),
    AlertUi(
        icon = Icons.Outlined.Warning,
        title = "Presión Alta - Remolque",
        detailLabel = "Presión:",
        detailValue = "9.2 bar",
        status = AlertStatus.CRITICA
    ),
    AlertUi(
        icon = Icons.Outlined.Thermostat,
        title = "Sobrecalentamiento Frenos",
        detailLabel = "Eje:",
        detailValue = "Trasero",
        status = AlertStatus.CRITICA
    ),
    AlertUi(
        icon = Icons.Outlined.GpsFixed,
        title = "Alineación Requerida",
        detailLabel = "Desviación:",
        detailValue = "Leve",
        status = AlertStatus.PENDIENTE
    )
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AlertsScreenPreview() {
    HombreCamionTheme {
        AlertsScreen(onBack = {})
    }
}
