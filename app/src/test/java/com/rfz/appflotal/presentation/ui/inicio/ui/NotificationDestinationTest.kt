package com.rfz.appflotal.presentation.ui.inicio.ui

import com.rfz.appflotal.core.util.screens.NavScreens
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Destino que pide una notificación de "Siguiendo" al abrir la app. */
class NotificationDestinationTest {

    private val accessoryDestination = followedAccessoryDestination(42)

    private fun resolve(
        destination: String = accessoryDestination,
        route: String? = NavScreens.HOME,
        hasUser: Boolean = true,
        hasCompletePlan: Boolean = true
    ) = resolveNotificationDestination(destination, route, hasUser, hasCompletePlan)

    @Test
    fun `mientras arranca la app se espera`() {
        assertEquals(NotificationDestinationAction.Wait, resolve(route = null))
        assertEquals(NotificationDestinationAction.Wait, resolve(route = NavScreens.LOADING))
    }

    @Test
    fun `si la sesion aun no se lee se espera en lugar de descartar`() {
        assertEquals(NotificationDestinationAction.Wait, resolve(hasUser = false))
    }

    @Test
    fun `en pantallas de acceso se descarta`() {
        assertEquals(NotificationDestinationAction.Discard, resolve(route = NavScreens.LOGIN))
        assertEquals(
            NotificationDestinationAction.Discard,
            resolve(route = NavScreens.TERMINOS, hasUser = false)
        )
    }

    @Test
    fun `sin plan Complete se descarta`() {
        assertEquals(NotificationDestinationAction.Discard, resolve(hasCompletePlan = false))
    }

    @Test
    fun `con sesion abre el detalle o la lista de seguidos`() {
        assertEquals(NotificationDestinationAction.Open(42), resolve())
        assertEquals(
            NotificationDestinationAction.Open(null),
            resolve(destination = InicioActivity.DESTINO_FOLLOW_PROMOTIONS)
        )
    }

    @Test
    fun `destino desconocido se descarta`() {
        assertEquals(NotificationDestinationAction.Discard, resolve(destination = "otro"))
        assertEquals(
            NotificationDestinationAction.Discard,
            resolve(destination = "followed_accessory:abc")
        )
    }

    @Test
    fun `el id del accesorio se recupera del destino`() {
        assertEquals(42L, parseFollowedAccessoryId(accessoryDestination))
        assertNull(parseFollowedAccessoryId("follow_promotions"))
    }
}
