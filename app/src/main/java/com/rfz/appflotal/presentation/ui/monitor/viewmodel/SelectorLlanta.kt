package com.rfz.appflotal.presentation.ui.monitor.viewmodel

import com.rfz.appflotal.data.model.tpms.SensorAlerts
import com.rfz.appflotal.domain.monitor.DURACION_FIJADA_MS
import com.rfz.appflotal.domain.monitor.ModoSeleccion
import com.rfz.appflotal.domain.monitor.SeleccionLlanta
import com.rfz.appflotal.domain.monitor.alRecibir
import com.rfz.appflotal.domain.monitor.alTocar
import com.rfz.appflotal.domain.monitor.alVencer
import com.rfz.appflotal.domain.monitor.alertaPrincipal
import com.rfz.appflotal.domain.monitor.tieneAlertaPrioritaria
import com.rfz.appflotal.domain.tpms.LecturaLlanta
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * Lleva la llanta que muestra el panel del Monitor aplicando la regla de
 * domain/monitor/SeleccionLlanta, y el temporizador que suelta una llanta fijada.
 */
internal class SelectorLlanta(
    private val scope: CoroutineScope,
    /** Al vencer el tiempo de una llanta fijada; recibe la llanta mostrada después. */
    private val alVencer: (posicionMostrada: String?, cambio: Boolean) -> Unit,
) {
    var seleccion = SeleccionLlanta()
        private set

    /** Alerta más grave por posición, para nombrarla en el aviso. */
    private val alertaPorPosicion = mutableMapOf<String, SensorAlerts>()
    private var vencimiento: Job? = null

    /** Registra la trama; devuelve true si el panel debe mostrar esta lectura. */
    fun recibir(posicion: String, lectura: LecturaLlanta): Boolean {
        if (posicion.isEmpty()) return false
        val alerta = lectura.alertaPrincipal
        if (alerta != null) alertaPorPosicion[posicion] = alerta else alertaPorPosicion.remove(posicion)
        seleccion = seleccion.alRecibir(posicion, lectura.tieneAlertaPrioritaria)
        return seleccion.posicion == posicion
    }

    fun tocar(posicion: String) {
        seleccion = seleccion.alTocar(posicion, ahora = System.currentTimeMillis())
        vencimiento?.cancel()
        vencimiento = scope.launch {
            delay(DURACION_FIJADA_MS.milliseconds)
            val anterior = seleccion.posicion
            seleccion = seleccion.alVencer(ahora = System.currentTimeMillis())
            alVencer(seleccion.posicion, seleccion.posicion != anterior)
        }
    }

    fun volverAAutomatico(conservarPosicion: Boolean) {
        vencimiento?.cancel()
        seleccion = seleccion.copy(
            modo = ModoSeleccion.Automatica,
            posicion = if (conservarPosicion) seleccion.posicion else null,
            sinVer = emptyList()
        )
    }

    /** La llanta mostrada dejó de estar activa; la siguiente trama elige otra. */
    fun olvidarPosicion() {
        seleccion = seleccion.copy(posicion = null)
    }

    fun alertaSinVerMasReciente(): String? = seleccion.sinVer.lastOrNull()

    /** Solo con una llanta fijada: en automático la alerta ya toma el panel. */
    fun aviso(): AvisoAlertaUi? {
        val sinVer = seleccion.sinVer
        if (seleccion.modo !is ModoSeleccion.Fijada || sinVer.isEmpty()) return null
        val posicion = sinVer.last()
        return AvisoAlertaUi(
            posicion = posicion,
            alerta = alertaPorPosicion[posicion] ?: SensorAlerts.NO_DATA,
            adicionales = sinVer.size - 1,
            posiciones = sinVer.toSet()
        )
    }
}
