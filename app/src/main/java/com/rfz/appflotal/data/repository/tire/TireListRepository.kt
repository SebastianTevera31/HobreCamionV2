package com.rfz.appflotal.data.repository.tire

import com.rfz.appflotal.data.model.tire.response.TireListResponse
import com.rfz.appflotal.data.network.service.tire.TireListService
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class TireListRepository @Inject constructor(
    private val tireListService: TireListService,
    private val getTasksUseCase: GetTasksUseCase
) {
    suspend fun doTireList(): Result<List<TireListResponse>> {
        return try {
            val token = getTasksUseCase().first()[0].fld_token
            val response = tireListService.doTireList(token)
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