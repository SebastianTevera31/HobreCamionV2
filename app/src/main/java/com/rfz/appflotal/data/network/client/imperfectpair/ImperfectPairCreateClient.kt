package com.rfz.appflotal.data.network.client.imperfectpair

import com.rfz.appflotal.data.model.imperfectpair.ApiResponse
import com.rfz.appflotal.data.model.imperfectpair.dto.ImperfectPairCreateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ImperfectPairCreateClient {
    @POST("api/ImperfectPair/createImperfectPair")
    suspend fun createImperfectPair(
        @Header("Authorization") token: String,
        @Body request: ImperfectPairCreateRequest
    ): Response<List<ApiResponse>>
}
