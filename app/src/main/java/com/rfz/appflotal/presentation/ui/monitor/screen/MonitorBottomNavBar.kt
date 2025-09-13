package com.rfz.appflotal.presentation.ui.monitor.screen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.theme.HombreCamionTheme

enum class MonitorScreenViews(
    @StringRes val title: Int,
) {
    DIAGRAMA(title = R.string.diagrama),
    POSICION(title = R.string.posiciones)
}

private data class MonitorNavItem(
    val view: MonitorScreenViews,
    @DrawableRes val iconRes: Int,
    @StringRes val labelRes: Int
)

// Ajusta tus icons aquí:
private val monitorNavItems = listOf(
    MonitorNavItem(
        MonitorScreenViews.DIAGRAMA,
        R.drawable.motor_vehicle,
        MonitorScreenViews.DIAGRAMA.title
    ),
    MonitorNavItem(
        MonitorScreenViews.POSICION,
        R.drawable.float_float,
        MonitorScreenViews.POSICION.title
    ),
)

// -------------------- NavigationBar (Material 3) --------------------
@Composable
fun MonitorBottomNavBar(
    modifier: Modifier = Modifier,
    onClick: (MonitorScreenViews) -> Unit,
    selectedView: MonitorScreenViews
) {
    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,
        windowInsets = NavigationBarDefaults.windowInsets
    ) {
        monitorNavItems.forEach { item ->
            val selected = (item.view == selectedView)
            // Evita recrear lambdas en cada recomposición:
            val onTap = remember(onClick, item.view) { { onClick(item.view) } }

            NavigationBarItem(
                selected = selected,
                onClick = onTap,
                icon = {
                    // Cambia a ImageVector.vectorResource(item.iconRes) si tus recursos son vectoriales
                    Icon(
                        painter = painterResource(item.iconRes),
                        contentDescription = stringResource(item.labelRes),
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = stringResource(item.labelRes),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun MonitorBottomNavBarPreview() {
    HombreCamionTheme {
        MonitorBottomNavBar(
            onClick = {},
            selectedView = MonitorScreenViews.DIAGRAMA
        )
    }
}

