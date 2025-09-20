package com.rfz.appflotal.data.repository.imperfectpair

import com.rfz.appflotal.data.model.imperfectpair.ApiResponse
import com.rfz.appflotal.data.model.imperfectpair.dto.ImperfectPairCreateRequest
import com.rfz.appflotal.data.network.service.imperfectpair.ImperfectPairCreateService
import javax.inject.Inject

class ImperfectPairCreateRepository @Inject constructor(
    private val service: ImperfectPairCreateService
) {
    suspend fun createImperfectPair(
        token: String,
        request: ImperfectPairCreateRequest
    ): Result<ApiResponse> {
        return try {
            val response = service.createImperfectPair(token, request)
            if (response.isSuccessful) {
                response.body()?.firstOrNull()?.let {
                    Result.success(it)
                } ?: Result.failure(Throwable("Respuesta vacía"))
            } else {
                Result.failure(Throwable("Error ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
