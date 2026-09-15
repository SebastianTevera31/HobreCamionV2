package com.rfz.appflotal.presentation.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.theme.primaryLight
import com.rfz.appflotal.presentation.theme.secondaryLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Campo de fecha de solo lectura que abre un [DatePickerDialog] al tocarlo.
 * Sigue el mismo estilo visual que [FormTextField].
 *
 * @param value fecha ya formateada para mostrar (cadena vacia = sin seleccion)
 * @param onDateSelected devuelve la fecha elegida formateada segun [pattern]
 * @param pattern formato de salida; por defecto "dd/MM/yyyy"
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormDateField(
    @StringRes title: Int,
    value: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    isEditable: Boolean = true,
    pattern: String = "dd/MM/yyyy",
    brandColor: Color = primaryLight,
    darkerGray: Color = secondaryLight
) {
    var showDialog by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Abrir el dialogo cuando se toca el campo (que es de solo lectura)
    LaunchedEffect(isPressed) {
        if (isPressed && isEditable) showDialog = true
    }

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            enabled = isEditable,
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            interactionSource = interactionSource,
            label = {
                Text(
                    stringResource(title),
                    color = darkerGray.copy(alpha = 0.8f)
                )
            },
            singleLine = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = stringResource(title),
                    tint = brandColor
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedBorderColor = brandColor,
                unfocusedBorderColor = brandColor,
                focusedLabelColor = brandColor,
                cursorColor = brandColor,
                focusedTextColor = Color.DarkGray,
                unfocusedTextColor = Color.DarkGray,
                disabledTextColor = Color.DarkGray
            )
        )

        // Capa transparente que captura el toque (el TextField readOnly no emite click)
        if (isEditable) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { showDialog = true }
            )
        }
    }

    if (showDialog) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val sdf = SimpleDateFormat(pattern, Locale.getDefault())
                            // El picker devuelve la fecha en UTC a medianoche
                            sdf.timeZone = TimeZone.getTimeZone("UTC")
                            onDateSelected(sdf.format(Date(millis)))
                        }
                        showDialog = false
                    }
                ) {
                    Text(stringResource(R.string.confirmar), color = brandColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.cancelar), color = brandColor)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FormDateFieldPreview() {
    HombreCamionTheme {
        FormDateField(
            title = R.string.fecha,
            value = "10/08/2026",
            onDateSelected = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FormDateFieldEmptyPreview() {
    HombreCamionTheme {
        FormDateField(
            title = R.string.fecha_de_adquisicion,
            value = "",
            onDateSelected = {}
        )
    }
}