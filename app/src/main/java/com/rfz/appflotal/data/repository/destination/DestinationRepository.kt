package com.rfz.appflotal.data.repository.destination

import com.rfz.appflotal.core.util.AppLocale
import com.rfz.appflotal.data.model.catalogo.TipoDatoOffline
import com.rfz.appflotal.data.model.catalogo.aCatalogo
import com.rfz.appflotal.data.model.catalogo.aDestinoRespuesta
import com.rfz.appflotal.data.model.destination.Destination
import com.rfz.appflotal.data.model.destination.response.DestinationResponse
import com.rfz.appflotal.data.model.destination.toDomain
import com.rfz.appflotal.data.network.service.catalogo.LocalCatalogosDataSource
import com.rfz.appflotal.data.network.service.destination.DestinationService
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class DestinationRepository @Inject constructor(
    private val destinationService: DestinationService,
    private val getTasksUseCase: GetTasksUseCase,
    private val local: LocalCatalogosDataSource,
) {

    /** Del servidor (y se guarda); sin red, lo guardado. */
    suspend fun doDestination(): Result<List<Destination>> {
        val remoto = pedirAlServidor()
        val destinos = remoto.getOrNull()?.also { guardar(it) }
            ?: local.catalogo(TipoDatoOffline.DESTINO).takeIf { it.isNotEmpty() }
                ?.map { it.aDestinoRespuesta() }
            ?: return Result.failure(remoto.exceptionOrNull() ?: Throwable("Sin destinos"))

        val language = AppLocale.currentLocale.first().language
        return Result.success(destinos.map { destinationResponse ->
            destinationResponse.toDomain().copy(
                description = if (language.contains("es")) destinationResponse.fldDescription
                else destinationResponse.fldEsDescription
            )
        })
    }

    suspend fun actualizar(): Boolean = pedirAlServidor().onSuccess { guardar(it) }.isSuccess

    private suspend fun guardar(destinos: List<DestinationResponse>) =
        local.guardarCatalogo(TipoDatoOffline.DESTINO, destinos.map { it.aCatalogo() })

    private suspend fun pedirAlServidor(): Result<List<DestinationResponse>> {
        return try {
            val token = getTasksUseCase().first().first().fld_token
            val response = destinationService.doDestination(token)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Throwable("Error: Cuerpo de la respuesta nulo"))
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
