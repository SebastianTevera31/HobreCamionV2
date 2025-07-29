package com.rfz.appflotal.data.repository.languaje

import com.rfz.appflotal.data.model.defaultparameter.response.DefaultParameterResponse
import com.rfz.appflotal.data.model.languaje.LanguageResponse
import com.rfz.appflotal.data.network.service.defaultparameter.DefaultParameterService
import com.rfz.appflotal.data.network.service.languaje.LanguajeService
import javax.inject.Inject



class LanguajeRepository @Inject constructor(private val languajeService: LanguajeService) {

    suspend fun dolanguaje( tok: String, lang:String): Result<LanguageResponse> {
        return try {
            val response = languajeService.dolanguaje(tok,lang)
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