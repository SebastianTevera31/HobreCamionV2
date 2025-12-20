package com.rfz.appflotal.presentation.ui.monitor.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.rfz.appflotal.data.model.tpms.MonitorTireByDateResponse
import com.rfz.appflotal.presentation.theme.primaryLight

@Composable
fun CurrentPositionDataView(
    @StringRes message: Int,
    sensorDataList: List<MonitorTireByDateResponse>?,
    isOnSearch: Boolean,
    pressureUnit: String,
    temperatureUnit: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 16.dp,
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 48.dp),
        color = Color.White
    ) {
        if (sensorDataList != null) {
            if (sensorDataList.isNotEmpty()) {
                LazyColumn(
                    modifier = modifier.fillMaxSize(),
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
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = stringResource(R.string.fecha),
                                style = MaterialTheme.typography.titleSmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(2f)
                            )
                            Text(
                                text = pressureUnit, style = MaterialTheme.typography.titleSmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = stringResource(R.string.temperatura),
                                style = MaterialTheme.typography.titleSmall,
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    if (sensorDataList.isNotEmpty()) {
                        itemsIndexed(
                            items = sensorDataList
                        ) { index, data ->
                            val color = if (index % 2 == 0) Color(0x402E3192) else Color.White
                            SensorDataRow(
                                position = data.tirePosition,
                                fecha = if (isOnSearch) convertDate(
                                    date = data.sensorDate,
                                    convertFormat = "HH:mm:ss"
                                ) else convertDate(data.sensorDate),
                                psi = data.psi,
                                temperatura = "%s $temperatureUnit".format(data.temperature.toString()),
                                color = color
                            )
                        }
                    } else {
                        item { NoPositionDataView(message) }
                    }
                }
            } else {
                NoPositionDataView(R.string.no_se_encontraron_resultados)
            }
        } else {
            NoPositionDataView(R.string.no_se_encontraron_resultados)
        }
    }
}

@Composable
fun SensorDataRow(
    position: String,
    fecha: String,
    psi: Int,
    temperatura: String,
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
            modifier = Modifier.weight(1f)
        )
        Text(
            text = fecha, style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = "$psi", style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = temperatura, style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun NoPositionDataView(@StringRes message: Int, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(message),
            modifier = Modifier.align(Alignment.Center),
            color = primaryLight
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewPositionMonitorScreen() {
    val mockSensorDataList = listOf(
        MonitorTireByDateResponse(
            tirePosition = "P1",
            tireNumber = "TIRE-001",
            sensorDate = "2025-12-18T10:30:00",
            psi = 34,
            temperature = 28
        ),
        MonitorTireByDateResponse(
            tirePosition = "P1",
            tireNumber = "TIRE-001",
            sensorDate = "2025-12-18T10:30:00",
            psi = 34,
            temperature = 28
        ),
        MonitorTireByDateResponse(
            tirePosition = "P1",
            tireNumber = "TIRE-001",
            sensorDate = "2025-12-18T10:30:00",
            psi = 34,
            temperature = 28
        )
    )

    CurrentPositionDataView(
        message = R.string.no_se_encontraron_resultados,
        sensorDataList = mockSensorDataList,
        isOnSearch = false,
        pressureUnit = "psi",
        temperatureUnit = "C"
    )
}