package com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.ui.graphics.Color
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.screens.HombreCamionScreens
import com.rfz.appflotal.core.util.screens.NavScreens
import com.rfz.appflotal.presentation.theme.primaryLight
import com.rfz.appflotal.presentation.ui.forums.navigation.ForumsGraph
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.IconResource
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.model.asIcon
import com.rfz.appflotal.presentation.ui.reportes.navigation.ReportGraph

// Paleta específica de esta pantalla
object CompletePlanColors {
    val TealDark = primaryLight
    val TealMid = primaryLight.copy(alpha = 0.5f)
    val TealSoftBg = Color(0xFFE7F3F4)
    val WeatherBg = Color(0xFFD8EEF9)
    val CriticalBg = Color(0xFFFBDEDE)
    val CriticalFg = Color(0xFFD23B3B)
    val PendingBg = Color(0xFFFBE8C8)
    val PendingFg = Color(0xFFB57A17)
    val SubtleText = Color(0xFF8A97A0)
}

enum class BottomNavItems(
    val navIcon: IconResource,
    @StringRes val label: Int,
    val route: Any? = null,
    val hasBadge: Boolean = false
) {
    HOME(Icons.Outlined.Home.asIcon(), R.string.title_inicio, NavScreens.HOME),

    MONITOR(
        R.drawable.tire_pressure_warning.asIcon(),
        R.string.monitor,
        HombreCamionScreens.MONITOR
    ),
    ANALYTICS(Icons.Outlined.QueryStats.asIcon(), R.string.analytics, ReportGraph),
    MAP(Icons.Outlined.Map.asIcon(), R.string.vial_status, HombreCamionScreens.MAPA_VIAL.name),
    FORUM(Icons.Outlined.Forum.asIcon(), R.string.forum_title, ForumsGraph)
}