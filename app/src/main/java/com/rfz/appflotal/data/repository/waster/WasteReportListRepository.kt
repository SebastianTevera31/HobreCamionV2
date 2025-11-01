package com.rfz.appflotal.data.repository.waster

import com.rfz.appflotal.data.model.waster.response.WasteReportListResponse
import com.rfz.appflotal.data.network.service.waster.WasteReportListService
import javax.inject.Inject


class WasteReportListRepository @Inject constructor(private val wasteReportListService: WasteReportListService) {

    suspend fun doWasteReportList(tok: String): Result<List<WasteReportListResponse>> {
        return try {
            val response = wasteReportListService.doWasteReportList(tok)
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