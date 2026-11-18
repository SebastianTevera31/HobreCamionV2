package com.rfz.appflotal.data.network.service.sincronizacion

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.sincronizacion.ResultadoEnvio
import com.rfz.appflotal.data.network.service.DataError
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException

/**
 * Contrato confirmado: las tres operaciones responden HTTP 200 y aceptan con id = 200.
 * Falta un ejemplo real del cuerpo de rechazo para probar ese caso.
 */
class ResultadoEnvioTest {

    @Test
    fun aResultadoEnvio_idAceptado_devuelveAceptada() {
        val respuesta = Result.success(listOf(GeneralResponse(id = 200, message = "")))

        assertEquals(ResultadoEnvio.Aceptada, respuesta.aResultadoEnvio())
    }

    @Test
    fun aResultadoEnvio_http401_devuelveSesionVencida() {
        val respuesta = Result.failure<List<GeneralResponse>>(DataError.Http(401, null))

        assertEquals(ResultadoEnvio.SesionVencida, respuesta.aResultadoEnvio())
    }

    @Test
    fun aResultadoEnvio_sinRed_devuelveErrorConexion() {
        val respuesta = Result.failure<List<GeneralResponse>>(DataError.Network(IOException()))

        assertEquals(ResultadoEnvio.ErrorConexion, respuesta.aResultadoEnvio())
    }

    @Test
    fun aResultadoEnvio_http500_devuelveErrorConexion() {
        val respuesta = Result.failure<List<GeneralResponse>>(DataError.Http(500, null))

        assertEquals(ResultadoEnvio.ErrorConexion, respuesta.aResultadoEnvio())
    }
}
