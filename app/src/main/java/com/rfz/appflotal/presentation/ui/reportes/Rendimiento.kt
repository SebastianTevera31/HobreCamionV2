package com.rfz.appflotal.presentation.ui.reportes

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun RendimientoView(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SimpleTopBar(
                title = "Reportes",
                onBack = {},
                showBackButton = true,
                subTitle = ""
            )
        }
    ) { innerPadding ->

    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun RendimientoScreenPreview() {
    HombreCamionTheme {
        RendimientoView()
    }
}