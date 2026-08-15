package com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.rfz.appflotal.R

// Paleta específica de esta pantalla
object CompletePlanColors {
    val TealDark = Color(0xFF0B6A76)
    val TealMid = Color(0xFF0E7C89)
    val TealSoftBg = Color(0xFFE7F3F4)
    val WeatherBg = Color(0xFFD8EEF9)
    val CriticalBg = Color(0xFFFBDEDE)
    val CriticalFg = Color(0xFFD23B3B)
    val PendingBg = Color(0xFFFBE8C8)
    val PendingFg = Color(0xFFB57A17)
    val SubtleText = Color(0xFF8A97A0)
}

data class BottomNavItem(val icon: ImageVector, @StringRes val label: Int, val hasBadge: Boolean = false)

val bottomNavItems = listOf(
    BottomNavItem(Icons.Outlined.Home, R.string.title_inicio),
    BottomNavItem(Icons.Outlined.QueryStats, R.string.analytics),
    BottomNavItem(Icons.Outlined.Map, R.string.vial_status),
    BottomNavItem(Icons.Outlined.Forum, R.string.forum_title)
)
