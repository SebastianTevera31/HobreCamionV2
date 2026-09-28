package com.rfz.appflotal.presentation.ui.monitor.screen

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorTire

@Composable
fun WheelMountingScreen(
    baseConfigLabel: String?,
    tires: List<MonitorTire>,
    image: Bitmap?,
    imageDimens: Pair<Int, Int>,
    onMountTireClick: (position: String) -> Unit,
    onLinkSensorClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.montaje_ruedas_titulo),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            if (baseConfigLabel != null) {
                Text(
                    text = baseConfigLabel,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }

            Text(
                text = stringResource(R.string.montaje_ruedas_descripcion),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when {
                    baseConfigLabel == null -> Text(
                        text = stringResource(R.string.configuracion_no_soportada),
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp)
                    )

                    image == null -> CircularProgressIndicator()

                    else -> DiagramImage(
                        tires = tires,
                        image = image,
                        width = imageDimens.first,
                        height = imageDimens.second,
                        tireSelected = "",
                        orientation = DiagramOrientation.VERTICAL,
                        onHotspotClick = onMountTireClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Button(
                onClick = onLinkSensorClick,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.vincular_sensor))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WheelMountingScreenPreview() {
    val sampleTires = listOf(
        MonitorTire("P1", inAlert = false, isAssembled = true, isActive = true, xPosition = 100, yPosition = 100),
        MonitorTire("P2", inAlert = false, isAssembled = false, isActive = true, xPosition = 300, yPosition = 100),
    )
    HombreCamionTheme {
        WheelMountingScreen(
            baseConfigLabel = "BASE 6",
            tires = sampleTires,
            image = Bitmap.createBitmap(600, 300, Bitmap.Config.ARGB_8888),
            imageDimens = Pair(600, 300),
            onMountTireClick = {},
            onLinkSensorClick = {}
        )
    }
}
