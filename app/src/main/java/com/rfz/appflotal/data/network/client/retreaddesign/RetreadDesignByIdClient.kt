package com.rfz.appflotal.data.network.client.retreaddesign

import com.rfz.appflotal.data.model.retreaddesing.response.RetreadDesignByIdResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RetreadDesignByIdClient {
    @GET("api/Catalog/RetreadDesignById")
    suspend fun onRetreadDesignById(
        @Header("Authorization") token: String,
        @Query("id_retreadDesign") id: Int
    ): Result<RetreadDesignByIdResponse>
}