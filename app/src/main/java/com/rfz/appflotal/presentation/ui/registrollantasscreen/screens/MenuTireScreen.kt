package com.rfz.appflotal.presentation.ui.registrollantasscreen.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun MenuTireScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SimpleTopBar(
                title = "Menu de Llantas",
                onBack = onBack,
                showBackButton = true,
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Row() {

            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MenuTireScreenPreview() {
    HombreCamionTheme {
        MenuTireScreen(onBack = {})
    }
}
