package com.rfz.appflotal.data.model.tire.response

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** hasInspection de api/Tire/GetTireById llega como booleano. */
class TireIdResponseTest {

    private val gson = Gson()

    private fun json(hasInspection: String?) = """
        {
          "id_tire": 15,
          "c_typeAcquisition_fk_1": 1,
          "c_provider_fk_3": 0,
          "fld_acquisitionDate": "2026-10-21T00:00:00",
          "fld_registrationDate": "2026-10-21T10:00:00",
          "fld_document": "F-1",
          "fld_treadDepth": 18,
          "fld_unitCost": 8500,
          "fld_tireNumber": "LL-1",
          "c_user_fk_5": 3,
          "c_product_fk_6": 7,
          "fld_dot": "B6XY2425",
          "fld_active": true,
          "c_retreadDesign_fk_2": 0,
          "c_destination_fk_8": 2,
          "fld_lifecycle": 0
          ${hasInspection?.let { ""","hasInspection": $it""" } ?: ""}
        }
    """.trimIndent()

    private fun parse(hasInspection: String?): TireIdResponse =
        gson.fromJson(json(hasInspection), TireIdResponse::class.java)

    @Test
    fun `hasInspection false se lee como false`() {
        assertEquals(false, parse("false").hasInspection)
    }

    @Test
    fun `hasInspection true se lee como true`() {
        assertEquals(true, parse("true").hasInspection)
    }

    @Test
    fun `sin hasInspection queda null para que solo cuente el Wizard`() {
        assertNull(parse(null).hasInspection)
    }
}
