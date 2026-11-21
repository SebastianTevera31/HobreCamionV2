package com.rfz.appflotal.domain.monitor

import com.rfz.appflotal.data.model.tpms.SensorAlerts
import com.rfz.appflotal.domain.tpms.LecturaLlanta

/** Tiempo que una llanta tocada por el usuario se queda en el panel antes de volver a automático. */
const val DURACION_FIJADA_MS = 60_000L

sealed interface ModoSeleccion {
    /** El panel sigue las tramas que llegan, con prioridad para las alertas. */
    data object Automatica : ModoSeleccion

    /** El usuario tocó una llanta; las tramas de otras no le quitan el panel hasta [hasta]. */
    data class Fijada(val hasta: Long) : ModoSeleccion
}

/** Qué llanta muestra el panel y qué alertas compiten por él. */
data class SeleccionLlanta(
    val modo: ModoSeleccion = ModoSeleccion.Automatica,
    val posicion: String? = null,
    /** Posiciones cuya última trama trajo una alerta prioritaria. */
    val enAlerta: Set<String> = emptySet(),
    /** Alertas que llegaron con otra llanta fijada, de la más antigua a la más reciente. */
    val sinVer: List<String> = emptyList(),
)

/**
 * Presión, temperatura, pinchazo y extracción le quitan el panel a otra llanta; la batería baja
 * solo ilumina su llanta, porque no exige atención inmediata.
 */
val LecturaLlanta.tieneAlertaPrioritaria: Boolean
    get() = alertaPresion != SensorAlerts.NO_DATA ||
        alertaTemperatura != SensorAlerts.NO_DATA ||
        pinchazo != SensorAlerts.NO_DATA ||
        extraccion != SensorAlerts.NO_DATA

/** La alerta que se nombra en el aviso cuando una llanta tiene varias, de la más grave a la menos. */
val LecturaLlanta.alertaPrincipal: SensorAlerts?
    get() = listOf(pinchazo, extraccion, alertaPresion, alertaTemperatura, bateria)
        .firstOrNull { it != SensorAlerts.NO_DATA }

/** Llega la trama de [posicion]. Una trama sin posición (sensor sin lectura) no cambia nada. */
fun SeleccionLlanta.alRecibir(posicion: String, alertaPrioritaria: Boolean): SeleccionLlanta {
    if (posicion.isBlank()) return this
    val enAlerta = if (alertaPrioritaria) enAlerta + posicion else enAlerta - posicion
    val sinVerActual = if (alertaPrioritaria) sinVer else sinVer - posicion

    return when (modo) {
        ModoSeleccion.Automatica -> {
            // Una llanta mostrada en alerta solo la desplaza otra alerta, más reciente.
            val conservarActual = !alertaPrioritaria && posicion != this.posicion &&
                this.posicion != null && this.posicion in enAlerta
            copy(
                posicion = if (conservarActual) this.posicion else posicion,
                enAlerta = enAlerta,
                sinVer = sinVerActual
            )
        }

        is ModoSeleccion.Fijada -> {
            val nuevasSinVer = if (alertaPrioritaria && posicion != this.posicion) {
                sinVerActual - posicion + posicion
            } else sinVerActual
            copy(enAlerta = enAlerta, sinVer = nuevasSinVer)
        }
    }
}

/** El usuario toca una llanta (o "Ver" en el aviso): queda fija [DURACION_FIJADA_MS]. */
fun SeleccionLlanta.alTocar(posicion: String, ahora: Long): SeleccionLlanta = copy(
    modo = ModoSeleccion.Fijada(hasta = ahora + DURACION_FIJADA_MS),
    posicion = posicion,
    sinVer = sinVer - posicion
)

/** Al vencer el tiempo vuelve a automático y, si quedó una alerta sin ver, salta a la más reciente. */
fun SeleccionLlanta.alVencer(ahora: Long): SeleccionLlanta {
    val fijada = modo as? ModoSeleccion.Fijada ?: return this
    if (ahora < fijada.hasta) return this
    return copy(
        modo = ModoSeleccion.Automatica,
        posicion = sinVer.lastOrNull() ?: posicion,
        sinVer = emptyList()
    )
}
