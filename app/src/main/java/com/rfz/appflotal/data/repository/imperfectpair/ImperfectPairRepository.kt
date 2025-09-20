package com.rfz.appflotal.data.repository.imperfectpair

import com.rfz.appflotal.data.model.imperfectpair.ImperfectPairResponse
import com.rfz.appflotal.data.network.service.imperfectpair.ImperfectPairService
import javax.inject.Inject

class ImperfectPairRepository @Inject constructor(
    private val service: ImperfectPairService
) {
    suspend fun getImperfectPairs(token: String): Result<List<ImperfectPairResponse>> {
        return try {
            val response = service.getImperfectPairs(token)
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
