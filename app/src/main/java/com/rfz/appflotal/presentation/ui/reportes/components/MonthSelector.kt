package com.rfz.appflotal.presentation.ui.reportes.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthSelector(
    months: List<String>,
    selectedMonth: String?,
    onMonthSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedMonth ?: "",
            onValueChange = {},
            readOnly = true,
            label = {
                Text("Mes")
            },
            placeholder = {
                Text("Selecciona un mes")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = null
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            months.forEach { month ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = month,
                            fontWeight = if (month == selectedMonth) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            }
                        )
                    },
                    leadingIcon = {
                        if (month == selectedMonth) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    onClick = {
                        expanded = false
                        onMonthSelected(month)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MonthSelectorPreview() {
    HombreCamionTheme {
        MonthSelector(
            months = listOf("Enero", "Febrero", "Marzo", "Abril"),
            selectedMonth = "Enero",
            onMonthSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MonthSelectorUnselectedPreview() {
    HombreCamionTheme {
        MonthSelector(
            months = listOf("Enero", "Febrero", "Marzo", "Abril"),
            selectedMonth = null,
            onMonthSelected = {}
        )
    }
}