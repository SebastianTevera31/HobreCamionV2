package com.rfz.appflotal.presentation.ui.reportes.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.reportGraph(
    navController: NavHostController
) {
    navigation<ReportGraph>(
        startDestination = ReportMenu
    ) {
        composable<ReportMenu> {
            val parentEntry = remember(it) {
                try {
                    navController.getBackStackEntry<ReportGraph>()
                } catch (_: Exception) {
                    it
                }
            }
        }
    }
}