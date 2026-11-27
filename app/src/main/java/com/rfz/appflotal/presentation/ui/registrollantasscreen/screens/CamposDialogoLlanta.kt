package com.rfz.appflotal.presentation.ui.registrollantasscreen.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.ui.commonscreens.listmanager.screen.ItemDialog
import com.rfz.appflotal.presentation.ui.languaje.LocalizedApp
import com.rfz.appflotal.presentation.ui.utils.datePickerMillisToIsoDate
import com.rfz.appflotal.presentation.ui.utils.isoDateToDatePickerMillis

// Campos del antiguo diálogo de registro; los siguen usando RegisterTireScreen y EditMountedTireScreen.

@Composable
fun DialogTextField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isEditable: Boolean = true,
    warningMessage: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit,
) {
    Column {
        ItemDialog(
            label = label,
            value = value,
            isEmpty = false,
            isEditable = isEditable,
            onValueChange = onValueChange,
            modifier = modifier,
            keyboardType = keyboardType
        )
        if (warningMessage.isNotEmpty()) {
            Text(
                text = warningMessage,
                color = Color.Red,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    selectedDate: String,
    enabled: Boolean = true,
    onDateSelected: (String) -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = selectedDate.substringBefore('T'),
        onValueChange = {}, // No-op
        readOnly = true,
        enabled = enabled,
        label = { Text(stringResource(R.string.fecha_de_adquisicion)) },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = stringResource(R.string.seleccionar_fecha),
                modifier = Modifier.clickable(enabled = enabled) {
                    showDatePicker = true
                    focusManager.clearFocus()
                }
            )
        },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )

    if (showDatePicker && enabled) {
        LocalizedApp {
            // El DatePicker usa la medianoche UTC del día; convertirlo con la zona del
            // dispositivo daba el día anterior (ver DatePickerDates).
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = isoDateToDatePickerMillis(selectedDate)
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                onDateSelected(datePickerMillisToIsoDate(millis))
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text(stringResource(R.string.guardar))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                    }) {
                        Text(stringResource(R.string.cancelar))
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}
