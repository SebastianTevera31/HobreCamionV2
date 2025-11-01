package com.rfz.appflotal.data.repository.vehicle

import com.rfz.appflotal.data.model.vehicle.response.TypeVehicleResponse
import com.rfz.appflotal.data.network.service.vehicle.VehicleTypeService
import javax.inject.Inject


class VehicleTypeRepository @Inject constructor(private val vehicleTypeService: VehicleTypeService) {

    suspend fun doTypeVehicle(tok: String): Result<List<TypeVehicleResponse>> {
        return try {
            val response = vehicleTypeService.doTypeVehicle(tok)
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