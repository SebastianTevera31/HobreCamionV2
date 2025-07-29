package com.rfz.appflotal.data.repository.tire

import com.rfz.appflotal.data.model.tire.response.TireInspectionReportResponse
import com.rfz.appflotal.data.model.tire.response.TireListResponse
import com.rfz.appflotal.data.network.service.tire.TireGetService
import com.rfz.appflotal.data.network.service.tire.TireListService
import javax.inject.Inject



class TireListRepository @Inject constructor(private val tireListService: TireListService) {

    suspend fun doTireList( tok: String): Result<List<TireListResponse>> {
        return try {
            val response = tireListService.doTireList(tok)
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