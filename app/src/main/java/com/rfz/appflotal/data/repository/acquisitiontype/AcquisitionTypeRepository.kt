package com.rfz.appflotal.data.repository.acquisitiontype

import com.rfz.appflotal.data.model.acquisitiontype.response.AcquisitionTypeResponse
import com.rfz.appflotal.data.network.service.acquisitiontype.AcquisitionTypeService
import javax.inject.Inject


class AcquisitionTypeRepository @Inject constructor(private val acquisitionTypeService: AcquisitionTypeService) {

    suspend fun doAcquisitionType(tok: String): Result<List<AcquisitionTypeResponse>> {
        return try {
            val response = acquisitionTypeService.doAcquisitionType(tok)
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