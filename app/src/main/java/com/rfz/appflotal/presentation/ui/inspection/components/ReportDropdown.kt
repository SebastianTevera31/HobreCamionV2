package com.rfz.appflotal.presentation.ui.inspection.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.data.model.CatalogItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDropdown(
    reports: List<CatalogItem>,
    selectedId: String?,
    errorText: Int?,
    onSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Reporte"
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = reports.firstOrNull { it.id.toString() == selectedId }

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = modifier
        ) {
            OutlinedTextField(
                value = selected?.description ?: "",
                onValueChange = {},
                readOnly = true,
                isError = selected == null,
                label = { Text(label) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6A5DD9),
                    unfocusedBorderColor = Color(0xFFAAAAAA),
                    focusedLabelColor = Color(0xFF6A5DD9),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                shape = RoundedCornerShape(14.dp),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Ninguno") },
                    onClick = {
                        onSelected(null)
                        expanded = false
                    }
                )
                reports.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                item.description,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        onClick = {
                            onSelected(item.id.toString())
                            expanded = false
                        }
                    )
                }
            }
        }
        if (errorText != null) {
            Text(
                text = stringResource(errorText),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}