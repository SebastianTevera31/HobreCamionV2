package com.rfz.appflotal.domain.catalogo

import com.rfz.appflotal.data.model.catalogo.TipoDatoOffline
import com.rfz.appflotal.data.repository.catalog.CatalogRepository
import com.rfz.appflotal.data.repository.destination.DestinationRepository
import com.rfz.appflotal.data.repository.disassembly.DisassemblyTireRepository
import com.rfz.appflotal.data.repository.tire.TireListRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Descarga llantas, destinos, causas y reportes para montar, desmontar e inspeccionar sin red. */
class ActualizarDatosOfflineUseCase @Inject constructor(
    private val llantas: TireListRepository,
    private val destinos: DestinationRepository,
    private val desmontaje: DisassemblyTireRepository,
    private val catalogos: CatalogRepository,
) {
    /** true si se actualizaron todos; si alguno falla, se conserva lo que ya estaba guardado. */
    suspend operator fun invoke(): Boolean = coroutineScope {
        listOf(
            async { runCatching { llantas.actualizar() }.getOrDefault(false) },
            async { runCatching { destinos.actualizar() }.getOrDefault(false) },
            async { runCatching { desmontaje.actualizar() }.getOrDefault(false) },
            async { runCatching { catalogos.refreshTireReports() }.getOrDefault(false) },
        ).awaitAll().all { it }
    }
}

class ObservarDatosOfflineUseCase @Inject constructor(
    private val catalogos: CatalogRepository,
) {
    /** La descarga más antigua de los cuatro; null si falta alguno por descargar. */
    operator fun invoke(): Flow<Long?> = catalogos.observarDescargas().map { descargas ->
        if (TipoDatoOffline.entries.all { it in descargas }) descargas.values.min() else null
    }
}
