package com.rfz.appflotal.presentation.ui.alerts.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

enum class AlertType(@StringRes val title: Int) {
    ALL(R.string.todas),
    PRESSURE(R.string.presion),
    TEMPERATURE(R.string.temperatura),
}

@Composable
fun BaseFilterField(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    placeholder: String = ""
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingExtraSmall)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold
            )
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(placeholder) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.3f),
                    disabledBorderColor = Color.LightGray.copy(alpha = 0.3f),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color(0xFFF8F9FA)
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            )
            // Capa invisible para detectar click en todo el campo
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { onClick() }
            )
        }
    }
}

@Composable
fun TireFilterField(
    selectedWheel: String,
    wheels: List<String>,
    onSelectedWheel: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        BaseFilterField(
            label = "Rueda",
            value = selectedWheel.ifEmpty { "Todas" },
            icon = Icons.Default.RadioButtonChecked,
            onClick = { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            wheels.forEach { wheel ->
                DropdownMenuItem(
                    text = { Text(wheel) },
                    onClick = {
                        onSelectedWheel(wheel)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AlertTypeFilterField(
    selectedAlert: AlertType?,
    onSelectAlert: (AlertType) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        BaseFilterField(
            label = "Alerta",
            value = stringResource(selectedAlert?.title ?: AlertType.ALL.title),
            icon = Icons.Default.NotificationsActive,
            onClick = { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            AlertType.entries.forEach { alert ->
                DropdownMenuItem(
                    text = { Text(stringResource(alert.title)) },
                    onClick = {
                        onSelectAlert(alert)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun DateFilterField(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    pattern: String = "dd/MM/yyyy"
) {
    var showDialog by remember { mutableStateOf(false) }

    BaseFilterField(
        label = "Fecha",
        value = selectedDate.ifEmpty { "Seleccionar fecha" },
        icon = Icons.Default.CalendarMonth,
        onClick = { showDialog = true },
        modifier = modifier
    )

    if (showDialog) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val sdf = SimpleDateFormat(pattern, Locale.getDefault())
                            sdf.timeZone = TimeZone.getTimeZone("UTC")
                            onDateSelected(sdf.format(Date(millis)))
                        }
                        showDialog = false
                    }
                ) {
                    Text(stringResource(R.string.confirmar))
                }
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

@Preview(showBackground = true)
@Composable
fun AlertFilterComponentsPreview() {
    HombreCamionTheme {
        Column(
            modifier = Modifier.padding(Dimens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
        ) {
            TireFilterField(
                selectedWheel = "Eje 1 Izq",
                wheels = listOf("Todas", "Eje 1 Izq", "Eje 1 Der"),
                onSelectedWheel = {}
            )
            AlertTypeFilterField(
                selectedAlert = AlertType.PRESSURE,
                onSelectAlert = {}
            )
            DateFilterField(
                selectedDate = "10/08/2026",
                onDateSelected = {}
            )
        }
    }
}
