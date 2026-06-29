package com.rfz.appflotal.presentation.ui.reportes.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Locale

data class MonthYearSelection(
    val month: Int,
    val year: Int
) {
    fun toApiFormat(): String {
        val monthName = DateFormatSymbols(Locale("es", "MX"))
            .months[month - 1]
            .replaceFirstChar { it.uppercaseChar() }
        return "$year / $monthName"
    }
}

@Composable
fun MonthYearPickerField(
    selectedMonthYear: MonthYearSelection?,
    onMonthYearSelected: (MonthYearSelection) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Mes y año",
    placeholder: String = "Seleccionar mes",
    availableDates: List<String> = emptyList()
) {
    var showDialog by rememberSaveable {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedMonthYear?.toDisplayText().orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = {
                Text(label)
            },
            placeholder = {
                Text(placeholder)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    showDialog = true
                }
        )
    }

    if (showDialog) {
        MonthYearPickerDialog(
            selectedMonthYear = selectedMonthYear,
            availableDates = availableDates,
            onDismiss = {
                showDialog = false
            },
            onMonthYearSelected = { value ->
                onMonthYearSelected(value)
                showDialog = false
            }
        )
    }
}

@Composable
private fun MonthYearPickerDialog(
    selectedMonthYear: MonthYearSelection?,
    availableDates: List<String>,
    onDismiss: () -> Unit,
    onMonthYearSelected: (MonthYearSelection) -> Unit
) {
    val currentYear = remember {
        Calendar.getInstance().get(Calendar.YEAR)
    }

    val minYear = remember(availableDates) {
        availableDates.mapNotNull { it.take(4).toIntOrNull() }.minOrNull() ?: currentYear
    }
    val maxYear = remember(availableDates) {
        availableDates.mapNotNull { it.take(4).toIntOrNull() }.maxOrNull() ?: currentYear
    }

    var selectedYear by rememberSaveable(selectedMonthYear) {
        mutableIntStateOf(selectedMonthYear?.year ?: currentYear)
    }

    var showYearPicker by rememberSaveable { mutableStateOf(false) }

    val months = remember {
        listOf(
            MonthItem(1, "Enero"),
            MonthItem(2, "Febrero"),
            MonthItem(3, "Marzo"),
            MonthItem(4, "Abril"),
            MonthItem(5, "Mayo"),
            MonthItem(6, "Junio"),
            MonthItem(7, "Julio"),
            MonthItem(8, "Agosto"),
            MonthItem(9, "Septiembre"),
            MonthItem(10, "Octubre"),
            MonthItem(11, "Noviembre"),
            MonthItem(12, "Diciembre")
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Seleccionar mes y año",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!showYearPicker) {
                    YearSelector(
                        year = selectedYear,
                        onPreviousYear = {
                            selectedYear--
                        },
                        onNextYear = {
                            selectedYear++
                        },
                        onShowYears = { showYearPicker = true },
                        canGoPrevious = availableDates.isEmpty() || selectedYear > minYear,
                        canGoNext = availableDates.isEmpty() || selectedYear < maxYear
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(months) { month ->
                            val isAvailable = availableDates.isEmpty() ||
                                    availableDates.contains(
                                        MonthYearSelection(
                                            month.number,
                                            selectedYear
                                        ).toApiFormat()
                                    )

                            MonthButton(
                                month = month,
                                year = selectedYear,
                                selectedMonthYear = selectedMonthYear,
                                isAvailable = isAvailable,
                                onClick = {
                                    onMonthYearSelected(
                                        MonthYearSelection(
                                            month = month.number,
                                            year = selectedYear
                                        )
                                    )
                                }
                            )
                        }
                    }
                } else {
                    val startYear = (selectedYear / 12) * 12
                    val years = (startYear until startYear + 12).toList()

                    val canGoPreviousDecade = availableDates.isEmpty() || startYear > minYear
                    val canGoNextDecade = availableDates.isEmpty() || (startYear + 12) <= maxYear

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { selectedYear -= 12 },
                            enabled = canGoPreviousDecade
                        ) {
                            Text("‹")
                        }

                        Text(
                            text = "${years.first()} - ${years.last()}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )

                        TextButton(
                            onClick = { selectedYear += 12 },
                            enabled = canGoNextDecade
                        ) {
                            Text("›")
                        }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(years) { year ->
                            val isSelected = year == selectedYear
                            val isAvailable = availableDates.isEmpty() ||
                                    availableDates.any { it.startsWith(year.toString()) }

                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = isAvailable) {
                                        selectedYear = year
                                        showYearPicker = false
                                    },
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = if (isSelected) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else if (!isAvailable) {
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                                ),
                                elevation = CardDefaults.elevatedCardElevation(
                                    defaultElevation = if (isSelected) 4.dp else 1.dp
                                )
                            ) {
                                Text(
                                    text = year.toString(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 14.dp, horizontal = 6.dp),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (isSelected) {
                                            FontWeight.Bold
                                        } else {
                                            FontWeight.Normal
                                        }
                                    ),
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else if (!isAvailable) {
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun YearSelector(
    year: Int,
    onPreviousYear: () -> Unit,
    onNextYear: () -> Unit,
    onShowYears: () -> Unit,
    canGoPrevious: Boolean,
    canGoNext: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = onPreviousYear,
            enabled = canGoPrevious
        ) {
            Text("‹")
        }

        Button(
            onClick = onShowYears,
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 6.dp)
        ) {
            Text(
                text = year.toString(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        TextButton(
            onClick = onNextYear,
            enabled = canGoNext
        ) {
            Text("›")
        }
    }
}

@Composable
private fun MonthButton(
    month: MonthItem,
    year: Int,
    selectedMonthYear: MonthYearSelection?,
    isAvailable: Boolean,
    onClick: () -> Unit
) {
    val isSelected = selectedMonthYear?.month == month.number &&
            selectedMonthYear.year == year

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isAvailable) {
                onClick()
            },
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else if (!isAvailable) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.onSecondaryContainer
            }
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Text(
            text = month.name,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 6.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isSelected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Bold
                }
            ),
            color = if (isSelected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else if (!isAvailable) {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

private data class MonthItem(
    val number: Int,
    val name: String
)

private fun MonthYearSelection.toDisplayText(): String {
    val monthName = DateFormatSymbols(Locale("es", "MX"))
        .months[month - 1]
        .replaceFirstChar { it.uppercaseChar() }

    return "$monthName $year"
}

@Preview(showBackground = true)
@Composable
fun MonthYearPickerFieldPreview() {
    HombreCamionTheme {
        MonthYearPickerField(
            selectedMonthYear = MonthYearSelection(6, 2024),
            onMonthYearSelected = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MonthYearPickerFieldEmptyPreview() {
    HombreCamionTheme {
        MonthYearPickerField(
            selectedMonthYear = null,
            onMonthYearSelected = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MonthYearPickerDialogPreview() {
    HombreCamionTheme {
        MonthYearPickerDialog(
            selectedMonthYear = MonthYearSelection(6, 2024),
            availableDates = listOf("2024 / Junio", "2024 / Julio"),
            onDismiss = {},
            onMonthYearSelected = {}
        )
    }
}