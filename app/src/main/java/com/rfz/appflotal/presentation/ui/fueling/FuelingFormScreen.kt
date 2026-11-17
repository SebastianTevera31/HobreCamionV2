package com.rfz.appflotal.presentation.ui.fueling

import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.unit.sp
import com.rfz.appflotal.presentation.commons.ImagePickerField
import com.rfz.appflotal.presentation.ui.fueling.model.TankLevel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.Commons
import com.rfz.appflotal.presentation.commons.RequiresInternetNotice
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.fueling.model.FuelingFormData
import com.rfz.appflotal.presentation.ui.fueling.model.sampleFuelTypes
import com.rfz.appflotal.presentation.ui.languaje.LocalizedApp
import com.rfz.appflotal.presentation.ui.services.components.FieldLabel
import com.rfz.appflotal.presentation.ui.services.components.OdometerField
import com.rfz.appflotal.presentation.ui.services.components.isOdometerBelowCurrent
import com.rfz.appflotal.presentation.ui.services.components.ServiceDropdownField
import com.rfz.appflotal.presentation.ui.services.components.ServiceTextField
import com.rfz.appflotal.presentation.ui.services.model.CatalogItemUi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date
import java.util.TimeZone

/**
 * Formulario para registrar una carga de combustible (solo alta; el backend no permite editar).
 *
 * El odómetro inicia con el último registrado del vehículo. La ubicación la agrega el
 * repositorio al guardar, no se captura aquí.
 */
@Composable
fun FuelingFormScreen(
    fuelTypes: List<CatalogItemUi>,
    lastOdometer: Int,
    onBack: () -> Unit,
    onSubmit: (FuelingFormData) -> Unit,
    modifier: Modifier = Modifier,
    fuelTypesFailed: Boolean = false,
    onRetryFuelTypes: () -> Unit = {},
    isSaving: Boolean = false,
    isOffline: Boolean = false
) {
    var fuelTypeId by rememberSaveable { mutableStateOf<Int?>(null) }
    var units by rememberSaveable { mutableStateOf("") }
    var isLiter by rememberSaveable { mutableStateOf(true) }
    var amount by rememberSaveable { mutableStateOf("") }
    var odometer by rememberSaveable(lastOdometer) {
        mutableStateOf(lastOdometer.takeIf { it > 0 }?.toString() ?: "")
    }
    var station by rememberSaveable { mutableStateOf("") }
    var dateMillis by rememberSaveable { mutableStateOf(todayUtcMillis()) }
    var tankLevel by rememberSaveable { mutableStateOf<TankLevel?>(null) }
    var ticket by rememberSaveable { mutableStateOf<Uri?>(null) }

    val fuelTypeName = fuelTypes.firstOrNull { it.id == fuelTypeId }?.name.orEmpty()
    val odometerValue = odometer.toIntOrNull()
    val isValid = fuelTypeId != null &&
            (units.toDoubleOrNull() ?: 0.0) > 0.0 &&
            (amount.toDoubleOrNull() ?: -1.0) >= 0.0 &&
            odometerValue != null && odometerValue > 0 &&
            !isOdometerBelowCurrent(odometerValue, lastOdometer) &&
            tankLevel != null && ticket != null

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SimpleTopBar(
                title = stringResource(R.string.fue_form_title),
                onBack = onBack,
                showBackButton = true
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(Dimens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
        ) {
            if (isOffline) {
                RequiresInternetNotice(message = stringResource(R.string.fue_requiere_internet))
            }

            if (fuelTypesFailed) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.fue_error_catalogo),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onRetryFuelTypes) {
                        Text(stringResource(R.string.reintentar))
                    }
                }
            }

            ServiceDropdownField(
                label = stringResource(R.string.fue_tipo_combustible),
                selected = fuelTypeName,
                placeholder = stringResource(R.string.fue_seleccionar_combustible),
                options = fuelTypes.map { it.name },
                onSelect = { fuelTypeId = fuelTypes[it].id }
            )

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.PaddingExtraSmall)) {
                FieldLabel(stringResource(R.string.fue_unidad))
                VolumeUnitSelector(isLiter = isLiter, onChange = { isLiter = it })
            }

            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)) {
                ServiceTextField(
                    label = stringResource(R.string.fue_cantidad),
                    value = units,
                    onValueChange = { units = it.toDecimalInput() },
                    keyboardType = KeyboardType.Decimal,
                    modifier = Modifier.weight(1f)
                )
                ServiceTextField(
                    label = stringResource(R.string.fue_monto),
                    value = amount,
                    onValueChange = { amount = it.toDecimalInput() },
                    prefix = "$",
                    keyboardType = KeyboardType.Decimal,
                    modifier = Modifier.weight(1f)
                )
            }

            OdometerField(
                label = stringResource(R.string.fue_odometro),
                value = odometer,
                onValueChange = { odometer = it },
                currentOdometer = lastOdometer
            )

            TankLevelSelector(selected = tankLevel, onSelect = { tankLevel = it })

            FuelingDateField(
                label = stringResource(R.string.fue_fecha_carga),
                dateMillis = dateMillis,
                onDateSelected = { dateMillis = it }
            )

            ServiceTextField(
                label = stringResource(R.string.fue_estacion),
                value = station,
                onValueChange = { station = it.take(STATION_MAX_LENGTH) }
            )

            Column {
                ImagePickerField(
                    label = stringResource(R.string.fue_ticket_label),
                    editable = true,
                    newImage = ticket,
                    onImageSelected = { ticket = it },
                    onRemoveNewImage = { ticket = null },
                    photoPrefix = "ticket"
                )
                if (ticket == null) {
                    Text(
                        text = stringResource(R.string.fue_ticket_requerido),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }
            }

            Button(
                onClick = {
                    val typeId = fuelTypeId ?: return@Button
                    val level = tankLevel ?: return@Button
                    val ticketUri = ticket ?: return@Button
                    onSubmit(
                        FuelingFormData(
                            fuelTypeId = typeId,
                            units = units,
                            isLiter = isLiter,
                            amount = amount.ifBlank { "0" },
                            odometer = odometer,
                            dateMillis = dateMillis,
                            station = station,
                            tankPercentage = level.percentage,
                            ticket = ticketUri
                        )
                    )
                },
                enabled = isValid && !isSaving && !isOffline,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text(stringResource(R.string.fue_guardar), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

private const val STATION_MAX_LENGTH = 100

/** Litros / galones; viaja como `isLiter`. */
@Composable
private fun VolumeUnitSelector(isLiter: Boolean, onChange: (Boolean) -> Unit) {
    val options = listOf(
        true to stringResource(R.string.fue_litros),
        false to stringResource(R.string.fue_galones)
    )
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, (value, label) ->
            SegmentedButton(
                selected = isLiter == value,
                onClick = { onChange(value) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
            ) {
                Text(label)
            }
        }
    }
}

/**
 * Nivel del tanque tras la carga (obligatorio): botones rápidos ¼ ½ ¾ Lleno y una barra
 * ilustrativa. Viaja como `fld_percentage` (25, 50, 75 o 100). Sin selección la barra
 * queda vacía.
 */
@Composable
private fun TankLevelSelector(selected: TankLevel?, onSelect: (TankLevel) -> Unit) {
    val fullLabel = stringResource(R.string.fue_tanque_lleno)
    val progress by animateFloatAsState(
        targetValue = (selected?.percentage ?: 0) / 100f,
        label = "tankLevel"
    )

    Column(verticalArrangement = Arrangement.spacedBy(Dimens.PaddingExtraSmall)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FieldLabel(stringResource(R.string.fue_nivel_tanque), modifier = Modifier.weight(1f))
            if (selected != null) {
                Text(
                    text = "${selected.percentage} %",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            TankLevel.entries.forEachIndexed { index, level ->
                SegmentedButton(
                    selected = selected == level,
                    onClick = { onSelect(level) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = TankLevel.entries.size
                    ),
                    // Sin palomita: con 4 opciones angostas le quita espacio al texto.
                    icon = {}
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(level.fraction ?: fullLabel, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${level.percentage} %",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.PaddingExtraSmall)
                .height(8.dp),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            drawStopIndicator = {}
        )
    }
}

/** Campo de solo lectura que abre un selector de fecha; no permite fechas futuras. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FuelingDateField(
    label: String,
    dateMillis: Long,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        ServiceTextField(
            label = label,
            value = formatDisplayDate(dateMillis),
            onValueChange = {},
            enabled = false
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showDialog = true }
        )
    }

    if (showDialog) {
        LocalizedApp {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = dateMillis,
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                        utcTimeMillis <= todayUtcMillis()
                }
            )
            DatePickerDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let(onDateSelected)
                        showDialog = false
                    }) { Text(stringResource(R.string.confirmar)) }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text(stringResource(R.string.cancelar))
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

/** Medianoche UTC del día local de hoy: el formato en que trabaja el DatePicker. */
private fun todayUtcMillis(): Long =
    LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

private fun formatDisplayDate(millis: Long): String {
    val sdf = SimpleDateFormat(Commons.displayDatePattern(), Commons.displayLocale())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date(millis))
}

/** Deja solo dígitos y un punto decimal (la coma se toma como punto), con hasta 2 decimales. */
private fun String.toDecimalInput(): String {
    val normalized = replace(',', '.').filter { it.isDigit() || it == '.' }
    val dot = normalized.indexOf('.')
    if (dot < 0) return normalized.take(9)
    val integer = normalized.substring(0, dot).take(9)
    val decimals = normalized.substring(dot + 1).replace(".", "").take(2)
    return "$integer.$decimals"
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FuelingFormScreenPreview() {
    HombreCamionTheme {
        FuelingFormScreen(
            fuelTypes = sampleFuelTypes,
            lastOdometer = 1200,
            onBack = {},
            onSubmit = {}
        )
    }
}
