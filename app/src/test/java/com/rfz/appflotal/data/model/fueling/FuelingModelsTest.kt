package com.rfz.appflotal.data.model.fueling

import com.google.gson.Gson
import com.rfz.appflotal.data.model.catalog.FuelTypeResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Mapeo del historial de cargas y del catálogo de combustibles. */
class FuelingModelsTest {

    private val gson = Gson()

    private fun record(station: String? = "N/A", lat: Double = 0.0, lon: Double = 0.0) =
        FuelingRecordDto(
            fuelingDate = "2026-01-10T10:52:47.337",
            vehicle = "XXX001 - QWERTYUIOP",
            units = "40.00 L.",
            fuelType = "Diésel(Gasóleo)",
            amount = "$0.00",
            station = station,
            odometer = 1200,
            lat = lat,
            lon = lon
        )

    @Test
    fun `primera pagina de 15 registros tiene siguiente`() {
        val response = FuelingHistoryResponse(List(10) { record() }, total = 15, page = 1, pageSize = 10)

        val page = response.toDomain(requestedPage = 1, requestedPageSize = 10)

        assertEquals(10, page.items.size)
        assertTrue(page.hasNextPage)
    }

    @Test
    fun `ultima pagina no tiene siguiente`() {
        val response = FuelingHistoryResponse(List(5) { record() }, total = 15, page = 2, pageSize = 10)

        assertFalse(response.toDomain(requestedPage = 2, requestedPageSize = 10).hasNextPage)
    }

    @Test
    fun `pagina vacia no tiene siguiente aunque el total sea mayor`() {
        val response = FuelingHistoryResponse(emptyList(), total = 15, page = 0, pageSize = 0)

        val page = response.toDomain(requestedPage = 1, requestedPageSize = 10)

        assertEquals(1, page.page)
        assertFalse(page.hasNextPage)
    }

    @Test
    fun `estacion N_A y coordenadas 0,0 se tratan como ausentes`() {
        val fueling = record(station = "N/A").toDomain()

        assertEquals("", fueling.station)
        assertFalse(fueling.hasCoordinates)
        assertTrue(record(station = "Pemex", lat = 19.4, lon = -99.1).toDomain().hasCoordinates)
    }

    @Test
    fun `parsea la respuesta del historial`() {
        val json = """
            {"data":[{"fld_fuelingDate":"2026-01-10T10:52:47.337","vehicle":"XXX001 - QWERTYUIOP",
            "units":"40.00 L.","fuelType":"Diésel(Gasóleo)","amount":"$0.00","fld_station":"N/A",
            "fld_odometer":1200,"fld_lat":0,"fld_lon":0}],"total":15,"page":1,"pageSize":10}
        """.trimIndent()

        val response = gson.fromJson(json, FuelingHistoryResponse::class.java)
        val first = response.data!!.first().toDomain()

        assertEquals(15, response.total)
        assertEquals("40.00 L.", first.units)
        assertEquals(1200, first.odometer)
    }

    @Test
    fun `parsea el catalogo de combustibles`() {
        val json = """[{"id_fuelType":1,"fuelType":"Diésel(Gasóleo)"},{"id_fuelType":2,"fuelType":"Gasolina"}]"""

        val items = gson.fromJson(json, Array<FuelTypeResponse>::class.java).map { it.toCatalog() }

        assertEquals(listOf(1, 2), items.map { it.id })
        assertEquals("Gasolina", items[1].description)
    }
}
