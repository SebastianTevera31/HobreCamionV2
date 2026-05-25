package com.rfz.appflotal.presentation.commons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun ErrorView(
    modifier: Modifier = Modifier,
    showRetryButton: Boolean = false,
    onRetry: () -> Unit = {}
) {
    val errorMessage = stringResource(R.string.error_carga_datos)
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.camion_descompuesto),
                contentDescription = null,
                modifier = Modifier.size(240.dp)
            )
            Text(
                errorMessage,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            if (showRetryButton) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRetry) {
                    Text(text = stringResource(R.string.reintentar))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorViewPreview() {
    HombreCamionTheme {
        ErrorView(onRetry = {}, showRetryButton = true)
    }
}