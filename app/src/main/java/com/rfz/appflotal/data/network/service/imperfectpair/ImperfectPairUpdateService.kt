package com.rfz.appflotal.data.network.service.imperfectpair

import com.rfz.appflotal.data.model.imperfectpair.ApiResponse
import com.rfz.appflotal.data.model.imperfectpair.dto.ImperfectPairUpdateRequest
import com.rfz.appflotal.data.network.client.imperfectpair.ImperfectPairUpdateClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class ImperfectPairUpdateService @Inject constructor(
    private val client: ImperfectPairUpdateClient
) {
    suspend fun updateImperfectPair(
        token: String,
        request: ImperfectPairUpdateRequest
    ): Response<List<ApiResponse>> {
        return withContext(Dispatchers.IO) {
            client.updateImperfectPair(token, request)
        }
    }
}
