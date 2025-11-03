package com.rfz.appflotal.presentation.ui.monitor.screen

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme
import com.rfz.appflotal.presentation.theme.primaryLight


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitorTopBar(showDialog: () -> Unit, modifier: Modifier = Modifier, popBackStack: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = primaryLight, // Color de fondo
            titleContentColor = Color.White // Color del título
        ),
        title = { Text("Monitor") },
        navigationIcon = {
            IconButton(onClick = { popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        actions = {
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MonitorTopBarPreview() {
    HombreCamionTheme { MonitorTopBar(popBackStack = {}, showDialog = {}) }
}