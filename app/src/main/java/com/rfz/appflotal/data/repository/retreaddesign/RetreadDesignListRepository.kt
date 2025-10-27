package com.rfz.appflotal.data.repository.retreaddesign

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.network.service.retreaddesign.RetreadDesignListService
import javax.inject.Inject


class RetreadDesignListRepository @Inject constructor(private val retreadDesignListService: RetreadDesignListService) {

    suspend fun doBrandCrud(): Result<List<MessageResponse>> {
        return try {
            val response = retreadDesignListService.doCrudRetreadDesignList()
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