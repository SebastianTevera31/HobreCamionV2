package com.rfz.appflotal.domain.monitor

import com.rfz.appflotal.data.repository.assembly.AssemblyTireRepository
import com.rfz.appflotal.data.repository.monitor.DiagramaMonitorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Posiciones del diagrama. Con red se piden al servidor y se guardan en caché; sin red, o si el
 * servidor no responde (señal débil), se usa la caché.
 */
class CargarDiagramaUseCase @Inject constructor(
    private val diagrama: DiagramaMonitorRepository,
    private val montajes: AssemblyTireRepository,
) {
    suspend operator fun invoke(monitorId: Int, conRed: Boolean): List<LlantaDiagrama> {
        if (conRed) {
            runCatching { montajes.refreshMountedTires() }
            val delServidor = diagrama.obtenerDelServidor(monitorId)
            if (delServidor != null) {
                // Sin llantas no se toca la caché anterior.
                if (delServidor.isNotEmpty()) diagrama.guardarEnCache(monitorId, delServidor)
                return delServidor
            }
        }
        return diagrama.obtenerDeCache(monitorId)
    }
}

/** Posiciones montadas según la tabla local de montajes (la fuente de verdad), normalizadas. */
class ObservarPosicionesMontadasUseCase @Inject constructor(
    private val montajes: AssemblyTireRepository,
) {
    operator fun invoke(): Flow<Set<String>> =
        flow { emitAll(montajes.observeAssemblyTire()) }
            .map { lista -> lista.map { it.positionTire.normalizarPosicion() }.toSet() }
}

class ObservarLlantasActivasUseCase @Inject constructor(
    private val diagrama: DiagramaMonitorRepository,
) {
    operator fun invoke(): Flow<Map<String, Boolean>> = diagrama.observarActivas()
}
