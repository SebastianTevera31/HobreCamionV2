package com.rfz.appflotal.presentation.ui.monitor.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.rfz.appflotal.presentation.theme.primaryLight


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitorTopBar(modifier: Modifier = Modifier, popBackStack: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = primaryLight, // Color de fondo
            titleContentColor = Color.White // Color del título
        ), title = { Text("Monitor") }, navigationIcon = {
            IconButton(onClick = { popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        modifier = modifier
    )
}