package com.rfz.appflotal.data.repository.vehicle

import com.rfz.appflotal.data.model.vehicle.response.VehicleListResponse
import com.rfz.appflotal.data.network.service.vehicle.VehicleListService
import javax.inject.Inject


class VehicleListRepository @Inject constructor(private val vehicleListService: VehicleListService) {

    suspend fun doVehicleList( tok: String): Result<List<VehicleListResponse>> {
        return try {
            val response = vehicleListService.doVehicleList(tok)
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