package com.rfz.appflotal.presentation.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.primaryLight
import com.rfz.appflotal.presentation.theme.secondaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormTextField(
    @StringRes title: Int,
    value: String,
    onValueChange: (String) -> Unit,
    enable: Boolean = true,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    brandColor: Color = primaryLight,
    darkerGray: Color = secondaryLight
) {
    var passwordVisibility by remember { mutableStateOf(false) }
    val visualTransformation = if (keyboardType == KeyboardType.Password && passwordVisibility)
        PasswordVisualTransformation()
    else
        VisualTransformation.None

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = {
            Text(
                stringResource(title),
                color = darkerGray.copy(alpha = 0.8f)
            )
        },
        placeholder = {
            Text(
                stringResource(title),
                color = darkerGray.copy(alpha = 0.6f)
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        trailingIcon = {
            if (KeyboardType.Password == keyboardType) {
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(
                        imageVector = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = stringResource(R.string.content_description_icon_password),
                        tint = brandColor
                    )
                }
            }
        },

        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = brandColor,
            unfocusedBorderColor = brandColor,
            focusedLabelColor = brandColor,
            cursorColor = brandColor,
            focusedTextColor = Color.DarkGray,
            unfocusedTextColor = Color.DarkGray
        ),
        enabled = enable
    )
}