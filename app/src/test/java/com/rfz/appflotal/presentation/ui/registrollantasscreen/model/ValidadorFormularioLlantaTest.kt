package com.rfz.appflotal.presentation.ui.registrollantasscreen.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidadorFormularioLlantaTest {

    private val completos = CamposLlanta(
        tipoAdquisicion = TipoAdquisicionUi(1, "Compra", "Purchase"),
        producto = ProductoUi(7, "Michelin X Multi Z", 16),
        fechaAdquisicion = "2026-11-27",
        costo = "4500",
        profundidad = "16",
        numeroLlanta = "A-1023",
        dot = "2423",
    )

    @Test
    fun validar_camposCompletos_sinErrores() {
        assertTrue(ValidadorFormularioLlanta.validar(completos, adquisicionEditable = true).isEmpty())
    }

    @Test
    fun validar_altaVacia_marcaObligatorios() {
        val errores = ValidadorFormularioLlanta.validar(CamposLlanta(), adquisicionEditable = true)

        assertEquals(
            setOf(
                CampoLlanta.TIPO_ADQUISICION, CampoLlanta.PRODUCTO, CampoLlanta.FECHA,
                CampoLlanta.COSTO, CampoLlanta.PROFUNDIDAD, CampoLlanta.NUMERO_LLANTA
            ),
            errores.keys
        )
        assertTrue(errores.values.all { it == ErrorCampo.REQUERIDO })
    }

    @Test
    fun validar_costoYProfundidadEnCero_marcaMayorACero() {
        val errores = ValidadorFormularioLlanta.validar(
            completos.copy(costo = "0", profundidad = "0.0"),
            adquisicionEditable = true
        )

        assertEquals(
            mapOf(
                CampoLlanta.COSTO to ErrorCampo.MAYOR_A_CERO,
                CampoLlanta.PROFUNDIDAD to ErrorCampo.MAYOR_A_CERO
            ),
            errores
        )
    }

    @Test
    fun validar_numeroYDotLargos_marcaExcedeLongitud() {
        val errores = ValidadorFormularioLlanta.validar(
            completos.copy(numeroLlanta = "1".repeat(16), dot = "1".repeat(11)),
            adquisicionEditable = true
        )

        assertEquals(
            mapOf(
                CampoLlanta.NUMERO_LLANTA to ErrorCampo.EXCEDE_LONGITUD,
                CampoLlanta.DOT to ErrorCampo.EXCEDE_LONGITUD
            ),
            errores
        )
    }

    @Test
    fun validar_numeroYDotEnElLimiteConEspacios_sinErrores() {
        val errores = ValidadorFormularioLlanta.validar(
            completos.copy(numeroLlanta = " ${"1".repeat(15)} ", dot = "1".repeat(10)),
            adquisicionEditable = true
        )

        assertTrue(errores.isEmpty())
    }

    @Test
    fun validar_adquisicionBloqueada_soloValidaIdentificacion() {
        val errores = ValidadorFormularioLlanta.validar(
            CamposLlanta(numeroLlanta = ""),
            adquisicionEditable = false
        )

        assertEquals(mapOf(CampoLlanta.NUMERO_LLANTA to ErrorCampo.REQUERIDO), errores)
    }
}
