package com.rfz.appflotal.data.repository.controltype

import com.rfz.appflotal.data.model.controltype.response.ControlTypeResponse
import com.rfz.appflotal.data.network.service.controltype.ControlTypeService
import javax.inject.Inject



class ControlTypeRepository @Inject constructor(private val controlTypeService: ControlTypeService) {

    suspend fun doControlType(tok: String): Result<List<ControlTypeResponse>> {
        return try {
            val response = controlTypeService.doControlType(tok)
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