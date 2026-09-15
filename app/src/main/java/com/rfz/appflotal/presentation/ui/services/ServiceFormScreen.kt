package com.rfz.appflotal.presentation.ui.services

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.services.components.ServiceDropdownField
import com.rfz.appflotal.presentation.ui.services.components.ServiceTextField
import com.rfz.appflotal.presentation.ui.services.model.CatalogItemUi
import com.rfz.appflotal.presentation.ui.services.model.ServiceUi
import com.rfz.appflotal.presentation.ui.services.model.sampleServiceTypes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

enum class ServiceFormMode { CREATE, EDIT }

/** Estado editable del formulario de un servicio (mapea a ServiceDetailDto). */
data class ServiceFormData(
    val typeId: Int?,          // id_serviceType
    val description: String,   // fld_description (lo captura el usuario)
    val provider: String,      // fld_provider (lo captura el usuario)
    val price: String,         // fld_price
    val quantity: String,      // fld_cant
    val date: String           // fld_date (ISO 8601)
)

/**
 * Formulario compartido para "Nuevo servicio" (CREATE) y edición (EDIT).
 *
 * Nota: el endpoint de lectura no devuelve proveedor ni fecha, por lo que al
 * editar esos dos campos inician vacíos.
 */
@Composable
fun ServiceFormScreen(
    mode: ServiceFormMode,
    serviceTypes: List<CatalogItemUi>,
    onBack: () -> Unit,
    onSubmit: (ServiceFormData) -> Unit,
    modifier: Modifier = Modifier,
    initial: ServiceUi? = null
) {
    var typeName by remember { mutableStateOf(initial?.type ?: "") }
    var typeId by remember {
        mutableStateOf(serviceTypes.firstOrNull { it.name == initial?.type }?.id)
    }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var provider by remember { mutableStateOf(initial?.provider ?: "") }
    var price by remember { mutableStateOf(initial?.price?.takeIf { it > 0 }?.toString() ?: "") }
    var quantity by remember {
        mutableStateOf(initial?.quantity?.takeIf { it > 0 }?.toString() ?: "")
    }
    var dateMillis by remember { mutableStateOf<Long?>(null) }

    val total = (price.toIntOrNull() ?: 0) * (quantity.toIntOrNull() ?: 0)
    val isValid = typeId != null && description.isNotBlank() &&
            (price.toIntOrNull() ?: 0) > 0 && (quantity.toIntOrNull() ?: 0) > 0 &&
            dateMillis != null

    val title = stringResource(
        if (mode == ServiceFormMode.CREATE) R.string.srv_form_nuevo_title
        else R.string.srv_form_detalle_title
    )
    val submitLabel = stringResource(
        if (mode == ServiceFormMode.CREATE) R.string.srv_agregar_servicio_btn
        else R.string.srv_actualizar_btn
    )

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { SimpleTopBar(title = title, onBack = onBack, showBackButton = true) }
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
            ServiceDropdownField(
                label = stringResource(R.string.srv_tipo_servicio_label),
                selected = typeName,
                placeholder = stringResource(R.string.srv_servicio_seleccionar),
                options = serviceTypes.map { it.name },
                onSelect = {
                    typeName = serviceTypes[it].name
                    typeId = serviceTypes[it].id
                }
            )

            ServiceTextField(
                label = stringResource(R.string.srv_servicio_label),
                value = description,
                onValueChange = { description = it }
            )

            ServiceTextField(
                label = stringResource(R.string.srv_proveedor_label),
                value = provider,
                onValueChange = { provider = it }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)) {
                ServiceTextField(
                    label = stringResource(R.string.srv_costo_unitario_label),
                    value = price,
                    onValueChange = { price = it.filter { c -> c.isDigit() } },
                    prefix = "$",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )

                ServiceTextField(
                    label = stringResource(R.string.srv_cantidad_label),
                    value = quantity,
                    onValueChange = { quantity = it.filter { c -> c.isDigit() } },
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }

            ServiceDateField(
                label = stringResource(R.string.fecha),
                dateMillis = dateMillis,
                onDateSelected = { dateMillis = it }
            )

            ServiceTextField(
                label = stringResource(R.string.srv_total_label),
                value = "$%,d".format(total),
                onValueChange = {},
                enabled = false
            )

            Spacer(modifier = Modifier.size(Dimens.PaddingSmall))

            Button(
                onClick = {
                    onSubmit(
                        ServiceFormData(
                            typeId = typeId,
                            description = description,
                            provider = provider,
                            price = price,
                            quantity = quantity,
                            date = dateMillis.toIsoDate()
                        )
                    )
                },
                enabled = isValid,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(submitLabel, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

/** Campo de solo lectura que abre un selector de fecha. */
@Composable
private fun ServiceDateField(
    label: String,
    dateMillis: Long?,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        ServiceTextField(
            label = label,
            value = dateMillis?.let { formatDisplayDate(it) } ?: "",
            onValueChange = {},
            placeholder = stringResource(R.string.seleccionar_fecha),
            enabled = false
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showDialog = true }
        )
    }

    if (showDialog) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
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

private fun formatDisplayDate(millis: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date(millis))
}

/** Fecha en el formato ISO 8601 UTC que espera el backend (fld_date). */
private fun Long?.toIsoDate(): String {
    if (this == null) return ""
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date(this))
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ServiceFormCreatePreview() {
    HombreCamionTheme {
        ServiceFormScreen(
            mode = ServiceFormMode.CREATE,
            serviceTypes = sampleServiceTypes,
            onBack = {},
            onSubmit = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ServiceFormEditPreview() {
    HombreCamionTheme {
        ServiceFormScreen(
            mode = ServiceFormMode.EDIT,
            serviceTypes = sampleServiceTypes,
            onBack = {},
            onSubmit = {},
            initial = com.rfz.appflotal.presentation.ui.services.model.sampleServices.first()
        )
    }
}
