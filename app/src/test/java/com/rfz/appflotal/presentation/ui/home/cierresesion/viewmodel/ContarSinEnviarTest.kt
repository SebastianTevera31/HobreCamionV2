package com.rfz.appflotal.presentation.ui.home.cierresesion.viewmodel

import com.rfz.appflotal.data.model.sincronizacion.EstadoOperacion
import com.rfz.appflotal.data.model.sincronizacion.OperacionPendiente
import com.rfz.appflotal.data.model.sincronizacion.TipoOperacion
import org.junit.Assert.assertEquals
import org.junit.Test

class ContarSinEnviarTest {

    private fun operacion(id: Long, tipo: TipoOperacion, idGrupo: String? = null) =
        OperacionPendiente(id, tipo, "P3", idGrupo, EstadoOperacion.PENDIENTE, 0, null, 0)

    @Test
    fun contarSinEnviar_desmontajeConInspeccionYMontaje_cuentaDos() {
        val operaciones = listOf(
            operacion(1, TipoOperacion.INSPECCION, "g"),
            operacion(2, TipoOperacion.DESMONTAJE, "g"),
            operacion(3, TipoOperacion.MONTAJE)
        )

        assertEquals(2, contarSinEnviar(operaciones))
    }
}
