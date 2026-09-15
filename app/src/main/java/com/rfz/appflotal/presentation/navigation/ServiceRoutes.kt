package com.rfz.appflotal.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Rutas type-safe del módulo de servicios (Navigation Compose).
 *
 * Flujo "solo servicios": una lista de servicios y un formulario de alta/edición.
 * Ya no hay órdenes de servicio.
 */

/** Grafo contenedor del módulo. Se navega a él con navController.navigate(ServiceGraph). */
@Serializable
object ServiceGraph

/** Lista de servicios del vehículo (pantalla inicial del grafo). */
@Serializable
object ServiceList

/**
 * Formulario de servicio.
 * serviceId == null -> alta (Nuevo servicio)
 * serviceId != null -> edición
 */
@Serializable
data class ServiceFormRoute(val serviceId: Int? = null)
