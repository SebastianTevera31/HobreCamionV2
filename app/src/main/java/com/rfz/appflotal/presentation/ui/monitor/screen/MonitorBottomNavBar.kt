package com.rfz.appflotal.presentation.ui.monitor.screen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R

enum class MonitorScreenViews(
    @StringRes val title: Int,
) {
    DIAGRAMA(title = R.string.diagrama),
    POSICION(title = R.string.posiciones)
}

@Composable
fun MonitorBottomNavBar(
    modifier: Modifier = Modifier,
    onClick: (MonitorScreenViews) -> Unit,
    selectedView: MonitorScreenViews
) {
    BottomAppBar(
        windowInsets = NavigationBarDefaults.windowInsets,
        containerColor = Color.White,
        modifier = modifier
    ) {
        MonitorNavButton(
            isSelected = selectedView,
            image = R.drawable.motor_vehicle,
            text = MonitorScreenViews.DIAGRAMA.title,
            modifier = Modifier.weight(1f)
        ) { onClick(MonitorScreenViews.DIAGRAMA) }

        MonitorNavButton(
            isSelected = selectedView,
            image = R.drawable.float_float,
            text = MonitorScreenViews.POSICION.title,
            modifier = Modifier.weight(1f)
        ) { onClick(MonitorScreenViews.POSICION) }
    }
}

@Composable
fun MonitorNavButton(
    isSelected: MonitorScreenViews,
    modifier: Modifier = Modifier,
    @DrawableRes image: Int,
    @StringRes text: Int,
    onClick: () -> Unit
) {
    Button(
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(if (isSelected.title == text) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.onPrimaryContainer)
        ) {
            Icon(
                painter = painterResource(image),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            )
            Text(
                stringResource(text),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }
}

