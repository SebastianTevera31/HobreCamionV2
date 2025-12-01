package com.rfz.appflotal.presentation.ui.inspection.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.presentation.ui.components.NumberField
import com.rfz.appflotal.presentation.ui.components.SectionHeader
import com.rfz.appflotal.presentation.ui.inspection.viewmodel.InspectionFormState

@Composable
fun InspectionContent(
    modifier: Modifier = Modifier,
    form: InspectionFormState,
    lastOdometer: Int,
    inspectionList: List<CatalogItem> = emptyList(),
    isOdometerEditable: Boolean = true,
    showReportList: Boolean = true
) {
    Column(modifier = modifier) {
        // Reporte
        if (showReportList) {
            ReportDropdown(
                reports = inspectionList,
                selectedId = form.selectedReportId,
                onSelected = { form.selectedReportId = it },
                errorText = form.selectedReportIdError,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(dimensionResource(R.dimen.thin_dimen)))
        }

        // Bloque: Temperatura y Odometro
        SectionHeader(stringResource(R.string.lecturas))

        if (isOdometerEditable) {
            Text(
                text = stringResource(
                    R.string.advertencia_ingreso_odometro,
                    lastOdometer
                ),
                style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                modifier = Modifier.padding(dimensionResource(R.dimen.small_dimen))
            )
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.small_dimen)
            )
        ) {
            NumberField(
                value = form.temperature,
                onValueChange = { form.temperature = it },
                label = stringResource(R.string.temperatura_c),
                errorText = form.temperatureError,
                modifier = Modifier.weight(1f)
            )
            NumberField(
                value = form.odometer,
                onValueChange = { form.odometer = it },
                label = stringResource(R.string.odometro),
                errorText = form.odometerError,
                modifier = Modifier.weight(1f),
                isEditable = isOdometerEditable
            )
        }

        Spacer(Modifier.height(12.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.small_dimen)
            )
        ) {
            NumberField(
                value = form.pressureMeasured,
                onValueChange = { form.pressureMeasured = it },
                label = stringResource(R.string.presion_medida),
                style = MaterialTheme.typography.bodyMedium,
                errorText = form.pressureMeasuredError,
                modifier = Modifier.weight(1f)
            )

            NumberField(
                value = form.adjustedPressure,
                onValueChange = { form.adjustedPressure = it },
                style = MaterialTheme.typography.bodyMedium,
                label = stringResource(R.string.presion_ajustada),
                errorText = form.adjustedPressureError,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(12.dp))

        // Bloque: Profundidad de piso
        SectionHeader(stringResource(R.string.profundidad_de_piso_mm))
        Spacer(Modifier.height(8.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(id = R.dimen.small_dimen)
                )
            ) {
                NumberField(
                    value = form.treadDepth1,
                    onValueChange = { form.treadDepth1 = it },
                    label = "T1",
                    errorText = form.treadDepth1Error,
                    modifier = Modifier
                        .widthIn(min = 220.dp)
                        .heightIn(min = 56.dp)
                        .weight(1f),
                    keyboardType = KeyboardType.Number
                )

                NumberField(
                    value = form.treadDepth2,
                    onValueChange = { form.treadDepth2 = it },
                    label = "T2",
                    errorText = form.treadDepth2Error,
                    modifier = Modifier
                        .widthIn(min = 220.dp)
                        .heightIn(min = 56.dp)
                        .weight(1f),
                    keyboardType = KeyboardType.Number
                )

                NumberField(
                    value = form.treadDepth3,
                    onValueChange = { form.treadDepth3 = it },
                    label = "T3",
                    errorText = form.treadDepth3Error,
                    modifier = Modifier
                        .widthIn(min = 220.dp)
                        .heightIn(min = 56.dp)
                        .weight(1f),
                    keyboardType = KeyboardType.Number
                )

                NumberField(
                    value = form.treadDepth4,
                    onValueChange = { form.treadDepth4 = it },
                    label = "T4",
                    errorText = form.treadDepth4Error,
                    modifier = Modifier
                        .widthIn(min = 220.dp)
                        .heightIn(min = 56.dp)
                        .weight(1f),
                    keyboardType = KeyboardType.Number
                )
            }
            if (form.oneTreadDepthAtLeast != null) {
                Text(
                    text = stringResource(R.string.una_medidad_profundidad_mayor_0),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Image(
                painterResource(R.drawable.tire_tread_diagram),
                contentDescription = null,
                modifier = Modifier.height(140.dp)
            )
        }

        Spacer(Modifier.height(80.dp))
    }
}