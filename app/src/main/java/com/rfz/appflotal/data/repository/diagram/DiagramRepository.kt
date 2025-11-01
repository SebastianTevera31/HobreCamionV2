package com.rfz.appflotal.data.repository.diagram

import com.rfz.appflotal.data.model.diagram.response.DiagramResponse
import com.rfz.appflotal.data.network.service.diagram.DiagramService
import javax.inject.Inject


class DiagramRepository @Inject constructor(private val diagramService: DiagramService) {

    suspend fun doDiagram(tok: String): Result<List<DiagramResponse>> {
        return try {
            val response = diagramService.doDiagram(tok)
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