package com.rfz.appflotal.data.repository.tire

import com.rfz.appflotal.data.model.catalogo.aEntidad
import com.rfz.appflotal.data.model.catalogo.aRespuesta
import com.rfz.appflotal.data.model.tire.response.TireListResponse
import com.rfz.appflotal.data.network.service.catalogo.LocalCatalogosDataSource
import com.rfz.appflotal.data.network.service.tire.TireListService
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/** Inventario de llantas que la cola de envíos necesita refrescar tras sincronizar. */
interface InventarioLlantasRepository {
    /** Descarga el inventario y lo guarda; false si el servidor no respondió. */
    suspend fun actualizar(): Boolean
}

class TireListRepository @Inject constructor(
    private val tireListService: TireListService,
    private val getTasksUseCase: GetTasksUseCase,
    private val local: LocalCatalogosDataSource,
) : InventarioLlantasRepository {

    /**
     * Del servidor (y se guarda); sin red, lo guardado. Las llantas montadas sin red se quitan:
     * el servidor aún no sabe que dejaron el stock.
     */
    suspend fun doTireList(): Result<List<TireListResponse>> {
        val remoto = pedirAlServidor()
        // Si guardar falla, la lista del servidor se muestra igual; solo se pierde la copia offline.
        val llantas = remoto.getOrNull()?.also { runCatching { guardar(it) } }
            ?: local.llantas().takeIf { it.isNotEmpty() }?.map { it.aRespuesta() }
            ?: return remoto
        val montadasEnCola = local.llantasMontadasEnCola()
        return Result.success(llantas.filterNot { it.idTire in montadasEnCola })
    }

    override suspend fun actualizar(): Boolean =
        pedirAlServidor().onSuccess { guardar(it) }.isSuccess

    private suspend fun guardar(llantas: List<TireListResponse>) =
        local.guardarLlantas(llantas.map { it.aEntidad() })

    private suspend fun pedirAlServidor(): Result<List<TireListResponse>> {
        return try {
            val token = getTasksUseCase().first()[0].fld_token
            val response = tireListService.doTireList(token)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Throwable("Error: Cuerpo de la respuesta nulo"))
            } else {
                when (response.code()) {
                    401 -> Result.failure(Throwable("Error 401: No autorizado"))
                    else -> Result.failure(Throwable("Error en la respuesta del servidor: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
