package com.rfz.appflotal.presentation.ui.monitor.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.Commons.convertDate
import com.rfz.appflotal.data.model.tpms.DiagramMonitorResponse
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun PositionMonitorScreen(
    sensorDataList: List<DiagramMonitorResponse>?,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 16.dp,
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            //Cabecera
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pluralStringResource(R.plurals.posicion_tag, 1),
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(2f)
                    )
                    Text(
                        text = pluralStringResource(R.plurals.llanta_tag, 1, ""),
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(2f)
                    )
                    Text(
                        text = stringResource(R.string.fecha),
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(2f)
                    )
                    Text(
                        text = "PSI", style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = stringResource(R.string.temperatura),
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(2f)
                    )
                }
            }

            if (sensorDataList != null) {
                if (sensorDataList.isNotEmpty()) {
                    itemsIndexed(
                        items = sensorDataList, key = { _, it -> it.idDiagramaMonitor }
                    ) { index, data ->
                        val color = if (index % 2 == 0) Color(0x402E3192) else Color.White
                        SensorDataRow(
                            position = data.sensorPosition,
                            llanta = data.tireNumber,
                            fecha = data.ultimalectura,
                            psi = data.psi,
                            temperatura = data.temperature,
                            color = color
                        )
                    }
                } else {
                    item { NoPositionDataView() }
                }
            } else {
                item { NoPositionDataView() }
            }
        }
    }
}

@Composable
fun SensorDataRow(
    position: String,
    llanta: String,
    fecha: String,
    psi: Float,
    temperatura: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(color = color),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = position,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = llanta, style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = convertDate(fecha), style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = "$psi", style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "$temperatura", style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(2f)
        )
    }
}

@Composable
fun NoPositionDataView(modifier: Modifier = Modifier) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = "No se han registrado datos", modifier.fillMaxSize())
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewPositionMonitorScreen() {
    val mockSensorDataList = listOf(
        DiagramMonitorResponse(
            1, 101, "AA:BB:CC:DD:EE:01", 1, "Eje delantero",
            "Izquierda", "Lado izquierdo", 501, "Sensor A",
            1001, "Config estándar", 32.5f, "RR01", 28.0f,
            false, lowPressure = false, highPressure = false, ultimalectura = "2025-08-12'T'10:30:00"
        ),
        DiagramMonitorResponse(
            2,
            102,
            "AA:BB:CC:DD:EE:02",
            1,
            "Eje delantero",
            "Derecha",
            "Lado derecho",
            502,
            "Sensor B",
            1002,
            "Config alta presión",
            36.0f,
            "RR02",
            30.5f,
            highTemperature = false,
            lowPressure = false,
            highPressure = true,
            ultimalectura = "2025-08-12'T'10:35:00"
        ),
        DiagramMonitorResponse(
            3,
            103,
            "AA:BB:CC:DD:EE:03",
            2,
            "Eje trasero",
            "Izquierda",
            "Lado izquierdo",
            503,
            "Sensor C",
            1003,
            "Config alta temperatura",
            29.0f,
            "RR03",
            45.0f,
            highTemperature = true,
            lowPressure = true,
            highPressure = false,
            ultimalectura = "2025-08-12'T'10:40:00"
        )
    )
    HombreCamionTheme {
        PositionMonitorScreen(mockSensorDataList, modifier = Modifier.safeContentPadding())
    }
}