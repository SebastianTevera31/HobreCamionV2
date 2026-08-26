package com.rfz.appflotal.presentation.ui.tiremanagment.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rfz.appflotal.presentation.theme.HombreCamionTheme


@Composable
fun TireCatalogRoute(modifier: Modifier = Modifier) {
    TireCatalogScreen(modifier = modifier)
}

@Composable
fun TireCatalogScreen(modifier: Modifier = Modifier) {

}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun TireCatalogScreenPreview() {
    HombreCamionTheme {
        TireCatalogScreen()
    }
}