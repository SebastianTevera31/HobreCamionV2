package com.rfz.appflotal.data.repository.scrap

import com.rfz.appflotal.data.model.scrap.response.ScrapReportResponse
import com.rfz.appflotal.data.network.service.scrap.ScrapReportService
import javax.inject.Inject


class ScrapReportRepository @Inject constructor(private val scrapReportService: ScrapReportService) {

    suspend fun doScrapReport(tok: String): Result<List<ScrapReportResponse>> {
        return try {
            val response = scrapReportService.doScrapReport(tok)
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