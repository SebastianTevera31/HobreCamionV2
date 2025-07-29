package com.rfz.appflotal.data.repository.tire

import com.rfz.appflotal.data.model.brand.dto.BrandCrudDto
import com.rfz.appflotal.data.model.brand.response.BranListResponse
import com.rfz.appflotal.data.model.tire.response.TireInspectionReportResponse
import com.rfz.appflotal.data.model.tire.response.TirexIdResponse
import com.rfz.appflotal.data.network.service.brand.BrandCrudService
import com.rfz.appflotal.data.network.service.tire.TireGetService
import javax.inject.Inject


class TireGetRepository @Inject constructor(private val tireGetService: TireGetService) {

    suspend fun doTireGet(id_tire: Int, tok: String): Result<List<TirexIdResponse>> {
        return try {
            val response = tireGetService.doTireGet(id_tire,tok)
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