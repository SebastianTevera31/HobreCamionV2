package com.rfz.appflotal.presentation.ui.services

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.rfz.appflotal.presentation.commons.ImagePickerField
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.Dimens
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.forums.components.CharacterCounter
import com.rfz.appflotal.presentation.ui.forums.components.ForumLimits
import com.rfz.appflotal.presentation.ui.languaje.LocalizedApp
import com.rfz.appflotal.presentation.ui.services.components.FieldLabel
import com.rfz.appflotal.presentation.ui.services.components.OdometerField
import com.rfz.appflotal.presentation.ui.services.components.isOdometerBelowCurrent
import com.rfz.appflotal.presentation.ui.services.components.ServiceDropdownField
import com.rfz.appflotal.presentation.ui.services.components.ServiceTextField
import com.rfz.appflotal.presentation.ui.services.model.CatalogItemUi
import com.rfz.appflotal.presentation.ui.services.model.ServiceUi
import com.rfz.appflotal.presentation.ui.services.model.toEditablePrice
import com.rfz.appflotal.presentation.ui.services.model.toMoney
import com.rfz.appflotal.presentation.ui.services.model.sampleServiceTypes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

enum class ServiceFormMode { CREATE, EDIT }

/** Costo unitario mientras se escribe: dígitos con hasta 2 decimales ("", "12", "12.", "12.5"). */
private val PRICE_INPUT = Regex("""\d*(\.\d{0,2})?""")

/** Estado editable del formulario de un servicio (mapea a ServiceDetailDto). */
data class ServiceFormData(
    val typeId: Int?,          // id_serviceType
    val description: String,   // fld_description (lo captura el usuario)
    val provider: String,      // fld_provider (lo captura el usuario)
    val price: String,         // fld_price
    val quantity: String,      // fld_cant
    val date: String,           // fld_date (ISO 8601)
    val odometer: String,       // fld_odometer (solo dígitos)
    val notas: String,           // fld_notes
    val image: Uri? = null,      // imagen nueva elegida; null = conservar la actual
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
    initial: ServiceUi? = null,
    lastOdometer: Int = 0,
    isSaving: Boolean = false
) {
    // Mientras se guarda no se permite salir: el resultado regresa a este formulario.
    BackHandler(enabled = isSaving) {}
    val guardedBack = { if (!isSaving) onBack() }

    var typeName by remember(initial?.id) { mutableStateOf(initial?.type ?: "") }
    var typeId by remember(initial?.id, serviceTypes) {
        mutableStateOf(
            serviceTypes.firstOrNull {
                it.name.trim().equals(initial?.type?.trim(), ignoreCase = true)
            }?.id ?: serviceTypes.firstOrNull()?.id
        )
    }
    var description by remember(initial?.id) { mutableStateOf(initial?.description ?: "") }
    var provider by remember(initial?.id) { mutableStateOf(initial?.provider ?: "") }
    var price by remember(initial?.id) {
        mutableStateOf(
            initial?.price?.takeIf { it > 0 }?.toEditablePrice() ?: ""
        )
    }
    var quantity by remember(initial?.id) {
        mutableStateOf(initial?.quantity?.takeIf { it > 0 }?.toString() ?: "")
    }
    var dateMillis by remember(initial?.id) {
        mutableStateOf<Long?>(
            initial?.let { s ->
                parseDateToMillis(s.rawDate.ifBlank { s.date })
            }
        )
    }
    // Alta: inicia con el odómetro vigente del vehículo y no acepta uno menor.
    // Edición: solo lectura con el del servicio (vacío si GetServices no lo devuelve;
    // en ese caso se envía 0 mientras el backend lo expone).
    val isEdit = mode == ServiceFormMode.EDIT
    var odometer by remember(initial?.id, lastOdometer) {
        mutableStateOf(
            if (initial != null) initial.odometer?.toString().orEmpty()
            else lastOdometer.takeIf { it > 0 }?.toString().orEmpty()
        )
    }
    var notas by remember(initial?.id) { mutableStateOf(initial?.notes ?: "") }
    var newImage by rememberSaveable(initial?.id) { mutableStateOf<Uri?>(null) }

    val total = (price.toDoubleOrNull() ?: 0.0) * (quantity.toIntOrNull() ?: 0)
    val odometerValue = odometer.toIntOrNull()
    val isOdometerValid = isEdit || (
            odometerValue != null && odometerValue > 0 &&
                    !isOdometerBelowCurrent(odometerValue, lastOdometer))
    val isValid = typeId != null && description.isNotBlank() &&
            (price.toDoubleOrNull() ?: 0.0) > 0 && (quantity.toIntOrNull() ?: 0) > 0 &&
            dateMillis != null && isOdometerValid

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
        topBar = { SimpleTopBar(title = title, onBack = guardedBack, showBackButton = true) }
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
                    // Decimal con hasta 2 dígitos; algunos teclados escriben coma.
                    onValueChange = { input ->
                        val normalized = input.replace(',', '.')
                        if (PRICE_INPUT.matches(normalized)) price = normalized
                    },
                    prefix = "$",
                    keyboardType = KeyboardType.Decimal,
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

            OdometerField(
                label = stringResource(R.string.srv_odometro_label),
                value = odometer,
                onValueChange = { odometer = it },
                currentOdometer = lastOdometer,
                readOnlyNote = if (isEdit) stringResource(R.string.srv_odometro_solo_lectura) else null
            )

            ServiceTextField(
                label = stringResource(R.string.srv_total_label),
                value = total.toMoney(),
                onValueChange = {},
                enabled = false
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FieldLabel(
                    stringResource(R.string.notas),
                    modifier = Modifier.align(Alignment.Start)
                )
                OutlinedTextField(
                    value = notas,
                    onValueChange = { notas = it.take(ForumLimits.TEXT_MAX_LENGTH) },
                    shape = RoundedCornerShape(Dimens.PaddingSmall),
                    placeholder = {
                        Text(
                            text = stringResource(
                                R.string.agregar_notas
                            )
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor =
                            MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor =
                            MaterialTheme.colorScheme.surfaceVariant,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp)
                )
                CharacterCounter(current = notas.length, max = ForumLimits.TEXT_MAX_LENGTH)
            }

            ImagePickerField(
                label = stringResource(R.string.srv_imagen_label),
                editable = mode == ServiceFormMode.CREATE,
                newImage = newImage,
                currentImageUrl = initial?.imageUrl,
                onImageSelected = { newImage = it },
                onRemoveNewImage = { newImage = null },
                photoPrefix = "service"
            )

            Button(
                onClick = {
                    onSubmit(
                        ServiceFormData(
                            typeId = typeId,
                            description = description,
                            provider = provider,
                            price = price,
                            quantity = quantity,
                            date = dateMillis.toIsoDate(),
                            odometer = odometer,
                            notas = notas,
                            image = newImage,
                        )
                    )
                },
                enabled = isValid && !isSaving,
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
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(submitLabel, fontWeight = FontWeight.Bold)
                }
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
        LocalizedApp {
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
}

private fun formatDisplayDate(millis: Long): String {
    val sdf = SimpleDateFormat(Commons.displayDatePattern(), Commons.displayLocale())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date(millis))
}

private fun parseDateToMillis(dateStr: String): Long? {
    if (dateStr.isBlank()) return null
    val patterns = arrayOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd",
        Commons.displayDatePattern(),
        "dd/MM/yyyy",
        "d 'de' MMMM, yyyy",
        "MMMM d, yyyy"
    )
    for (pattern in patterns) {
        try {
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())
            if (pattern.contains("Z") || pattern.contains("T")) {
                sdf.timeZone = TimeZone.getTimeZone("UTC")
            }
            val parsed = sdf.parse(dateStr)
            if (parsed != null) return parsed.time
        } catch (_: Exception) {
            // Continuar con el siguiente patrón
        }
    }
    return null
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
