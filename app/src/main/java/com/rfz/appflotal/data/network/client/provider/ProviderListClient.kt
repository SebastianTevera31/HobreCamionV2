package com.rfz.appflotal.data.network.client.provider

import com.rfz.appflotal.data.model.provider.response.ProviderListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ProviderListClient {


    @GET("api/Catalog/ProviderList")
    suspend fun doProviderList( @Header("Authorization") token: String,@Query("id_typeProvider") id_typeProvider: Int): Response<List<ProviderListResponse>>
}