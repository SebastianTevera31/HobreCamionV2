package com.rfz.appflotal.presentation.ui.commonscreens.listmanager.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.languaje.LocalizedApp

@Composable
fun AddItemDialog(
    title: String,
    content: @Composable () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isEntryValid: Boolean,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        tonalElevation = 12.dp,
        title = {
            LocalizedApp {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            )
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        text = {
            LocalizedApp {
                LazyColumn {
                    item {
                        content()
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = isEntryValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                LocalizedApp {
                    Text(stringResource(R.string.save).uppercase(), fontWeight = FontWeight.Bold)
                }

            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(14.dp)
            ) {
                LocalizedApp {
                    Text(
                        stringResource(R.string.cancelar).uppercase(),
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddItemDialogPreview2() {
    HombreCamionTheme {
        AddItemDialog(
            title = "Elemento de prueba",
            content = {
                OutlinedTextField(
                    value = "newBrandName",
                    onValueChange = { },
                    label = {
                        Text(
                            "Nombre de la marca",
                            color = Color.Gray
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6A5DD9),
                        unfocusedBorderColor = Color(0xFFAAAAAA),
                        focusedLabelColor = Color(0xFF6A5DD9),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
                if (true) {
                    Text(
                        "El nombre es requerido",
                        color = Color.Red,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            },
            onConfirm = {},
            onDismiss = {},
            isEntryValid = true,
        )

    }
}