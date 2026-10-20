package com.rfz.appflotal.presentation.ui.monitor.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.data.repository.bluetooth.BluetoothSignalQuality
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.theme.primaryLight


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitorTopBar(
    showNavigationButton: Boolean = true,
    showDialog: () -> Unit,
    modifier: Modifier = Modifier,
    monitorMac: String = "",
    signalQuality: BluetoothSignalQuality = BluetoothSignalQuality.Desconocida,
    popBackStack: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = primaryLight, // Color de fondo
            titleContentColor = Color.White // Color del título
        ),
        title = { Text("Monitor") },
        navigationIcon = {
            if (showNavigationButton) {
                IconButton(onClick = { popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            TpmsStatusChip(monitorMac = monitorMac, signalQuality = signalQuality)
            IconButton(onClick = showDialog) {
                Icon(
                    painter = painterResource(R.drawable.macconfig),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = Color.White
                )
            }
        },
        modifier = modifier
    )
}

/**
 * Estado de la conexión TPMS: sin monitor asignado (gris), conectado (verde) o con
 * monitor asignado pero sin señal (ámbar).
 */
@Composable
private fun TpmsStatusChip(
    monitorMac: String,
    signalQuality: BluetoothSignalQuality,
    modifier: Modifier = Modifier
) {
    val (dotColor, text) = when {
        monitorMac.isBlank() -> Color(0xFF9E9E9E) to stringResource(R.string.sin_tpms)
        signalQuality == BluetoothSignalQuality.Desconocida ->
            Color(0xFFFFB300) to stringResource(
                R.string.tpms_estado,
                stringResource(R.string.sin_conexi_n)
            )

        else -> Color(0xFF4CAF50) to stringResource(
            R.string.tpms_estado,
            stringResource(signalQuality.signalText!!)
        )
    }

    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.15f),
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(dotColor, CircleShape)
            )
            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MonitorTopBarPreview() {
    HombreCamionTheme {
        MonitorTopBar(
            popBackStack = {},
            showDialog = {},
            monitorMac = "AA:BB:CC:DD:EE:FF",
            signalQuality = BluetoothSignalQuality.Excelente
        )
    }
}
