package com.rfz.appflotal.presentation.ui.monitor.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.Commons.addOneDay
import com.rfz.appflotal.core.util.Commons.convertDate
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PositionFilterView(
    numWheels: Int,
    modifier: Modifier = Modifier,
    onGetSensorData: (String, String) -> Unit
) {
    val numWheels = Array(numWheels) { it -> "P${it + 1}" }
    var wheelSelected by remember { mutableStateOf("") }
    var dateSelected by remember { mutableStateOf(getCurrentDate(pattern = "yyyy-MM-dd")) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.buscar_registros),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.background(Color.White),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PositionDatePicker(modifier = Modifier.weight(2f)) {
                dateSelected = it
            }
            WheelSpinner(listOfWheels = numWheels, modifier = Modifier.weight(1f)) {
                wheelSelected = it
            }
            Button(
                onClick = {
                    if (wheelSelected.isNotEmpty() && dateSelected.isNotEmpty()) {
                        onGetSensorData(wheelSelected, dateSelected)
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color("#2E3192".toColorInt())),
                modifier = Modifier
                    .weight(2f)
                    .padding(top = 20.dp)
            ) {
                Text(text = stringResource(R.string.consultar))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PositionDatePicker(modifier: Modifier = Modifier, onSelectDate: (String) -> Unit) {
    var startDate = getCurrentDate(pattern = "dd/MM/yyyy")
    var showDialog by remember { mutableStateOf(false) }
    val state = rememberDatePickerState()

    val millis = state.selectedDateMillis
    if (millis != null) {
        val date = addOneDay(Date(millis))
        startDate = getCurrentDate(date, "dd/MM/yyyy")
    }

    Column(modifier = modifier) {
        Text(text = stringResource(R.string.fecha))
        Row(
            modifier = Modifier
                .height(60.dp)
                .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(16.dp))
                .clickable { showDialog = true },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = stringResource(R.string.seleccionar_fecha),
                modifier = Modifier.padding(start = 8.dp)
            )
            Text(
                text = startDate,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (showDialog) {
            DatePickerDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(onClick = {
                        onSelectDate(
                            convertDate(
                                date = startDate,
                                initialFormat = "dd/MM/yyyy",
                                convertFormat = "yyyy-MM-dd"
                            )
                        )
                        showDialog = false
                    }) { Text(text = stringResource(R.string.confirmar)) }
                },
                dismissButton = {
                    Button(onClick = {
                        showDialog = false
                    }) { Text(text = stringResource(R.string.cancelar)) }
                }
            ) { DatePicker(state = state) }
        }
    }

}

@Composable
fun WheelSpinner(
    listOfWheels: Array<String>,
    modifier: Modifier = Modifier,
    onSelectWheel: (String) -> Unit
) {
    var selectedText by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Text(text = pluralStringResource(R.plurals.llanta_tag, 1, ""))

        Row(
            modifier = Modifier
                .height(60.dp)
                .width(80.dp)
                .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(16.dp))
                .clickable { isExpanded = true },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = selectedText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            listOfWheels.forEach { tire ->
                DropdownMenuItem(
                    text = { Text(text = tire) },
                    onClick = {
                        onSelectWheel(tire.lowercase(Locale.getDefault()))
                        selectedText = tire
                        isExpanded = false
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DatePickerScreenPreview() {
    HombreCamionTheme {
        PositionFilterView(numWheels = 10, onGetSensorData = { _, _ -> })
    }
}