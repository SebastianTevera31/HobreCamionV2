package com.rfz.appflotal.presentation.ui.inicio.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun NotificationPermissionDialog(
    onDismiss: () -> Unit,
    onConfirmation: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        title = {
            Text(text = stringResource(R.string.permiso_de_notificacion))
        },
        text = {
            Text(text = stringResource(R.string.texto_permiso_de_notification))
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(stringResource(R.string.confirmar))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.cancelar))
            }
        },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun NotificationPermissionPreview() {
    HombreCamionTheme {
        NotificationPermissionDialog(
            onDismiss = {},
            onConfirmation = {}
        )
    }
}
