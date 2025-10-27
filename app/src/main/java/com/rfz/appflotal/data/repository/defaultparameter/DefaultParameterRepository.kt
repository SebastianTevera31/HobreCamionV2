package com.rfz.appflotal.data.repository.defaultparameter

import com.rfz.appflotal.data.model.defaultparameter.response.DefaultParameterResponse
import com.rfz.appflotal.data.network.service.defaultparameter.DefaultParameterService
import javax.inject.Inject


class DefaultParameterRepository @Inject constructor(private val defaultParameterService: DefaultParameterService) {

    suspend fun doDefaultParameter( tok: String, id_user:Int): Result<List<DefaultParameterResponse>> {
        return try {
            val response = defaultParameterService.doDefaultParameter(tok,id_user)
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