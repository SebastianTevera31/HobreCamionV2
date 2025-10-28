package com.rfz.appflotal.data.network.client.retreaddesign

import com.rfz.appflotal.data.model.retreaddesing.response.RetreadDesignListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface RetreadDesignListClient {

    @GET("api/Catalog/RetreadDesignList")
    suspend fun doRetreadDesignList(@Header("Authorization") token: String): Response<List<RetreadDesignListResponse>>
}