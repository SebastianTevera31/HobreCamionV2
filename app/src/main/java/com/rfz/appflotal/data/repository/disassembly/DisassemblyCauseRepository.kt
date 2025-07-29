package com.rfz.appflotal.data.repository.disassembly

import com.rfz.appflotal.data.model.brand.dto.BrandCrudDto
import com.rfz.appflotal.data.model.brand.response.BranListResponse
import com.rfz.appflotal.data.model.disassembly.response.DisassemblyCauseResponse
import com.rfz.appflotal.data.network.service.brand.BrandCrudService
import com.rfz.appflotal.data.network.service.disassembly.DisassemblyCauseService
import javax.inject.Inject


class DisassemblyCauseRepository @Inject constructor(private val disassemblyCauseService: DisassemblyCauseService) {

    suspend fun doDisassemblyCause(tok: String): Result<List<DisassemblyCauseResponse>> {
        return try {
            val response = disassemblyCauseService.doDisassemblyCause(tok)
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