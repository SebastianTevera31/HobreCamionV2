package com.rfz.appflotal.data.network.client.originaldesign

import com.rfz.appflotal.data.model.originaldesign.response.OriginalDesignByIdResponse
import com.rfz.appflotal.data.model.product.response.ProductByIdResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query



interface OriginalDesignByIdClient {

    @GET("api/Catalog/OriginalDesignById")
    suspend fun doOriginalDesignById(
        @Query("id_originalDesign") id_originalDesign: Int,
        @Header("Authorization") token: String
    ): Response<List<OriginalDesignByIdResponse>>
}