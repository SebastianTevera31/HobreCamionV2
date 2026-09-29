package com.rfz.appflotal.presentation.ui.monitor.screen

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.ui.monitor.viewmodel.MonitorTire

private val MountedColor = Color(0xFF2E7D32)

@Composable
fun WheelMountingScreen(
    hasConfiguration: Boolean,
    baseConfigLabel: String?,
    isSensorLinked: Boolean,
    tires: List<MonitorTire>,
    image: Bitmap?,
    imageDimens: Pair<Int, Int>,
    onMountTireClick: (position: String) -> Unit,
    onDefineConfigClick: () -> Unit,
    onLinkSensorClick: () -> Unit,
    onFinishClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var pendingPosition by remember { mutableStateOf<String?>(null) }

    pendingPosition?.let { position ->
        MountConfirmDialog(
            position = position,
            onDismiss = { pendingPosition = null },
            onConfirm = {
                pendingPosition = null
                onMountTireClick(position)
            }
        )
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (hasConfiguration) {
                        OutlinedButton(
                            onClick = onLinkSensorClick,
                            enabled = !isSensorLinked,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                stringResource(
                                    if (isSensorLinked) R.string.sensor_vinculado else R.string.vincular_sensor
                                )
                            )
                        }
                    }
                    Button(
                        onClick = onFinishClick,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.finalizar))
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = buildString {
                    append(stringResource(R.string.montaje_ruedas_titulo))
                    if (hasConfiguration && baseConfigLabel != null) append(" · $baseConfigLabel")
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            when {
                !hasConfiguration -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = stringResource(R.string.montaje_sin_configuracion),
                        textAlign = TextAlign.Center
                    )
                    Button(onClick = onDefineConfigClick) {
                        Text(stringResource(R.string.definir_configuracion))
                    }
                }

                baseConfigLabel == null -> Text(
                    text = stringResource(R.string.configuracion_no_soportada),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(24.dp)
                )

                image == null -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                else -> {
                    ProgressCard(
                        mounted = tires.count { it.isAssembled },
                        total = tires.size
                    )

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            DiagramImage(
                                tires = tires,
                                image = image,
                                width = imageDimens.first,
                                height = imageDimens.second,
                                tireSelected = "",
                                orientation = DiagramOrientation.VERTICAL,
                                onHotspotClick = { position ->
                                    val tire = tires.find { it.sensorPosition == position }
                                    if (tire?.isAssembled != true) pendingPosition = position
                                },
                                sizeDp = maxWidth,
                                showAssemblyStatus = true,
                                scrollable = false
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressCard(mounted: Int, total: Int) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.montaje_ruedas_descripcion),
                style = MaterialTheme.typography.bodyMedium
            )
            LinearProgressIndicator(
                progress = { if (total == 0) 0f else mounted / total.toFloat() },
                color = MountedColor,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.llantas_montadas_contador, mounted, total),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendDot(Color(0xCC212121), stringResource(R.string.leyenda_pendiente))
                    LegendDot(MountedColor, stringResource(R.string.leyenda_montada))
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun MountConfirmDialog(
    position: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = position,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Text(
                    text = stringResource(R.string.montar_llanta_titulo),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Text(
                text = stringResource(R.string.montar_llanta_mensaje, position),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = onConfirm, shape = RoundedCornerShape(12.dp)) {
                Text(stringResource(R.string.montar_llanta_confirmar))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancelar)) }
        }
    )
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
            hasConfiguration = true,
            baseConfigLabel = "BASE 6",
            isSensorLinked = false,
            tires = sampleTires,
            image = createBitmap(600, 300),
            imageDimens = Pair(600, 300),
            onMountTireClick = {},
            onDefineConfigClick = {},
            onLinkSensorClick = {},
            onFinishClick = {}
        )
    }
}
