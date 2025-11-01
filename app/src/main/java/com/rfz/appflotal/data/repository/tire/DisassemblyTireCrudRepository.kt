package com.rfz.appflotal.data.repository.tire

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.tire.dto.DisassemblyTireDto
import com.rfz.appflotal.data.network.service.tire.DisassemblyTireCrudService
import javax.inject.Inject


class DisassemblyTireCrudRepository @Inject constructor(private val disassemblyTireCrudService: DisassemblyTireCrudService) {

    suspend fun doBrandCrud(requestBody: DisassemblyTireDto, tok: String): Result<List<MessageResponse>> {
        return try {
            val response = disassemblyTireCrudService.doDisassemblyTire(requestBody,tok)
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