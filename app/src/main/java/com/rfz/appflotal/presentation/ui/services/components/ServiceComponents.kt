package com.rfz.appflotal.presentation.ui.services.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.text.KeyboardOptions
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.services.model.ServiceOrderStatus

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
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.secondary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 4.dp)
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
    enabled: Boolean = true
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

/** Encabezado de sección con icono, al estilo de los mockups ("Órdenes", "Servicios"). */
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

/** Etiqueta arriba, valor abajo (Apertura/Finalizado, Costo/Cantidad/Total, etc.). */
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

/** Pastilla de estado de la orden (Abierta / Finalizada). */
@Composable
fun StatusBadge(status: ServiceOrderStatus, modifier: Modifier = Modifier) {
    val open = status == ServiceOrderStatus.ABIERTA
    val bg = if (open) MaterialTheme.colorScheme.tertiaryContainer else Color(0xFFDDF3E0)
    val fg = if (open) MaterialTheme.colorScheme.onTertiaryContainer else Color(0xFF1F7A34)
    val text = stringResource(
        if (open) R.string.srv_estado_abierta else R.string.srv_estado_finalizada
    )
    Box(
        modifier = modifier
            .background(bg, RoundedCornerShape(50))
            .padding(horizontal = Dimens.PaddingSmall, vertical = 3.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            fontWeight = FontWeight.Bold
        )
    }
}

/** Bottom sheet de acciones sobre una orden (lista y detalle de orden). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceOrderActionsSheet(
    folio: String,
    onEditOrder: () -> Unit,
    onAddServices: () -> Unit,
    onDeleteOrder: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    showAddServices: Boolean = true
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = Dimens.PaddingLarge, vertical = Dimens.PaddingSmall)
        ) {
            Text(
                text = stringResource(R.string.srv_orden_folio, folio),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.size(Dimens.PaddingSmall))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            SheetAction(
                icon = Icons.Outlined.Edit,
                text = stringResource(R.string.srv_accion_modificar_orden),
                onClick = onEditOrder
            )
            if (showAddServices) {
                SheetAction(
                    icon = Icons.Outlined.AddCircleOutline,
                    text = stringResource(R.string.srv_accion_agregar_servicio),
                    onClick = onAddServices
                )
            }
            SheetAction(
                icon = Icons.Outlined.DeleteOutline,
                text = stringResource(R.string.srv_accion_eliminar_orden),
                onClick = onDeleteOrder,
                destructive = true
            )
            Spacer(modifier = Modifier.size(Dimens.PaddingSmall))
        }
    }
}

@Composable
private fun SheetAction(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    destructive: Boolean = false
) {
    val color =
        if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = Dimens.PaddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(Dimens.PaddingMedium))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = color,
            fontWeight = FontWeight.Medium
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
                label = stringResource(R.string.srv_servicio_label),
                selected = "Balanceo",
                options = listOf("Balanceo", "Renovado"),
                onSelect = {}
            )
            ServiceTextField(
                label = stringResource(R.string.srv_costo_unitario_label),
                value = "100",
                onValueChange = {},
                prefix = "$",
                keyboardType = KeyboardType.Number
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)) {
                StatusBadge(ServiceOrderStatus.ABIERTA)
                StatusBadge(ServiceOrderStatus.FINALIZADA)
            }
        }
    }
}
