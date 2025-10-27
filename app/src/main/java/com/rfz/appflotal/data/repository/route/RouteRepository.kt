package com.rfz.appflotal.data.repository.route

import com.rfz.appflotal.data.model.route.response.RouteResponse
import com.rfz.appflotal.data.network.service.route.RouteService
import javax.inject.Inject


class RouteRepository @Inject constructor(private val routeService: RouteService) {

    suspend fun doRoute(tok: String): Result<List<RouteResponse>> {
        return try {
            val response = routeService.doRoute(tok)
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