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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import java.text.Normalizer
import java.util.Calendar
import java.util.Locale

// Nombres de mes que puede devolver el endpoint según el idioma del usuario
// ("2025 / Diciembre", "2025 / December"). No deben traducirse: solo se usan
// para empatar con la respuesta del API.
private val API_MONTH_NAMES = listOf(
    listOf("enero", "january"),
    listOf("febrero", "february"),
    listOf("marzo", "march"),
    listOf("abril", "april"),
    listOf("mayo", "may"),
    listOf("junio", "june"),
    listOf("julio", "july"),
    listOf("agosto", "august"),
    listOf("septiembre", "setiembre", "september"),
    listOf("octubre", "october"),
    listOf("noviembre", "november"),
    listOf("diciembre", "december")
)

private val YEAR_REGEX = Regex("""\b\d{4}\b""")
private val WORD_REGEX = Regex("""\p{L}+""")

private fun String.normalizeForMatch(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace(Regex("""\p{Mn}+"""), "")
        .lowercase(Locale.ROOT)

data class MonthYearSelection(
    val month: Int,
    val year: Int
) {
    companion object {
        /** Convierte el campo "mes" del API ("2025 / Diciembre") a mes/año numéricos. */
        fun fromApiFormat(value: String): MonthYearSelection? {
            val year = YEAR_REGEX.find(value)?.value?.toIntOrNull() ?: return null
            val words = WORD_REGEX.findAll(value.normalizeForMatch()).map { it.value }.toList()
            val monthIndex = API_MONTH_NAMES.indexOfFirst { names -> words.any { it in names } }
            if (monthIndex < 0) return null
            return MonthYearSelection(month = monthIndex + 1, year = year)
        }
    }
}

@Composable
fun MonthYearPickerField(
    selectedMonthYear: MonthYearSelection?,
    onMonthYearSelected: (MonthYearSelection) -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(R.string.mes_y_ano),
    placeholder: String = stringResource(R.string.seleccionar_mes),
    // null = sin restricción; lista vacía = no hay periodos con información
    availableDates: List<String>? = null
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
    availableDates: List<String>?,
    onDismiss: () -> Unit,
    onMonthYearSelected: (MonthYearSelection) -> Unit
) {
    val currentYear = remember {
        Calendar.getInstance().get(Calendar.YEAR)
    }

    val availableSelections = remember(availableDates) {
        availableDates.orEmpty().mapNotNull { MonthYearSelection.fromApiFormat(it) }.toSet()
    }
    val availableYears = remember(availableSelections) {
        availableSelections.map { it.year }.toSet()
    }

    val minYear = availableYears.minOrNull() ?: currentYear
    val maxYear = availableYears.maxOrNull() ?: currentYear

    var selectedYear by rememberSaveable(selectedMonthYear) {
        mutableIntStateOf(selectedMonthYear?.year ?: currentYear)
    }

    var showYearPicker by rememberSaveable { mutableStateOf(false) }

    val months = listOf(
        MonthItem(1, stringResource(R.string.enero)),
        MonthItem(2, stringResource(R.string.febrero)),
        MonthItem(3, stringResource(R.string.marzo)),
        MonthItem(4, stringResource(R.string.abril)),
        MonthItem(5, stringResource(R.string.mayo)),
        MonthItem(6, stringResource(R.string.junio)),
        MonthItem(7, stringResource(R.string.julio)),
        MonthItem(8, stringResource(R.string.agosto)),
        MonthItem(9, stringResource(R.string.septiembre)),
        MonthItem(10, stringResource(R.string.octubre)),
        MonthItem(11, stringResource(R.string.noviembre)),
        MonthItem(12, stringResource(R.string.diciembre))
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.seleccionar_mes_y_ano),
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
                        canGoPrevious = availableDates == null || selectedYear > minYear,
                        canGoNext = availableDates == null || selectedYear < maxYear
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(months) { month ->
                            val isAvailable = availableDates == null ||
                                    MonthYearSelection(month.number, selectedYear) in availableSelections

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

                    val canGoPreviousDecade = availableDates == null || startYear > minYear
                    val canGoNextDecade = availableDates == null || (startYear + 12) <= maxYear

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
                            val isAvailable = availableDates == null ||
                                    year in availableYears

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
                Text(stringResource(R.string.cancelar))
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

@Composable
private fun MonthYearSelection.toDisplayText(): String {
    val monthNames = listOf(
        stringResource(R.string.enero),
        stringResource(R.string.febrero),
        stringResource(R.string.marzo),
        stringResource(R.string.abril),
        stringResource(R.string.mayo),
        stringResource(R.string.junio),
        stringResource(R.string.julio),
        stringResource(R.string.agosto),
        stringResource(R.string.septiembre),
        stringResource(R.string.octubre),
        stringResource(R.string.noviembre),
        stringResource(R.string.diciembre)
    )
    val monthName = monthNames.getOrElse(month - 1) { "" }

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