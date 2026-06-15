package com.rfz.appflotal.presentation.ui.reportes.rendimiento

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.data.model.assembly.AssemblyTire
import com.rfz.appflotal.presentation.commons.SimpleTopBar
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

@Composable
fun MenuRendimientoView(wheels: List<AssemblyTire>, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SimpleTopBar(
                title = "Reporte de Rendimiento",
                onBack = {},
                showBackButton = true,
                subTitle = ""
            )
        }
    ) { innerPadding ->
        Column(
            modifier
                .padding(innerPadding)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Seleccione una llanta",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(wheels) { wheel ->
                    WheelButton(wheel)
                }
            }
        }
    }
}

@Composable
fun WheelButton(wheel: AssemblyTire, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable { },
        colors = CardDefaults.cardColors(
            containerColor = Color(0x402E3192)
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = wheel.positionTire)
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun MenuRendimientoScreenPreview() {
    HombreCamionTheme {
        MenuRendimientoView(
            wheels = listOf(
                AssemblyTire(
                    idAxle = 1,
                    idTire = 1,
                    positionTire = "P1",
                    odometer = 1000,
                    assemblyDate = "15 de junio del 2026",
                    updatedAt = 123449
                )
            )
        )
    }
}