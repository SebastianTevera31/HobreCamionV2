package com.rfz.appflotal.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Rutas type-safe del módulo de servicios (Navigation Compose).
 *
 * En lugar de rutas como strings ("servicios/{id}") se declaran objetos/clases
 * @Serializable. Ventajas frente al estilo antiguo:
 *  - Los argumentos son campos tipados (Int, Int?), no strings que hay que parsear.
 *  - No se puede navegar a una ruta con argumentos mal formados: lo verifica el compilador.
 *  - Es el mismo patrón que ya usa el módulo de cuponera (couponGraph).
 */

/** Grafo contenedor del módulo. Se navega a él con navController.navigate(ServiceGraph). */
@Serializable
object ServiceGraph

/** Lista de órdenes de un vehículo (pantalla inicial del grafo). */
@Serializable
object ServiceList

/** Detalle de una orden concreta. */
@Serializable
data class ServiceOrderDetailRoute(val orderId: Int)

/**
 * Formulario de servicio dentro de una orden.
 * serviceId == null  -> alta (Nuevo servicio)
 * serviceId != null  -> edición (Detalles de servicio)
 */
@Serializable
data class ServiceFormRoute(val orderId: Int, val serviceId: Int? = null)

/**
 * Formulario de orden.
 * orderId == null -> crear orden nueva
 * orderId != null -> modificar una orden existente
 */
@Serializable
data class ServiceOrderFormRoute(val orderId: Int? = null)
