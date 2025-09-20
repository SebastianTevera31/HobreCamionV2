package com.rfz.appflotal.data.network.client.imperfectpair

import com.rfz.appflotal.data.model.imperfectpair.ApiResponse
import com.rfz.appflotal.data.model.imperfectpair.dto.ImperfectPairUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT


interface ImperfectPairUpdateClient {
    @PUT("api/ImperfectPair/updateImperfectPair")
    suspend fun updateImperfectPair(
        @Header("Authorization") token: String,
        @Body request: ImperfectPairUpdateRequest
    ): Response<List<ApiResponse>>
}
