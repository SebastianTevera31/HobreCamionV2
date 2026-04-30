package com.rfz.appflotal.presentation.ui.vialstatus.view

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun VialLocationMenu(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    countryFields: List<CatalogItem>,
    stateFields: List<CatalogItem>,
    selectedCountry: CatalogItem? = null,
    selectedState: CatalogItem? = null,
    onCountryChange: (id: Int) -> Unit,
    onStateChange: (id: Int) -> Unit,
    onSearch: () -> Unit
) {
    val currentOnDismiss by rememberUpdatedState(onDismiss)
    val currentOnSearch by rememberUpdatedState(onSearch)

    val canSearch = selectedCountry != null && selectedState != null

    BackHandler(enabled = visible) {
        currentOnDismiss()
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f))
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
                ) + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .heightIn(max = 620.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            // Consume el click para evitar cerrar al tocar dentro del panel.
                        },
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = 28.dp,
                        bottomEnd = 28.dp
                    ),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.ubicacion),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )

                                Text(
                                    text = stringResource(R.string.selecciona_pais_estado_instruccion),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(
                                onClick = currentOnDismiss
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.cerrar)
                                )
                            }
                        }

                        HorizontalDivider()

                        ConfigSectionTitle(title = stringResource(R.string.pais))

                        DropConfigurationView(
                            title = stringResource(R.string.pais),
                            selectedOption = selectedCountry,
                            onSelectOption = onCountryChange,
                            options = countryFields
                        )

                        ConfigSectionTitle(title = stringResource(R.string.estado))

                        DropConfigurationView(
                            title = stringResource(R.string.estado),
                            selectedOption = selectedState,
                            onSelectOption = onStateChange,
                            options = stateFields
                        )

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            enabled = canSearch,
                            onClick = currentOnSearch,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = stringResource(R.string.buscar_mapa_vial),
                                fontWeight = FontWeight.SemiBold
                            )
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
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.SemiBold
        ),
        color = MaterialTheme.colorScheme.primary
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
    var expanded by rememberSaveable { mutableStateOf(false) }

    val hasOptions = options.isNotEmpty()
    val selectedText = selectedOption?.description.orEmpty()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = it
        },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = {
                Text(text = title)
            },
            placeholder = {
                Text(
                    text = if (hasOptions) stringResource(R.string.selecciona_una_opcion) else stringResource(R.string.sin_elementos)
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = true
                )
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier
                .exposedDropdownSize(matchAnchorWidth = true)
                .heightIn(max = 280.dp)
        ) {
            if (hasOptions) {
                options.forEach { option ->
                    val isSelected = option.id == selectedOption?.id

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option.description,
                                fontWeight = if (isSelected) {
                                    FontWeight.SemiBold
                                } else {
                                    FontWeight.Normal
                                }
                            )
                        },
                        trailingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else {
                            null
                        },
                        onClick = {
                            onSelectOption(option.id)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            } else {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.sin_elementos_disponibles),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    enabled = false,
                    onClick = {}
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VialLocationMenuPreview() {
    HombreCamionTheme {
        VialLocationMenu(
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

