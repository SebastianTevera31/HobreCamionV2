package com.rfz.appflotal.presentation.navigation

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.rfz.appflotal.core.util.screens.NavScreens
import com.rfz.appflotal.presentation.ui.home.screen.completeplan.utils.BottomNavItems

/**
 * Navegación "a prueba de doble toque".
 *
 * Problema: cada toque de un botón back llama a popBackStack()/navigateUp() de
 * inmediato. Si el usuario toca varias veces rápido —antes de que la transición
 * termine— se ejecutan varios pops y se sacan de la pila más pantallas de las
 * debidas (o se navega varias veces), desbordando la navegación.
 *
 * Solución: solo actuar si el destino actual sigue RESUMED. Al iniciar cualquier
 * navegación, el BackStackEntry actual baja de RESUMED durante la transición, así
 * que los toques sucesivos en esa ventana se ignoran de forma segura.
 *
 * Es el patrón recomendado por Google para Navigation Compose.
 */
private fun NavController.isCurrentDestinationResumed(): Boolean =
    currentBackStackEntry?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.RESUMED) == true

/** popBackStack() protegido contra toques múltiples durante la transición. */
fun NavController.popBackStackSafely(): Boolean =
    if (isCurrentDestinationResumed()) popBackStack() else false

/** navigateUp() protegido contra toques múltiples durante la transición. */
fun NavController.navigateUpSafely(): Boolean =
    if (isCurrentDestinationResumed()) navigateUp() else false

// ---------------------------------------------------------------------------
// Navegación a pestañas del bottom bar.
// ---------------------------------------------------------------------------

/** ¿La ruta corresponde a una pestaña del bottom bar? */
fun isBottomBarTabRoute(route: Any?): Boolean =
    route != null && BottomNavItems.entries.any { it.route == route }

/**
 * Navega a una pestaña del bottom bar de forma idempotente e integrada con
 * saveState/restoreState.
 *
 * Se usa TANTO para los clics del bottom bar COMO para los enlaces de contenido
 * (grid, tarjetas) cuyo destino es una pestaña. Así una pestaña nunca queda en la
 * pila "en dos modos" (uno integrado con saveState y otro no), que es lo que
 * corrompía la navegación e impedía volver a Home.
 */
fun NavController.navigateAsBottomBarTab(route: Any) {
    val options: NavOptionsBuilder.() -> Unit = {
        popUpTo(NavScreens.HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
    when (route) {
        is String -> navigate(route, builder = options)
        else -> navigate(route, builder = options)
    }
}
