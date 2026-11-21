package com.rfz.appflotal.domain.sincronizacion

import com.rfz.appflotal.data.model.sincronizacion.EstadoOperacion
import com.rfz.appflotal.data.model.sincronizacion.OperacionPendiente
import com.rfz.appflotal.data.model.sincronizacion.TipoOperacion
import com.rfz.appflotal.domain.monitor.normalizarPosicion
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/** Por qué hoy ya no se puede inspeccionar una llanta. */
enum class BloqueoInspeccion { INSPECCIONADA_HOY, MONTADA_HOY }

/** Lo que ya ocurrió hoy en una posición; de ahí salen las operaciones permitidas. */
data class OperacionesDelDia(
    val inspeccionadaHoy: Boolean = false,
    val montadaHoy: Boolean = false,
) {
    val bloqueoInspeccion: BloqueoInspeccion?
        get() = when {
            inspeccionadaHoy -> BloqueoInspeccion.INSPECCIONADA_HOY
            montadaHoy -> BloqueoInspeccion.MONTADA_HOY
            else -> null
        }

    /** Una inspección al día, y ninguna el día en que se montó la llanta. */
    val puedeInspeccionar: Boolean get() = bloqueoInspeccion == null

    /** Con inspección o montaje de hoy, el desmontaje no la vuelve a pedir. */
    val desmontajeRequiereInspeccion: Boolean get() = bloqueoInspeccion == null
}

/**
 * [ultimaInspeccion] y [fechaMontaje] llegan como las guarda la app ("yyyy-MM-dd…" en hora
 * local). Una inspección en cola cuenta como hecha; una rechazada por el servidor, no.
 */
fun calcularOperacionesDelDia(
    posicion: String,
    ultimaInspeccion: String?,
    fechaMontaje: String?,
    cola: List<OperacionPendiente>,
    hoy: LocalDate,
    zona: ZoneId,
): OperacionesDelDia {
    val clave = posicion.normalizarPosicion()
    val inspeccionEnCola = cola.any {
        it.tipo == TipoOperacion.INSPECCION &&
            it.estado != EstadoOperacion.CON_ERROR &&
            it.posicion.normalizarPosicion() == clave &&
            Instant.ofEpochMilli(it.creadoEn).atZone(zona).toLocalDate() == hoy
    }
    return OperacionesDelDia(
        inspeccionadaHoy = inspeccionEnCola || esDelDia(ultimaInspeccion, hoy),
        montadaHoy = esDelDia(fechaMontaje, hoy)
    )
}

// Una fecha vacía o ilegible no bloquea: el día se toma de los primeros 10 caracteres.
private fun esDelDia(fecha: String?, dia: LocalDate): Boolean =
    fecha?.take(10)?.let { runCatching { LocalDate.parse(it) }.getOrNull() } == dia
