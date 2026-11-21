package com.rfz.appflotal.domain.sincronizacion

import com.rfz.appflotal.data.repository.assembly.AssemblyTireRepository
import com.rfz.appflotal.data.repository.database.SensorDataTableRepository
import com.rfz.appflotal.data.repository.sincronizacion.ColaSincronizacionRepository
import com.rfz.appflotal.domain.monitor.normalizarPosicion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

/**
 * Operaciones de hoy por posición normalizada. Una posición que no aparece en el mapa no tiene
 * nada hoy. La última inspección viene de sensor_data (diagrama del servidor o inspección aceptada).
 */
class ObservarOperacionesDelDiaUseCase @Inject constructor(
    private val sensores: SensorDataTableRepository,
    private val montajes: AssemblyTireRepository,
    private val cola: ColaSincronizacionRepository,
) {
    operator fun invoke(): Flow<Map<String, OperacionesDelDia>> = combine(
        flow { emitAll(sensores.observeLastRecords()) },
        flow { emitAll(montajes.observeAssemblyTire()) },
        cola.observarOperaciones()
    ) { registros, montadas, operaciones ->
        val zona = ZoneId.systemDefault()
        val hoy = LocalDate.now(zona)
        val ultimaInspeccion = registros.associate { it.tire.normalizarPosicion() to it.lastInspection }
        val fechaMontaje = montadas.associate { it.positionTire.normalizarPosicion() to it.assemblyDate }
        (ultimaInspeccion.keys + fechaMontaje.keys).associateWith { posicion ->
            calcularOperacionesDelDia(
                posicion = posicion,
                ultimaInspeccion = ultimaInspeccion[posicion],
                fechaMontaje = fechaMontaje[posicion],
                cola = operaciones,
                hoy = hoy,
                zona = zona
            )
        }
    }
}

class ConsultarOperacionesDelDiaUseCase @Inject constructor(
    private val observar: ObservarOperacionesDelDiaUseCase,
) {
    /** Si no se puede leer, se asume que no hubo nada: el desmontaje pedirá la inspección. */
    suspend operator fun invoke(posicion: String): OperacionesDelDia =
        runCatching { observar().first()[posicion.normalizarPosicion()] }.getOrNull()
            ?: OperacionesDelDia()
}
