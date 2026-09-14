package com.rfz.appflotal.presentation.ui.services

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
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
import com.rfz.appflotal.presentation.ui.services.model.sampleServiceCatalog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Datos para crear una orden de servicio (ServiceOrderDto en construcción). */
data class NewOrderData(
    val vehicleId: Int?,
    val odometer: String,
    val registrationDate: String,
    val notes: String
)

/**
 * Pantalla para crear una nueva orden de servicio. No existía en los mockups pero es
 * necesaria: doCrudServiceOrder requiere vehículo, odómetro y fecha de registro.
 */
@Composable
fun NewServiceOrderScreen(
    vehicles: List<CatalogItemUi>,
    onBack: () -> Unit,
    onSubmit: (NewOrderData) -> Unit,
    modifier: Modifier = Modifier,
    preselectedVehicleId: Int? = null
) {
    var vehicleId by remember { mutableStateOf(preselectedVehicleId) }
    var odometer by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val vehicleName = vehicles.firstOrNull { it.id == vehicleId }?.name ?: ""
    val isValid = vehicleId != null && odometer.toIntOrNull() != null && date.isNotBlank()

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SimpleTopBar(
                title = stringResource(R.string.srv_nueva_orden_title),
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
            ServiceTextField(
                label = stringResource(R.string.srv_odometro_label),
                value = odometer,
                onValueChange = { odometer = it.filter { c -> c.isDigit() } },
                keyboardType = KeyboardType.Number
            )

            OrderDateField(
                label = stringResource(R.string.srv_orden_apertura),
                value = date,
                onDateSelected = { date = it }
            )

            ServiceTextField(
                label = stringResource(R.string.srv_notas_label),
                value = notes,
                onValueChange = { notes = it },
                placeholder = stringResource(R.string.srv_notas_placeholder),
                singleLine = false,
                minLines = 3
            )

            Spacer(modifier = Modifier.size(Dimens.PaddingSmall))

            Button(
                onClick = {
                    onSubmit(
                        NewOrderData(
                            vehicleId = vehicleId,
                            odometer = odometer,
                            registrationDate = date,
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
                Text(stringResource(R.string.srv_crear_orden_btn), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun OrderDateField(
    label: String,
    value: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        ServiceTextField(
            label = label,
            value = value,
            onValueChange = {},
            placeholder = stringResource(R.string.seleccionar_fecha),
            enabled = false
        )
        // Capa transparente para abrir el selector al tocar el campo.
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showDialog = true }
        )
    }

    if (showDialog) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        sdf.timeZone = TimeZone.getTimeZone("UTC")
                        onDateSelected(sdf.format(Date(millis)))
                    }
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NewServiceOrderPreview() {
    HombreCamionTheme {
        NewServiceOrderScreen(
            vehicles = sampleServiceCatalog,
            onBack = {},
            onSubmit = {}
        )
    }
}
