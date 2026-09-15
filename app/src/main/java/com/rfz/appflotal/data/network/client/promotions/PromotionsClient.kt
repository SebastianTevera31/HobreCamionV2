package com.rfz.appflotal.data.network.client.promotions

import com.rfz.appflotal.data.model.promotions.DiscountsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PromotionsService {
    @GET("api/PromotionsAndDiscounts/GetDiscounts")
    suspend fun getDiscounts(
        @Header("Authorization") token: String,
        @Query("search") search: String = "",
        @Query("store") store: String = "",
        @Query("limit") limit: Int = 10,
        @Query("pagesPerStore") pagesPerStore: Int = 20
    ): Response<DiscountsResponse>
}
