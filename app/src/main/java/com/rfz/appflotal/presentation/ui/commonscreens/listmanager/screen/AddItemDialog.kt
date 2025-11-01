package com.rfz.appflotal.presentation.ui.commonscreens.listmanager.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun AddItemDialog(
    title: String,
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
                                listOf(Color(0xFF6A5DD9), Color(0xFF8E85FF))
                            )
                        )
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color(0xFF4A3DAD),
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = isEntryValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6A5DD9),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("GUARDAR", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, Color(0xFF6A5DD9)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("CANCELAR", color = Color(0xFF6A5DD9), fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddItemDialogPreview() {
    HombreCamionTheme {
        AddItemDialog(
            title = "Elemento de prueba",
            onConfirm = {},
            onDismiss = {},
            isEntryValid = true,
        )
    }
}