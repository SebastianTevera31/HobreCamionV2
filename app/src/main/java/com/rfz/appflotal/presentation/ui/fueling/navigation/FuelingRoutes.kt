package com.rfz.appflotal.presentation.ui.fueling.navigation

import kotlinx.serialization.Serializable

/** Grafo del módulo de cargas de combustible. Se entra con navController.navigate(FuelingGraph). */
@Serializable
object FuelingGraph

/** Historial de cargas (pantalla inicial del grafo). */
@Serializable
object FuelingHistory

/** Alta de una carga. */
@Serializable
object FuelingForm
