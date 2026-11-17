package com.rfz.appflotal.presentation.ui.services.components

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

private val FieldShape = RoundedCornerShape(12.dp)

@Composable
private fun serviceFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.4f),
    disabledBorderColor = Color.LightGray.copy(alpha = 0.3f),
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color(0xFFF8F9FA),
    disabledContainerColor = Color(0xFFF1F1F4)
)

@Composable
fun FieldLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.secondary,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier.padding(start = 4.dp)
    )
}

/** Campo de texto/numérico editable con etiqueta arriba. */
@Composable
fun ServiceTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1,
    prefix: String? = null,
    suffix: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingExtraSmall)
    ) {
        FieldLabel(label)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            placeholder = { Text(placeholder) },
            prefix = prefix?.let { { Text(it) } },
            suffix = suffix?.let { { Text(it) } },
            isError = isError,
            singleLine = singleLine,
            minLines = minLines,
            shape = FieldShape,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = serviceFieldColors(),
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/** Campo de solo lectura que despliega un menú de opciones al tocarlo. */
@Composable
fun ServiceDropdownField(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = ""
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingExtraSmall)
    ) {
        FieldLabel(label)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(placeholder) },
                shape = FieldShape,
                trailingIcon = {
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                colors = serviceFieldColors(),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.fillMaxWidth()
            )
            // Capa transparente para capturar el click en todo el campo.
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { expanded = true }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelect(index)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

/** Encabezado de sección con icono. */
@Composable
fun ServiceSectionHeader(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        }
        trailing?.invoke()
    }
}

/** Etiqueta arriba, valor abajo (Cantidad/Precio/Total, etc.). */
@Composable
fun InfoPair(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    alignment: Alignment.Horizontal = Alignment.Start
) {
    Column(modifier = modifier, horizontalAlignment = alignment) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.size(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = valueColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/** Diálogo de confirmación para acciones destructivas (eliminar). */
@Composable
fun ConfirmDeleteDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = { Text(message) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = FieldShape
            ) {
                Text(stringResource(R.string.srv_eliminar_btn), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = FieldShape) {
                Text(stringResource(R.string.cancelar))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun ServiceComponentsPreview() {
    HombreCamionTheme {
        Column(
            modifier = Modifier.padding(Dimens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
        ) {
            ServiceDropdownField(
                label = stringResource(R.string.srv_tipo_servicio_label),
                selected = "Preventivo",
                options = listOf("Preventivo", "Correctivo"),
                onSelect = {}
            )
            ServiceTextField(
                label = stringResource(R.string.srv_costo_unitario_label),
                value = "100",
                onValueChange = {},
                prefix = "$",
                keyboardType = KeyboardType.Number
            )
        }
    }
}

/** Odómetro capturado menor al vigente: el formulario no se puede guardar. */
fun isOdometerBelowCurrent(value: Int?, currentOdometer: Int): Boolean =
    value != null && currentOdometer > 0 && value < currentOdometer

/**
 * Campo de odómetro (km) compartido por Combustible y Servicios.
 *
 * - Editable ([readOnlyNote] = null): no acepta un valor menor a [currentOdometer]; abajo
 *   muestra la ayuda con el odómetro vigente, o el error si se escribe uno menor.
 * - Solo lectura ([readOnlyNote] != null): deshabilitado, con la nota debajo (p. ej. al
 *   editar un servicio, para no alterar la trazabilidad).
 */
@Composable
fun OdometerField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    currentOdometer: Int,
    modifier: Modifier = Modifier,
    readOnlyNote: String? = null
) {
    val readOnly = readOnlyNote != null
    val belowCurrent = !readOnly && isOdometerBelowCurrent(value.toIntOrNull(), currentOdometer)

    Column(modifier = modifier) {
        ServiceTextField(
            label = label,
            value = value,
            onValueChange = { onValueChange(it.filter { c -> c.isDigit() }.take(9)) },
            keyboardType = KeyboardType.Number,
            suffix = "km",
            enabled = !readOnly,
            isError = belowCurrent
        )
        val (text, isError) = when {
            readOnly -> readOnlyNote to false
            belowCurrent -> stringResource(R.string.error_odometro_inferior) to true
            currentOdometer > 0 -> stringResource(
                R.string.odometro_actual_ayuda,
                "%,d".format(currentOdometer),
                "km"
            ) to false
            else -> null to false
        }
        if (text != null) {
            Text(
                text = text,
                color = if (isError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}
