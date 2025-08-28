package com.rfz.appflotal.presentation.ui.monitor.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.theme.onPrimaryLight
import com.rfz.appflotal.presentation.theme.primaryLight
import com.rfz.appflotal.presentation.theme.secondaryLight
import com.rfz.appflotal.presentation.ui.components.FormTextField
import com.rfz.appflotal.presentation.ui.languaje.LocalizedApp

@Composable
fun MonitorRegisterDialog(
    configurations: Map<Int, String>,
    modifier: Modifier = Modifier,
    showCloseButton: Boolean = false,
    monitorSelected: Pair<Int, String>? = null,
    macValue: String = "",
    onCloseButton: () -> Unit = {},
    onContinueButton: (String, Pair<Int, String>?) -> Unit
) {
    var macAddress by remember { mutableStateOf("") }
    var configurationSelected by remember { mutableStateOf<Pair<Int, String>?>(null) }

    macAddress = macValue
    configurationSelected = monitorSelected

    Dialog(onDismissRequest = {}) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(376.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = onPrimaryLight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp, horizontal = 28.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ingrese datos del monitor",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                val configurations = configurations
                DropDownConfigurationMenu(
                    title = R.string.monitor,
                    values = configurations,
                    defaultOption = configurationSelected?.second ?: ""
                ) {
                    configurationSelected = it
                }
                FormTextField(
                    title = R.string.direcci_n_mac,
                    value = macAddress,
                    onValueChange = { macAddress = it }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (showCloseButton) {
                        Button(
                            onClick = onCloseButton,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .width(120.dp)
                                .weight(1f),
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                        ) {
                            Text(
                                text = stringResource(R.string.cerrar)
                            )
                        }
                    }
                    Button(
                        onClick = {
                            onContinueButton(macAddress, configurationSelected)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .width(120.dp)
                            .weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.save)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DropDownConfigurationMenu(
    @StringRes title: Int,
    values: Map<Int, String>,
    modifier: Modifier = Modifier,
    defaultOption: String = "",
    selectedOption: (Pair<Int, String>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var parentSize by remember { mutableStateOf(Size.Zero) }
    var selectedValue by remember { mutableStateOf("") }

    selectedValue = defaultOption

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .border(width = 1.dp, color = primaryLight, RoundedCornerShape(12.dp))
            .clickable { expanded = true }
            .onGloballyPositioned { coordinates -> // 👈 medir el Box directamente
                parentSize = coordinates.size.toSize()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape = RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedValue.ifEmpty { stringResource(title) },
                color = secondaryLight,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(R.drawable.drop_down_arrow),
                contentDescription = null,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { parentSize.width.toDp() })
        ) {
            values.forEach { value ->
                LocalizedApp {
                    DropdownMenuItem(
                        text = { Text(text = value.value) },
                        onClick = {
                            selectedValue = value.value
                            selectedOption(value.toPair())
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MonitorRegisterDialogPreview() {
    HombreCamionTheme {
        MonitorRegisterDialog(
            configurations = emptyMap(),
            onCloseButton = {},
            onContinueButton = { _, _ -> false })
    }
}