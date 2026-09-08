package com.rfz.appflotal.data.network.service.promotions

import com.rfz.appflotal.data.model.promotions.DiscountsResponse
import com.rfz.appflotal.data.network.client.promotions.PromotionsService
import com.rfz.appflotal.data.network.networkRequestHelper
import javax.inject.Inject

class RemotePromotionsDataSource @Inject constructor(
    private val promotionsClient: PromotionsService
) {

    suspend fun getDiscounts(
        token: String,
        search: String = "",
        store: String = "",
        limit: Int = 50,
        pagesPerStore: Int = 20
    ): Result<DiscountsResponse> = networkRequestHelper {
        promotionsClient.getDiscounts(
            token = "Bearer $token",
            search = search,
            store = store,
            limit = limit,
            pagesPerStore = pagesPerStore
        )
    }
}
