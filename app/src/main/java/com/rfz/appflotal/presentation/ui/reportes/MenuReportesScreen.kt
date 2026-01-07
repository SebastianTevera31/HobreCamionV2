package com.rfz.appflotal.presentation.ui.reportes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.home.screen.ElegantMenuCard
import com.rfz.appflotal.presentation.ui.home.utils.cardBackground
import com.rfz.appflotal.presentation.ui.home.utils.primaryColor
import com.rfz.appflotal.presentation.ui.home.utils.secondaryColor


@Composable
fun MenuReportesView(modifier: Modifier = Modifier) {
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ElegantMenuCard(
                    title = "Rendimiento",
                    iconRes = R.drawable.rendimiento,
                    onClick = {},
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor,
                    cardBackground = cardBackground,
                    modifier = Modifier.weight(1f)
                )
                ElegantMenuCard(
                    title = "Proyeccion",
                    iconRes = R.drawable.proyeccion,
                    onClick = {},
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor,
                    cardBackground = cardBackground,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ElegantMenuCard(
                    title = "Pila de Desecho",
                    iconRes = R.drawable.pila_desecho,
                    onClick = {},
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor,
                    cardBackground = cardBackground,
                    modifier = Modifier.weight(1f)
                )
                ElegantMenuCard(
                    title = "Semaforo",
                    iconRes = R.drawable.semaforeo,
                    onClick = {},
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor,
                    cardBackground = cardBackground,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ElegantMenuCard(
                    title = "Servicios",
                    iconRes = R.drawable.servicios,
                    onClick = {},
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor,
                    cardBackground = cardBackground,
                    modifier = Modifier.width(175.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MenuReportesScreenPreview() {
    HombreCamionTheme {
        MenuReportesView()
    }
}
