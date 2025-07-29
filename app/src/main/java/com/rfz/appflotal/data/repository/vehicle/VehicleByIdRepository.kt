package com.rfz.appflotal.data.repository.vehicle

import com.rfz.appflotal.data.model.brand.dto.BrandCrudDto
import com.rfz.appflotal.data.model.brand.response.BranListResponse
import com.rfz.appflotal.data.model.vehicle.response.VehicleIdResponse
import com.rfz.appflotal.data.network.service.brand.BrandCrudService
import com.rfz.appflotal.data.network.service.vehicle.VehicleByIdService
import javax.inject.Inject


class VehicleByIdRepository @Inject constructor(private val vehicleByIdService: VehicleByIdService) {

    suspend fun doGetVehicleById(tok: String,id_vehicle: Int): Result<List<VehicleIdResponse>> {
        return try {
            val response = vehicleByIdService.doGetVehicleById(tok,id_vehicle)
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