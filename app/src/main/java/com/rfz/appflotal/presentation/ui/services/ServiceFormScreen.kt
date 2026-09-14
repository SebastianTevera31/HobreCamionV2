package com.rfz.appflotal.presentation.ui.services

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.rfz.appflotal.presentation.ui.services.model.ServiceItemUi
import com.rfz.appflotal.presentation.ui.services.model.sampleOccurrenceTypes
import com.rfz.appflotal.presentation.ui.services.model.sampleProviders
import com.rfz.appflotal.presentation.ui.services.model.sampleServiceCatalog

enum class ServiceFormMode { CREATE, EDIT }

/** Estado editable del formulario de un servicio (ServiceDetailDto en construcción). */
data class ServiceFormData(
    val serviceId: Int? = null,
    val providerId: Int? = null,
    val occurrenceTypeId: Int? = null,
    val unitCost: String = "",
    val quantity: String = "",
    val notes: String = ""
)

/**
 * Formulario compartido para "Nuevo servicio" (CREATE) y "Detalles de servicio" (EDIT).
 * Los campos Proveedor y Tipo de ocurrencia se agregan porque el ServiceDetailDto los
 * exige y en el mockup no aparecían. El total se calcula en vivo (cantidad × costo).
 */
@Composable
fun ServiceFormScreen(
    mode: ServiceFormMode,
    services: List<CatalogItemUi>,
    providers: List<CatalogItemUi>,
    occurrenceTypes: List<CatalogItemUi>,
    onBack: () -> Unit,
    onSubmit: (ServiceFormData) -> Unit,
    modifier: Modifier = Modifier,
    initial: ServiceItemUi? = null
) {
    var serviceId by remember { mutableStateOf(initial?.serviceId) }
    var providerName by remember { mutableStateOf(initial?.provider ?: "") }
    var providerId by remember { mutableStateOf(providers.firstOrNull { it.name == initial?.provider }?.id) }
    var occurrenceName by remember { mutableStateOf(initial?.occurrenceType ?: "") }
    var occurrenceId by remember {
        mutableStateOf(occurrenceTypes.firstOrNull { it.name == initial?.occurrenceType }?.id)
    }
    var unitCost by remember {
        mutableStateOf(initial?.unitCost?.let { formatInput(it) } ?: "")
    }
    var quantity by remember {
        mutableStateOf(initial?.quantity?.let { formatInput(it) } ?: "")
    }
    var notes by remember { mutableStateOf(initial?.notes ?: "") }

    val serviceName = services.firstOrNull { it.id == serviceId }?.name ?: ""
    val total = (unitCost.toDoubleOrNull() ?: 0.0) * (quantity.toDoubleOrNull() ?: 0.0)
    val isValid = serviceId != null && unitCost.toDoubleOrNull() != null &&
            (quantity.toDoubleOrNull() ?: 0.0) > 0.0

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
                label = stringResource(R.string.srv_servicio_label),
                selected = serviceName,
                placeholder = stringResource(R.string.srv_servicio_seleccionar),
                options = services.map { it.name },
                onSelect = { serviceId = services[it].id }
            )

            ServiceDropdownField(
                label = stringResource(R.string.srv_proveedor_label),
                selected = providerName,
                placeholder = stringResource(R.string.srv_servicio_seleccionar),
                options = providers.map { it.name },
                onSelect = { providerName = providers[it].name; providerId = providers[it].id }
            )

            ServiceDropdownField(
                label = stringResource(R.string.srv_tipo_ocurrencia_label),
                selected = occurrenceName,
                placeholder = stringResource(R.string.srv_servicio_seleccionar),
                options = occurrenceTypes.map { it.name },
                onSelect = {
                    occurrenceName = occurrenceTypes[it].name
                    occurrenceId = occurrenceTypes[it].id
                }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)) {
                ServiceTextField(
                    label = stringResource(R.string.srv_costo_unitario_label),
                    value = unitCost,
                    onValueChange = { unitCost = it.filter { c -> c.isDigit() || c == '.' } },
                    prefix = "$",
                    keyboardType = KeyboardType.Decimal,
                    modifier = Modifier.weight(1f)
                )
                ServiceTextField(
                    label = stringResource(R.string.srv_cantidad_label),
                    value = quantity,
                    onValueChange = { quantity = it.filter { c -> c.isDigit() || c == '.' } },
                    keyboardType = KeyboardType.Decimal,
                    modifier = Modifier.weight(1f)
                )
            }

            ServiceTextField(
                label = stringResource(R.string.srv_total_label),
                value = "$%,.2f".format(total),
                onValueChange = {},
                enabled = false
            )

            ServiceTextField(
                label = stringResource(R.string.srv_descripcion_label),
                value = notes,
                onValueChange = { notes = it },
                placeholder = stringResource(R.string.srv_descripcion_placeholder),
                singleLine = false,
                minLines = 3
            )

            Spacer(modifier = Modifier.size(Dimens.PaddingSmall))

            Button(
                onClick = {
                    onSubmit(
                        ServiceFormData(
                            serviceId = serviceId,
                            providerId = providerId,
                            occurrenceTypeId = occurrenceId,
                            unitCost = unitCost,
                            quantity = quantity,
                            notes = notes
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

private fun formatInput(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ServiceFormCreatePreview() {
    HombreCamionTheme {
        ServiceFormScreen(
            mode = ServiceFormMode.CREATE,
            services = sampleServiceCatalog,
            providers = sampleProviders,
            occurrenceTypes = sampleOccurrenceTypes,
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
            services = sampleServiceCatalog,
            providers = sampleProviders,
            occurrenceTypes = sampleOccurrenceTypes,
            onBack = {},
            onSubmit = {},
            initial = com.rfz.appflotal.presentation.ui.services.model.sampleServiceItems.first()
        )
    }
}
