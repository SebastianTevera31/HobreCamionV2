package com.rfz.appflotal.data.network.service.catalogo

import com.rfz.appflotal.data.dao.CatalogoDao
import com.rfz.appflotal.data.dao.OperacionPendienteDao
import com.rfz.appflotal.data.model.assembly.AssemblyTireDto
import com.rfz.appflotal.data.model.catalogo.CatalogoEntity
import com.rfz.appflotal.data.model.catalogo.DescargaCatalogoEntity
import com.rfz.appflotal.data.model.catalogo.LlantaEntity
import com.rfz.appflotal.data.model.catalogo.TipoDatoOffline
import com.rfz.appflotal.data.model.sincronizacion.TipoOperacion
import com.rfz.appflotal.data.model.sincronizacion.gsonPayload
import com.rfz.appflotal.di.dispatchers.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** Copia local de los catálogos que se necesitan para operar sin conexión. */
class LocalCatalogosDataSource @Inject constructor(
    private val catalogoDao: CatalogoDao,
    private val operacionDao: OperacionPendienteDao,
    @IoDispatcher private val io: CoroutineDispatcher,
) {
    suspend fun catalogo(tipo: TipoDatoOffline): List<CatalogoEntity> = withContext(io) {
        catalogoDao.obtener(tipo.name)
    }

    suspend fun guardarCatalogo(tipo: TipoDatoOffline, elementos: List<CatalogoEntity>) =
        withContext(io) {
            catalogoDao.reemplazar(tipo.name, elementos, descarga(tipo))
        }

    suspend fun borrarCatalogo(tipo: TipoDatoOffline) = withContext(io) {
        catalogoDao.borrar(tipo.name)
    }

    suspend fun llantas(): List<LlantaEntity> = withContext(io) { catalogoDao.obtenerLlantas() }

    suspend fun guardarLlantas(llantas: List<LlantaEntity>) = withContext(io) {
        catalogoDao.reemplazarLlantas(llantas, descarga(TipoDatoOffline.LLANTA))
    }

    /** Llantas montadas sin red: el servidor aún las tiene en stock, pero ya no están disponibles. */
    suspend fun llantasMontadasEnCola(): Set<Int> = withContext(io) {
        operacionDao.todas()
            .filter { it.tipo == TipoOperacion.MONTAJE }
            .mapNotNull { runCatching { gsonPayload.fromJson(it.payload, AssemblyTireDto::class.java).idTire }.getOrNull() }
            .toSet()
    }

    fun observarDescargas(): Flow<Map<TipoDatoOffline, Long>> =
        catalogoDao.observarDescargas().map { lista -> lista.associate { it.tipo to it.descargadoEn } }

    private fun descarga(tipo: TipoDatoOffline) =
        DescargaCatalogoEntity(tipo = tipo, descargadoEn = System.currentTimeMillis())
}
