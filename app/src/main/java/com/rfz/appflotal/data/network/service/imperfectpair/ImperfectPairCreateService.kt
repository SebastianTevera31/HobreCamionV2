package com.rfz.appflotal.data.network.service.imperfectpair

import com.rfz.appflotal.data.model.imperfectpair.ApiResponse
import com.rfz.appflotal.data.model.imperfectpair.dto.ImperfectPairCreateRequest
import com.rfz.appflotal.data.network.client.imperfectpair.ImperfectPairCreateClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class ImperfectPairCreateService @Inject constructor(
    private val client: ImperfectPairCreateClient
) {
    suspend fun createImperfectPair(
        token: String,
        request: ImperfectPairCreateRequest
    ): Response<List<ApiResponse>> {
        return withContext(Dispatchers.IO) {
            client.createImperfectPair(token, request)
        }
    }
}
