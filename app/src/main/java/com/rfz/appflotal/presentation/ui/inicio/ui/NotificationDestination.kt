package com.rfz.appflotal.presentation.ui.inicio.ui

import com.rfz.appflotal.core.util.screens.NavScreens

private const val DESTINO_FOLLOWED_ACCESSORY_PREFIX = "followed_accessory:"

/** Destino al detalle de un accesorio seguido; viaja como texto para guardarse en el estado. */
fun followedAccessoryDestination(accessoryId: Long): String =
    "$DESTINO_FOLLOWED_ACCESSORY_PREFIX$accessoryId"

internal fun parseFollowedAccessoryId(destination: String): Long? =
    destination.removePrefix(DESTINO_FOLLOWED_ACCESSORY_PREFIX)
        .takeIf { destination.startsWith(DESTINO_FOLLOWED_ACCESSORY_PREFIX) }
        ?.toLongOrNull()

// Rutas previas a tener sesión completa: un destino pendiente aquí se descarta.
internal val AUTH_ROUTES = setOf(
    NavScreens.LOGIN,
    NavScreens.RECUPERAR_CONTRASENIA,
    NavScreens.REGISTRAR_USUARIO,
    NavScreens.TERMINOS,
    NavScreens.PERMISOS
)

/** Qué hacer con el destino que pidió una notificación. */
internal sealed interface NotificationDestinationAction {
    /** Aún no se sabe a dónde lleva el arranque o qué sesión hay; se vuelve a evaluar. */
    data object Wait : NotificationDestinationAction
    data object Discard : NotificationDestinationAction

    /** Abrir "Siguiendo" y, si [followedAccessoryId] no es null, el detalle de ese accesorio. */
    data class Open(val followedAccessoryId: Long?) : NotificationDestinationAction
}

/**
 * [hasUser] es false tanto sin sesión como mientras la sesión todavía no se lee de la base
 * local; fuera de LOADING y de las pantallas de acceso hay sesión, así que se espera a que
 * llegue en lugar de descartar el destino.
 */
internal fun resolveNotificationDestination(
    destination: String,
    currentRoute: String?,
    hasUser: Boolean,
    hasCompletePlan: Boolean
): NotificationDestinationAction {
    if (currentRoute == null || currentRoute == NavScreens.LOADING) {
        return NotificationDestinationAction.Wait
    }
    if (currentRoute in AUTH_ROUTES) return NotificationDestinationAction.Discard
    if (!hasUser) return NotificationDestinationAction.Wait
    if (!hasCompletePlan) return NotificationDestinationAction.Discard

    val accessoryId = parseFollowedAccessoryId(destination)
    return if (destination == InicioActivity.DESTINO_FOLLOW_PROMOTIONS || accessoryId != null) {
        NotificationDestinationAction.Open(accessoryId)
    } else {
        NotificationDestinationAction.Discard
    }
}
