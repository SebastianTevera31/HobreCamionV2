package com.rfz.appflotal.presentation.ui.reportes.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthYearPickerField(
    selectedMonth: YearMonth?,
    onMonthSelected: (YearMonth) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Mes"
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    val formatter = remember {
        DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "MX"))
    }

    OutlinedTextField(
        value = selectedMonth?.format(formatter)?.replaceFirstChar {
            it.uppercase()
        } ?: "",
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        placeholder = { Text("Seleccionar mes") },
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        enabled = false
    )

    if (showDialog) {
        MonthYearDatePickerDialog(
            initialMonth = selectedMonth ?: YearMonth.now(),
            onDismiss = { showDialog = false },
            onConfirm = { yearMonth ->
                onMonthSelected(yearMonth)
                showDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthYearDatePickerDialog(
    initialMonth: YearMonth,
    onDismiss: () -> Unit,
    onConfirm: (YearMonth) -> Unit
) {
    val zoneId = ZoneId.systemDefault()

    val initialMillis = remember(initialMonth) {
        initialMonth
            .atDay(1)
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis
                        ?: datePickerState.displayedMonthMillis

                    val yearMonth = selectedMillis.toYearMonth(zoneId)

                    onConfirm(yearMonth)
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            title = {
                Text("Selecciona mes y año")
            },
            headline = {
                Text("Se tomará solo el mes y año")
            },
            showModeToggle = false
        )
    }
}

private fun Long.toYearMonth(zoneId: ZoneId): YearMonth {
    val localDate = Instant
        .ofEpochMilli(this)
        .atZone(zoneId)
        .toLocalDate()

    return YearMonth.of(
        localDate.year,
        localDate.month
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MonthYearPickerFieldPreview() {
    HombreCamionTheme {
        MonthYearPickerField(
            selectedMonth = YearMonth.now(),
            onMonthSelected = {}
        )
    }
}