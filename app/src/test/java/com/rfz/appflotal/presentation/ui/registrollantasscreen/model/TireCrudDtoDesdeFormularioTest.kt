package com.rfz.appflotal.presentation.ui.registrollantasscreen.model

import com.rfz.appflotal.data.model.tire.response.TireIdResponse
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class TireCrudDtoDesdeFormularioTest {

    private val registro = LocalDateTime.of(2026, 11, 27, 10, 30)

    private val campos = CamposLlanta(
        tipoAdquisicion = TipoAdquisicionUi(1, "Compra", "Purchase"),
        producto = ProductoUi(7, "Michelin X Multi Z", 16),
        fechaAdquisicion = "2026-11-20T00:00:00",
        costo = "4500.75",
        folioFactura = " F-001 ",
        profundidad = "15.5",
        numeroLlanta = " A-1023 ",
        dot = "2423",
    )

    private val ficha = TireIdResponse(
        idTire = 55,
        typeAcquisitionId = 3,
        providerId = 0,
        acquisitionDate = "2025-01-10T00:00:00",
        registrationDate = "2025-01-10T00:00:00",
        document = "F-OLD",
        treadDepth = 18.0,
        unitCost = 3900,
        tireNumber = "VIEJO",
        userId = 1,
        productId = 9,
        dot = "0000",
        isActive = true,
        retreadDesignId = 0,
        destinationId = 2,
        lifecycle = 0,
        hasInspection = true,
    )

    @Test
    fun construirTireCrudDto_alta_usaLosCamposCapturados() {
        val dto = construirTireCrudDto(0, campos, fichaBloqueada = null, fechaRegistro = registro)

        assertEquals(0, dto.idTire)
        assertEquals(1, dto.typeAcquisitionId)
        assertEquals(7, dto.productId)
        assertEquals("2026-11-20T00:00", dto.acquisitionDate)
        assertEquals(4500, dto.unitCost)
        assertEquals(15.5, dto.treadDepth, 0.0)
        assertEquals("F-001", dto.document)
        assertEquals("A-1023", dto.tireNumber)
        assertEquals("2026-11-27T10:30", dto.registrationDate)
    }

    @Test
    fun construirTireCrudDto_adquisicionBloqueada_conservaDatosDeLaFicha() {
        val dto = construirTireCrudDto(55, campos, fichaBloqueada = ficha, fechaRegistro = registro)

        assertEquals(55, dto.idTire)
        assertEquals(3, dto.typeAcquisitionId)
        assertEquals(9, dto.productId)
        assertEquals("2025-01-10T00:00:00", dto.acquisitionDate)
        assertEquals(3900, dto.unitCost)
        assertEquals(18.0, dto.treadDepth, 0.0)
        // La identificación sí se toma de lo capturado.
        assertEquals("A-1023", dto.tireNumber)
        assertEquals("2423", dto.dot)
        assertEquals("F-001", dto.document)
    }

    @Test
    fun construirTireCrudDto_fechaIlegible_usaLaFechaDeRegistro() {
        val dto = construirTireCrudDto(
            0, campos.copy(fechaAdquisicion = "x"), fichaBloqueada = null, fechaRegistro = registro
        )

        assertEquals("2026-11-27T10:30", dto.acquisitionDate)
    }
}
