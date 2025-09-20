package com.rfz.appflotal.data.network.client.imperfectpair

import com.rfz.appflotal.data.model.imperfectpair.ImperfectPairResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ImperfectPairClient {

    @GET("api/ImperfectPair/ImperfectPair")
    suspend fun getImperfectPairs(
        @Header("Authorization") token: String
    ): Response<List<ImperfectPairResponse>>
}
