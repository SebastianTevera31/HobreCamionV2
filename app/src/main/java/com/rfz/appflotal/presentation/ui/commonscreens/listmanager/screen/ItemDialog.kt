package com.rfz.appflotal.presentation.ui.commonscreens.listmanager.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun ItemDialog(
    label: String,
    value: String,
    isEmpty: Boolean,
    modifier: Modifier = Modifier,
    isEditable: Boolean = true,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it) },
            label = {
                Text(
                    label, color = Color.Gray
                )
            },
            isError = isEmpty,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6A5DD9),
                unfocusedBorderColor = Color(0xFFAAAAAA),
                focusedLabelColor = Color(0xFF6A5DD9),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions().copy(
                keyboardType = keyboardType
            ),
            enabled = isEditable
        )

        if (isEmpty) {
            Text(
                stringResource(R.string.es_requerido_message, label),
                color = Color.Red,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ItemDialogPreview() {
    HombreCamionTheme {
        ItemDialog(
            "Nombre",
            "",
            isEmpty = false,
            onValueChange = {},
        )
    }
}

