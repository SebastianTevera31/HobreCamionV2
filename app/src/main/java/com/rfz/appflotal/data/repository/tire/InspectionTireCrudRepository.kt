package com.rfz.appflotal.data.repository.tire

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.tire.dto.InspectionTireDto
import com.rfz.appflotal.data.network.service.tire.InspectionTireCrudService
import javax.inject.Inject



class InspectionTireCrudRepository @Inject constructor(private val inspectionTireCrudService: InspectionTireCrudService) {

    suspend fun doInspectionTire(requestBody: InspectionTireDto): Result<List<GeneralResponse>> {
        return try {
            val response = inspectionTireCrudService.doInspectionTire(requestBody)
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