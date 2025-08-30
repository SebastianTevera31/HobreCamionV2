package com.rfz.appflotal.presentation.ui.updateuserscreen.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun UpdateDriverScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        modifier = modifier.verticalScroll(scrollState)
    ) {

    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun UpdateDriverScreenPreview() {
    HombreCamionTheme {
        UpdateDriverScreen(modifier = Modifier.safeDrawingPadding())
    }
}