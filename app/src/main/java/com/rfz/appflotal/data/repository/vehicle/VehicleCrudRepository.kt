package com.rfz.appflotal.data.repository.vehicle

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.vehicle.dto.VehicleCrudDto
import com.rfz.appflotal.data.network.service.vehicle.VehicleCrudService
import javax.inject.Inject



class VehicleCrudRepository @Inject constructor(private val vehicleCrudService: VehicleCrudService) {

    suspend fun doCrudVehicle(requestBody: VehicleCrudDto, tok:String): Result<GeneralResponse> {
        return try {
            val response = vehicleCrudService.doCrudVehicle(requestBody,tok)
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