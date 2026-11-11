package com.rfz.appflotal.presentation.ui.inicio.ui

import com.rfz.appflotal.core.util.screens.NavScreens
import org.junit.Assert.assertEquals
import org.junit.Test

/** Destino que pide una notificación de "Siguiendo" al abrir la app. */
class NotificationDestinationTest {

    private fun resolve(
        destination: String = followedAccessoryDestination(42),
        route: String? = NavScreens.HOME,
        hasUser: Boolean = true,
        hasCompletePlan: Boolean = true
    ) = resolveNotificationDestination(destination, route, hasUser, hasCompletePlan)

    @Test
    fun resolveNotificationDestination_appStarting_waits() {
        assertEquals(NotificationDestinationAction.Wait, resolve(route = null))
        assertEquals(NotificationDestinationAction.Wait, resolve(route = NavScreens.LOADING))
    }

    @Test
    fun resolveNotificationDestination_sessionNotLoadedYet_waits() {
        assertEquals(NotificationDestinationAction.Wait, resolve(hasUser = false))
    }

    @Test
    fun resolveNotificationDestination_authRoute_discards() {
        assertEquals(NotificationDestinationAction.Discard, resolve(route = NavScreens.LOGIN))
        assertEquals(
            NotificationDestinationAction.Discard,
            resolve(route = NavScreens.TERMINOS, hasUser = false)
        )
    }

    @Test
    fun resolveNotificationDestination_withoutCompletePlan_discards() {
        assertEquals(NotificationDestinationAction.Discard, resolve(hasCompletePlan = false))
    }

    @Test
    fun resolveNotificationDestination_validDestination_opensDetailOrList() {
        assertEquals(NotificationDestinationAction.Open(42), resolve())
        assertEquals(
            NotificationDestinationAction.Open(null),
            resolve(destination = InicioActivity.DESTINO_FOLLOW_PROMOTIONS)
        )
    }

    @Test
    fun resolveNotificationDestination_unknownOrMalformedDestination_discards() {
        assertEquals(NotificationDestinationAction.Discard, resolve(destination = "otro"))
        assertEquals(
            NotificationDestinationAction.Discard,
            resolve(destination = "followed_accessory:abc")
        )
    }
}
