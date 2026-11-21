package com.rfz.appflotal.domain.monitor

import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.repository.database.SensorDataTableRepository
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.tpms.ApiTpmsUseCase
import com.rfz.appflotal.domain.tpms.MonitorUnitConversionUseCase
import com.rfz.appflotal.domain.userpreferences.ObservePressureUnitUseCase
import com.rfz.appflotal.domain.userpreferences.ObserveTemperatureUnitUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/** Lectura de una posición, ya convertida a las unidades que eligió el usuario. */
data class LecturaPosicion(
    val posicion: String,
    val numeroLlanta: String,
    val fecha: String,
    val presion: Float,
    val temperatura: Float,
)

/** Convierte de PSI y °C a las unidades elegidas al momento de consultar. */
class ConvertirLecturaUseCase @Inject constructor(
    private val observarTemperatura: ObserveTemperatureUnitUseCase,
    private val observarPresion: ObservePressureUnitUseCase,
    private val conversion: MonitorUnitConversionUseCase,
) {
    suspend operator fun invoke(lecturas: List<LecturaPosicion>): List<LecturaPosicion> {
        val unidadTemperatura = observarTemperatura().first()
        val unidadPresion = observarPresion().first()
        return lecturas.map {
            val convertida = conversion(it.temperatura, unidadTemperatura, it.presion, unidadPresion)
            it.copy(temperatura = convertida.temperature, presion = convertida.pressure)
        }
    }
}

class ObtenerUltimasLecturasUseCase @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val sensorDataTableRepository: SensorDataTableRepository,
    private val convertir: ConvertirLecturaUseCase,
) {
    /** Solo llantas activas, por número de posición; null si el monitor no tiene lecturas. */
    suspend operator fun invoke(): List<LecturaPosicion>? {
        val monitorId = getTasksUseCase().first().firstOrNull()?.id_monitor ?: return null
        val registros = sensorDataTableRepository.getLastData(monitorId)
        if (registros.isEmpty()) return null

        val lecturas = registros.filter { it.active }.map {
            LecturaPosicion(it.tire, it.tireNumber, it.timestamp, it.pressure.toFloat(), it.temperature.toFloat())
        }
        return convertir(lecturas).sortedBy { it.posicion.numeroDePosicion() }
    }
}

class ObtenerHistorialPosicionUseCase @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val apiTpmsUseCase: ApiTpmsUseCase,
    private val convertir: ConvertirLecturaUseCase,
) {
    /** null si el servidor no respondió. */
    suspend operator fun invoke(posicion: String, fecha: String): List<LecturaPosicion>? {
        val monitorId = getTasksUseCase().first().firstOrNull()?.id_monitor ?: return null
        val respuesta = apiTpmsUseCase.doGetMonitorTireByDate(monitorId, posicion, fecha)
        if (respuesta !is ApiResult.Success) return null

        val lecturas = respuesta.data.orEmpty().map {
            LecturaPosicion(it.tirePosition, it.tireNumber, it.sensorDate, it.psi.toFloat(), it.temperature.toFloat())
        }
        return convertir(lecturas)
    }
}

private fun String.numeroDePosicion(): Int = replace("P", "").trim().toIntOrNull() ?: Int.MAX_VALUE
