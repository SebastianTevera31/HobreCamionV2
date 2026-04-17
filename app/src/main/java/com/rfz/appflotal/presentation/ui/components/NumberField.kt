package com.rfz.appflotal.presentation.ui.components

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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.inspection.viewmodel.filterNumericDot

@Composable
fun NumberField(
    value: String,
    onValueChange: (String) -> Unit,
    errorText: Int?,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    label: String = "",
    placeHolderText: String = "",
    isEditable: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.NumberPassword,
    imeAction: ImeAction = ImeAction.Next
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it.filterNumericDot()) },
            placeholder = { Text(placeHolderText) },
            label = {
                Text(
                    text = label,
                    style = style,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            },
            isError = errorText != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            enabled = isEditable,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    if (it.isFocused) {
                        if (value == "0") {
                            onValueChange("")
                        }
                    } else {
                        if (value.isBlank()) {
                            onValueChange("0")
                        }
                    }
                },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6A5DD9),
                unfocusedBorderColor = Color(0xFFAAAAAA),
                focusedLabelColor = Color(0xFF6A5DD9),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            shape = RoundedCornerShape(14.dp),
        )
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

@Preview(showBackground = true)
@Composable
fun NumberFieldPreview() {
    HombreCamionTheme {
        NumberField(
            value = "123.45",
            onValueChange = {},
            errorText = null,
            label = "Sample Label",
            placeHolderText = "Sample Placeholder",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NumberFieldWithErrorPreview() {
    HombreCamionTheme {
        NumberField(
            value = "123.45",
            onValueChange = {},
            errorText = R.string.app_name,
            label = "Sample Label",
            placeHolderText = "Sample Placeholder",
            modifier = Modifier.padding(16.dp)
        )
    }
}
