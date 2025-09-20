package com.rfz.appflotal.data.repository.imperfectpair

import com.rfz.appflotal.data.model.imperfectpair.ApiResponse
import com.rfz.appflotal.data.model.imperfectpair.dto.ImperfectPairUpdateRequest
import com.rfz.appflotal.data.network.service.imperfectpair.ImperfectPairUpdateService
import javax.inject.Inject

class ImperfectPairUpdateRepository @Inject constructor(
    private val service: ImperfectPairUpdateService
) {
    suspend fun updateImperfectPair(
        token: String,
        request: ImperfectPairUpdateRequest
    ): Result<ApiResponse> {
        return try {
            val response = service.updateImperfectPair(token, request)
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
