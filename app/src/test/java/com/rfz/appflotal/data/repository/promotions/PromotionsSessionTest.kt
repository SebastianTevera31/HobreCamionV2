package com.rfz.appflotal.data.repository.promotions

import com.rfz.appflotal.data.model.database.AppHCEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** GetDiscounts solo se consulta con una sesión activa de plan Complete. */
class PromotionsSessionTest {

    private fun user(token: String = "token", plan: String = "Complete") = AppHCEntity(
        idUser = 1,
        fld_name = "",
        fld_username = "",
        fld_email = "",
        fld_token = token,
        country = 0,
        industry = 0,
        vehicleType = "",
        monitorMac = "",
        baseConfiguration = "",
        idVehicle = 0,
        vehiclePlates = "",
        paymentPlan = plan,
        fecha = "",
        termsGranted = true
    )

    @Test
    fun `sesion Complete con token devuelve el token`() {
        assertEquals("token", user().completeSessionToken())
    }

    @Test
    fun `sin sesion no hay token`() {
        assertNull((null as AppHCEntity?).completeSessionToken())
    }

    @Test
    fun `token vacio no cuenta como sesion`() {
        assertNull(user(token = " ").completeSessionToken())
    }

    @Test
    fun `otros planes no consultan`() {
        listOf("Free", "Only TPMS", "None", "", "desconocido").forEach { plan ->
            assertNull(plan, user(plan = plan).completeSessionToken())
        }
    }
}
