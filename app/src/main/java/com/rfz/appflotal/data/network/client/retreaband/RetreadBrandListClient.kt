package com.rfz.appflotal.data.network.client.retreaband

import com.rfz.appflotal.data.model.provider.response.ProviderListResponse
import com.rfz.appflotal.data.model.retreadbrand.response.RetreadBrandListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RetreadBrandListClient {

    @GET("api/Catalog/RetreadBrandList")
    suspend fun doRetreadBrandList(@Header("Authorization") token: String): Response<List<RetreadBrandListResponse>>
}