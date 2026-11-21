package com.rfz.appflotal.data.repository.monitor

import com.rfz.appflotal.data.dao.CoordinatesDao
import com.rfz.appflotal.data.model.database.CoordinatesEntity
import com.rfz.appflotal.data.model.tpms.DiagramMonitorResponse
import com.rfz.appflotal.data.model.tpms.PositionCoordinatesResponse
import com.rfz.appflotal.data.network.service.ApiResult
import com.rfz.appflotal.data.repository.database.SensorDataTableRepository
import com.rfz.appflotal.data.repository.tpms.ApiTpmsRepository
import com.rfz.appflotal.di.dispatchers.IoDispatcher
import com.rfz.appflotal.domain.monitor.LlantaDiagrama
import com.rfz.appflotal.domain.monitor.normalizarPosicion
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** Posiciones del diagrama: del servidor, de la caché local y su estado activo en sensor_data. */
interface DiagramaMonitorRepository {
    /** null si alguna de las dos llamadas al servidor falló. */
    suspend fun obtenerDelServidor(monitorId: Int): List<LlantaDiagrama>?
    suspend fun obtenerDeCache(monitorId: Int): List<LlantaDiagrama>
    suspend fun guardarEnCache(monitorId: Int, llantas: List<LlantaDiagrama>)

    /** Posición → activa, según los últimos registros de sensor_data del monitor de la sesión. */
    fun observarActivas(): Flow<Map<String, Boolean>>
}

class DiagramaMonitorRepositoryImpl @Inject constructor(
    private val apiTpmsRepository: ApiTpmsRepository,
    private val coordinatesDao: CoordinatesDao,
    private val sensorDataTableRepository: SensorDataTableRepository,
    @IoDispatcher private val io: CoroutineDispatcher,
) : DiagramaMonitorRepository {

    override suspend fun obtenerDelServidor(monitorId: Int): List<LlantaDiagrama>? = coroutineScope {
        val sensores = async { apiTpmsRepository.doGetDiagramMonitor(monitorId) }
        val coordenadas = async { apiTpmsRepository.doGetPositionCoordinates(monitorId) }
        val respuestaSensores = sensores.await()
        val respuestaCoordenadas = coordenadas.await()
        if (respuestaSensores !is ApiResult.Success || respuestaCoordenadas !is ApiResult.Success) {
            return@coroutineScope null
        }
        unirDiagrama(respuestaCoordenadas.data.orEmpty(), respuestaSensores.data.orEmpty())
    }

    // Instalaciones anteriores pueden tener la tabla duplicada; a la UI llega una fila por posición.
    override suspend fun obtenerDeCache(monitorId: Int): List<LlantaDiagrama> = withContext(io) {
        coordinatesDao.getCoordinates(monitorId).distinctBy { it.idPosition }.map { it.aLlanta() }
    }

    override suspend fun guardarEnCache(monitorId: Int, llantas: List<LlantaDiagrama>) =
        withContext(io) {
            coordinatesDao.replaceCoordinates(
                monitorId,
                llantas.distinctBy { it.posicion }.map { it.aEntidad(monitorId) }
            )
        }

    override fun observarActivas(): Flow<Map<String, Boolean>> =
        flow { emitAll(sensorDataTableRepository.observeLastRecords()) }
            .map { registros -> registros.associate { it.tire to it.active } }
}

/**
 * Una posición por coordenada (alimenta un LazyVerticalGrid con key = posición, que truena si
 * se repite), con alerta, montaje y sensor según el diagrama del servidor.
 */
internal fun unirDiagrama(
    coordenadas: List<PositionCoordinatesResponse>,
    sensores: List<DiagramMonitorResponse>,
): List<LlantaDiagrama> {
    val sensorPorPosicion = sensores.associateBy { it.sensorPosition.normalizarPosicion() }
    return coordenadas.distinctBy { it.position }.map { coordenada ->
        val sensor = sensorPorPosicion[coordenada.position]
        LlantaDiagrama(
            posicion = sensor?.sensorPosition ?: coordenada.position,
            enAlerta = sensor != null && (sensor.highTemperature || sensor.highPressure ||
                sensor.lowPressure || sensor.lowBattery || sensor.puncture),
            montada = sensor?.isAssembled == true,
            activa = sensor?.sensorId != 0,
            x = coordenada.fldPositionX,
            y = coordenada.fldPositionY,
        )
    }.sortedBy { it.posicion.removePrefix("P").trim().toIntOrNull() ?: Int.MAX_VALUE }
}

private fun CoordinatesEntity.aLlanta() = LlantaDiagrama(
    posicion = idPosition,
    enAlerta = inAlert,
    montada = isAssembled,
    activa = isActive,
    x = xPosition,
    y = yPosition,
)

private fun LlantaDiagrama.aEntidad(monitorId: Int) = CoordinatesEntity(
    monitorId = monitorId,
    idPosition = posicion,
    isAssembled = montada,
    inAlert = enAlerta,
    isActive = activa,
    xPosition = x,
    yPosition = y,
)
