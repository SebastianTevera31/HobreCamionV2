package com.rfz.appflotal.presentation.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.ui.languaje.LocalizedApp

@Composable
fun AwaitDialog(@StringRes message: Int = R.string.espere_un_momento) {
    AlertDialog(
        onDismissRequest = { },
        title = {},
        text = {
            LocalizedApp {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        color = Color(0xFF5B2034),
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 4.dp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(stringResource(message), fontWeight = FontWeight.Medium)
                }
            }
        },
        confirmButton = {},
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}