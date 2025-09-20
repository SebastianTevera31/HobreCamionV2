package com.rfz.appflotal.data.network.client.retreaddesign

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.provider.response.ProviderListResponse
import com.rfz.appflotal.data.model.retreaddesing.response.RetreadDesignResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RetreadDesignListClient {

    @GET("api/Catalog/RetreadDesignList")
    suspend fun doRetreadDesignList(@Header("Authorization") token: String): Response<List<RetreadDesignResponse>>
}