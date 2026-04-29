package com.rfz.appflotal.presentation.ui.vialstatus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun VialConfigurationMenu(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    countryFields: List<CatalogItem>,
    stateFields: List<CatalogItem>,
    onCountryChange: () -> Unit,
    onStateChange: () -> Unit,
    onSearch: () -> Unit
) {
    val currentOnDismiss by rememberUpdatedState(onDismiss)

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        currentOnDismiss()
                    }
            )

            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> -fullHeight }
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> -fullHeight }
                ) + fadeOut()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .heightIn(max = 620.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {},
                    shape = RoundedCornerShape(
                        bottomStart = 24.dp,
                        bottomEnd = 24.dp,
                        topStart = 16.dp,
                        topEnd = 16.dp
                    ),
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ubicacion",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )

                            TextButton(
                                onClick = onDismiss
                            ) {
                                Text(
                                    text = "Cerrar",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        HorizontalDivider()

                        ConfigSectionTitle("Pais")

                        DropConfigurationView(
                            title = "Pais",
                            selectedOption = null,
                            onSelectOption = {},
                            options = countryFields,
                        )

                        HorizontalDivider()

                        ConfigSectionTitle("Estado")

                        DropConfigurationView(
                            title = "Estado",
                            selectedOption = null,
                            onSelectOption = {},
                            options = stateFields
                        )

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onDismiss
                        ) {
                            Text("Ir")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConfigSectionTitle(
    title: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.SemiBold
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropConfigurationView(
    title: String,
    selectedOption: CatalogItem?,
    onSelectOption: (id: Int) -> Unit,
    options: List<CatalogItem>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        TextField(
            value = selectedOption?.description ?: "",
            onValueChange = {},
            label = {
                if (options.isNotEmpty()) {
                    Text(text = title)
                }
            },
            trailingIcon = {
                val icon = if (expanded) {
                    Icons.Default.ArrowDropDown
                } else Icons.Default.ArrowDropUp
                Icon(imageVector = icon, contentDescription = null)
            },
            enabled = options.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(12.dp))
        )

        if (options.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach {
                    DropdownMenuItem(
                        text = { Text(text = it.description) },
                        onClick = { onSelectOption(it.id) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            DropdownMenuItem(text = {
                Text(text = "Sin elementos")
            }, onClick = {}, modifier = Modifier.fillMaxWidth())
        }
    }
}


@Preview(showBackground = true)
@Composable
fun VialConfigurationMenuPreview() {
    HombreCamionTheme {
        VialConfigurationMenu(
            visible = true,
            onDismiss = {},
            countryFields = emptyList(),
            stateFields = emptyList(),
            onSearch = {},
            onStateChange = {},
            onCountryChange = {}
        )
    }
}

